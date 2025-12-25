package de.malteans.digishelf.core.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.digishelf.core.domain.Book
import de.malteans.digishelf.core.domain.BookRepository
import de.malteans.digishelf.core.domain.errorHandling.DataError
import de.malteans.digishelf.core.domain.errorHandling.Result
import de.malteans.digishelf.core.presentation.add.isIsbnFormat
import de.malteans.digishelf.export.domain.ExportRepository
import de.malteans.digishelf.export.presentation.ImportFileType
import de.malteans.digishelf.navigation.CurScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel (
    private val repository: BookRepository,
    private val exportRepository: ExportRepository,
): ViewModel() {

    private val _allBooks = repository.queryBooks(includeDeleted = true)

    private val _allBookSeries = repository.querySeries()

    private val _state = MutableStateFlow(SettingsState())

    val state = combine(
        _state,
        _allBooks,
        _allBookSeries
    ) { state, allBooks, allBookSeries ->
        val trashedBooks = allBooks.filter { it.deletedSince != 0L }
        state.copy(
            allBooks = allBooks,
            allBookSeries = allBookSeries,
            trashedBooks = trashedBooks,
            trashIsEmpty = trashedBooks.isEmpty()
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SettingsState()
    )

    fun onAction(action: SettingsAction) {
        when(action) {
            SettingsAction.SettingsOpened -> {
                _state.update {
                    it.copy(
                        curScreen = CurScreen.Settings
                    )
                }
            }
            SettingsAction.OnTrashClicked -> {
                _state.update {
                    it.copy(
                        curScreen = CurScreen.Trash
                    )
                }
            }
            is SettingsAction.OnTrashRestoreClicked -> {
                viewModelScope.launch(Dispatchers.IO) {
                    repository.restoreBook(action.book.id)
                }
            }
            // Trash
            is SettingsAction.OnTrashDeleteClicked -> {
                viewModelScope.launch(Dispatchers.IO) {
                    repository.deleteBook(action.book.id)
                }
            }
            SettingsAction.OnTrashDeleteAllClicked -> {
                viewModelScope.launch(Dispatchers.IO) {
                    repository.emptyTrash()
                }
            }
            SettingsAction.OnTrashRestoreAllClicked -> {
                viewModelScope.launch(Dispatchers.IO) {
                    repository.restoreAllBooks()
                }
            }
            // Import/Export
            SettingsAction.OnExportClicked -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _state.update { it.copy(
                        exportData = exportRepository.export()
                    ) }
                }
            }
            // New import branch: fileContent now contains the CSV text.
            is SettingsAction.OnImport -> {
                if (action.fileType == ImportFileType.JSON) {
                    viewModelScope.launch(Dispatchers.IO) {
                        exportRepository.import(action.fileContent)
                    }
                } else { // Legacy support
                    try {
                        viewModelScope.launch(Dispatchers.IO) {
                            val previousBooks = state.value.allBooks
                            val newBooks = readCsv(action.fileContent)
                            newBooks.forEach { book ->
                                if ((book.isbn.isIsbnFormat() && previousBooks?.find { it.isbn == book.isbn } != null))
                                    return@forEach
                                if (book.title.isNotBlank() && book.author.isNotBlank() && previousBooks?.find{ it.title == book.title && it.author == book.author } != null)
                                    return@forEach

                                repository.addBook(book.copy(id = 0L))
                            }
                        }
                    } catch(e: Exception) {
                        // Error handling (update state with error message as needed)
                    }
                }
            }
            SettingsAction.ResetExportData -> {
                _state.update { it.copy(
                    exportData = null
                ) }
            }
            is SettingsAction.OnCloudCompleteClicked -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _state.update { it.copy(
                        cloudCompletionInProgress = true
                    ) }
                    state.value.allBooks?.forEach { book ->
                        val cloudBookResult = if (book.isbn.isIsbnFormat()) {
                            repository.fetchBookFromRemote(isbn = book.isbn)
                        } else if (book.author.isNotBlank() && book.title.isNotBlank()) {
                            repository.fetchBookFromRemote(
                                title = book.title,
                                author = book.author,
                            )
                        } else {
                            Result.Error(
                                DataError.Remote.INVALIDE_QUERY
                            )
                        }

                        when (cloudBookResult) {
                            is Result.Success -> {
                                var newBook = book.copy()
                                val cloudBook = cloudBookResult.data

                                if (!newBook.isbn.isIsbnFormat())
                                    newBook = newBook.copy(isbn = cloudBook.isbn)
                                if (newBook.title.isEmpty() || cloudBook.title.contains(book.title))
                                    newBook = newBook.copy(title = cloudBook.title)
                                if (newBook.author.isEmpty() || cloudBook.author.contains(book.author))
                                    newBook = newBook.copy(author = cloudBook.author)
                                if (newBook.pageCount == null || newBook.pageCount == 0)
                                    newBook = newBook.copy(pageCount = cloudBook.pageCount)
                                if (newBook.price == null || newBook.price == 0.0)
                                    newBook = newBook.copy(price = cloudBook.price, currency = cloudBook.currency)

                                newBook = newBook.copy(
                                    onlineDescription = cloudBook.onlineDescription,
                                    imageUrl = cloudBook.imageUrl,
                                )

                                repository.updateBook(newBook)
                            }
                            is Result.Error -> {
                                // Handle error (e.g., show a message to the user)
                            }
                        }
                    }
                    _state.update { it.copy(
                        cloudCompletionInProgress = false,
                        cloudCompletionDone = true
                    ) }
                    delay(5000L)
                    _state.update { it.copy(
                        cloudCompletionDone = false
                    ) }
                }
            }

            else -> throw NotImplementedError("Action '$action' not implemented.")
        }
    }

    private fun readCsv(content: String): List<Book> {
        val lines = content.split("\n")
        val books = mutableListOf<Book>()

        for (line in lines.subList(1, lines.size - 1)) {
            val values = line.substring(1, line.length - 1).split("\",\"")

            if (values.size == 10) {
                val book = Book(
                    title = values[0],
                    author = values[1],
                    isbn = values[2],
                    possessionStatus = values[3].toBoolean(),
                    readStatus = values[4].toBoolean(),
                    rating = values[5].toIntOrNull(),
                    description = values[6],
                    deletedSince = values[7].toLong(),
                    bookSeries = null,
                    readingTime = values[9].toIntOrNull()
                )
                books.add(book)
            }
        }
        return books
    }
}
