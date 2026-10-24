package de.malteans.digishelf.core.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.digishelf.core.domain.Book
import de.malteans.digishelf.core.domain.BookRepository
import de.malteans.digishelf.core.domain.errorHandling.onError
import de.malteans.digishelf.core.domain.errorHandling.onSuccess
import de.malteans.digishelf.core.presentation.components.UiText
import de.malteans.digishelf.core.presentation.components.toUiText
import digishelf.composeapp.generated.resources.Res
import digishelf.composeapp.generated.resources.error
import digishelf.composeapp.generated.resources.error_completion
import digishelf.composeapp.generated.resources.error_unknown
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddViewModel (
    private val repository: BookRepository
): ViewModel() {

    private val _state = MutableStateFlow(AddState())

    val state = _state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AddState())

    fun onAction(action: AddAction) {
        when(action) {
            // OnChanged actions ------------------------------------------------------------------
            is AddAction.OnTitleChanged -> {
                _state.update {
                    it.copy(
                        title = action.title
                    )
                }
            }
            is AddAction.OnAuthorChanged -> {
                _state.update {
                    it.copy(author = action.author)
                }
            }
            is AddAction.OnIsbnChanged -> {
                // Clean ISBN input by removing non-digit characters
                val cleanedIsbn = action.isbn.replace(Regex("\\D"), "")
                _state.update {
                    it.copy(
                        isbn = cleanedIsbn
                    )
                }
                
                // Check for duplicate if ISBN is valid
                if (cleanedIsbn.isNotBlank() && cleanedIsbn.isIsbnFormat()) {
                    viewModelScope.launch(Dispatchers.IO) {
                        checkIsbnDuplicate(cleanedIsbn)
                    }
                } else {
                    _state.update {
                        it.copy(isDuplicateIsbn = false)
                    }
                }
            }
            
            // Search actions ----------------------------------------------------------------
            is AddAction.OnSearchClicked -> {
                val state = _state.value
                
                // Check if search conditions are met
                val titleValid = state.title.length >= 10
                val isbnValid = state.isbn.isIsbnFormat()
                val titleAndAuthorValid = state.title.length >= 6 && state.author.length >= 6
                
                if (titleValid || isbnValid || titleAndAuthorValid) {
                    // Open bottom sheet and start searching
                    _state.update {
                        it.copy(
                            showSearchBottomSheet = true,
                            isSearching = true,
                            searchResults = emptyList()
                        )
                    }
                    
                    viewModelScope.launch(Dispatchers.IO) {
                        try {
                            val result = repository.fetchBookFromRemote(
                                isbn = if (state.isbn.isIsbnFormat()) state.isbn else null,
                                title = if (state.title.isNotBlank()) state.title else null,
                                author = if (state.author.isNotBlank()) state.author else null,
                                maxResults = 10
                            )
                            
                            result.onSuccess { books ->
                                _state.update {
                                    it.copy(
                                        searchResults = books,
                                        isSearching = false
                                    )
                                }
                            }.onError { error ->
                                _state.update {
                                    it.copy(
                                        errorTitle = UiText.StringResourceId(Res.string.error_completion),
                                        errorMessage = error.toUiText(),
                                        showError = true,
                                        isSearching = false
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            _state.update {
                                it.copy(
                                    errorTitle = UiText.StringResourceId(Res.string.error),
                                    errorMessage = UiText.StringResourceId(Res.string.error_unknown),
                                    showError = true,
                                    isSearching = false
                                )
                            }
                        }
                    }
                }
            }
            
            is AddAction.OnSearchResultSelected -> {
                val book = action.book
                
                viewModelScope.launch(Dispatchers.IO) {
                    // Check if ISBN already exists in database
                    val existingBook = repository.getBookByIsbn(book.isbn).first()
                    
                    if (existingBook != null) {
                        // Duplicate ISBN - show dialog
                        _state.update {
                            it.copy(
                                pendingBook = book
                            )
                        }
                    } else {
                        // Not a duplicate - add book directly
                        val bookId = repository.addBook(book)
                        _state.update {
                            it.copy(
                                addedBookId = bookId,
                                showSearchBottomSheet = false,
                                isSearching = false,
                                searchResults = emptyList(),
                                title = "",
                                author = "",
                                isbn = ""
                            )
                        }
                    }
                }
            }
            
            is AddAction.OnDismissSearchBottomSheet -> {
                _state.update {
                    it.copy(
                        showSearchBottomSheet = false,
                        isSearching = false,
                        searchResults = emptyList()
                    )
                }
            }
            
            // Duplicate handling ------------------------------------------------------------
            is AddAction.OnConfirmAddDuplicate -> {
                val pendingBook = _state.value.pendingBook
                if (pendingBook != null) {
                    viewModelScope.launch(Dispatchers.IO) {
                        val bookId = repository.addBook(pendingBook)
                        _state.update {
                            it.copy(
                                addedBookId = bookId,
                                pendingBook = null,
                                showSearchBottomSheet = false,
                                title = "",
                                author = "",
                                isbn = ""
                            )
                        }
                    }
                }
            }
            
            is AddAction.OnDismissDuplicateDialog -> {
                _state.update {
                    it.copy(
                        pendingBook = null
                    )
                }
            }
            
            // Other actions ----------------------------------------------------------------------
            is AddAction.AddBook -> {
                _state.update {
                    it.copy(
                        isLoading = true
                    )
                }

                val title = _state.value.title.trim()
                if (title.isBlank()) return
                val author = _state.value.author.trim()
                val isbn = _state.value.isbn.trim()

                val book = Book(
                    title = title,
                    author = author,
                    isbn = isbn,
                )

                viewModelScope.launch(Dispatchers.IO) {
                    val bookId = repository.addBook(book)
                    _state.update {
                        it.copy(
                            addedBookId = bookId,
                            isLoading = false,
                            title = "",
                            author = "",
                            isbn = ""
                        )
                    }
                }
            }
            
            is AddAction.ClearFields -> {
                _state.update {
                    it.copy(
                        title = "",
                        author = "",
                        isbn = "",
                        addedBookId = null
                    )
                }
            }
            
            is AddAction.OnShowBookDetail -> {
                // Handled by the screen
            }
            is AddAction.OnShowOverview -> {
                // Handled by the screen
            }
            is AddAction.OnScan -> {
                // Handled by the screen
            }
            
            // Error handling ---------------------------------------------------------------------
            is AddAction.OnDismissError -> {
                _state.update {
                    it.copy(
                        showError = false,
                        errorTitle = UiText.StringResourceId(Res.string.error),
                        errorMessage = UiText.StringResourceId(Res.string.error_unknown)
                    )
                }
            }
        }
    }

    private suspend fun checkIsbnDuplicate(isbn: String) {
        if (isbn.isBlank() || !isbn.isIsbnFormat()) {
            _state.update {
                it.copy(isDuplicateIsbn = false)
            }
            return
        }
        
        val existingBooks = repository.queryBooks(isbnQuery = isbn).first()
        val isDuplicate = existingBooks.any { it.isbn == isbn }
        
        _state.update {
            it.copy(isDuplicateIsbn = isDuplicate)
        }
    }
}

fun String.isDigitsOnly() : Boolean {
    return all { it.isDigit() }
}

fun String.isIsbnFormat() : Boolean {
    return length == 13 && isDigitsOnly() && startsWith("978")
}
