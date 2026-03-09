package de.malteans.digishelf.core.presentation.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.digishelf.core.domain.BookRepository
import de.malteans.digishelf.core.domain.SortType
import de.malteans.digishelf.core.presentation.overview.components.SearchType
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
class OverviewViewModel (
    private val repository: BookRepository
): ViewModel() {

    private var _searchJob: Job? = null

    private val _state = MutableStateFlow(OverviewState())
    
    val state = _state
        .onStart {
            observeSearchQuery()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            OverviewState()
        )

    fun onAction(event: OverviewAction) {
        when (event) {
            is OverviewAction.ChangeFilterList -> {
                _state.update { it.copy(
                    possessionStatus = event.possessionStatus,
                    readStatus = event.readStatus,
                    eBookStatus = event.eBookStatus,
                    sortType = event.sortType,
                    searchType = event.searchType
                ) }
            }
            is OverviewAction.ResetFilter -> {
                _state.update { it.copy(
                    possessionStatus = null,
                    readStatus = null,
                    eBookStatus = null,
                    sortType = SortType.TITLE,
                    searchType = SearchType.TITLE
                ) }
            }
            is OverviewAction.DeleteBook -> {
                viewModelScope.launch(Dispatchers.IO) {
                    repository.trashBook(event.book.id)
                }
            }
            is OverviewAction.AddBook -> {
                viewModelScope.launch(Dispatchers.IO) {
                    repository.addBook(event.book)
                }
            }
            is OverviewAction.SearchQueryChanged -> {
                _state.update { it.copy(
                    searchQuery = event.searchQuery
                ) }
            }
            is OverviewAction.RestoreBook -> {
                viewModelScope.launch(Dispatchers.IO) {
                    repository.restoreBook(event.book.id)
                }
            }
            is OverviewAction.ReloadBooks -> {
                _state.update { it.copy(
                    isLoading = true
                ) }
                _searchJob?.cancel()
                _searchJob = fetchLocalBooks(
                    with(state.value) { SearchOptions(
                        searchQuery = searchQuery,
                        possessionStatus = possessionStatus,
                        readStatus = readStatus,
                        eBookStatus = eBookStatus,
                        sortType = sortType,
                        searchType = searchType
                    ) }
                )
            }

            else -> TODO("This should not happen")
        }
    }

    private fun observeSearchQuery() {
        state
            .map { SearchOptions(
                searchQuery = it.searchQuery,
                possessionStatus = it.possessionStatus,
                readStatus = it.readStatus,
                eBookStatus = it.eBookStatus,
                sortType = it.sortType,
                searchType = it.searchType
            ) }
            .distinctUntilChanged()
            .onEach { options ->
                _searchJob?.cancel()
                _searchJob = fetchLocalBooks(options)
            }
            .launchIn(viewModelScope)
    }

    private fun fetchLocalBooks(options: SearchOptions) = viewModelScope.launch(Dispatchers.IO) {
        _state.update { it.copy(isLoading = true) }
        val books = repository.fetchLocalBooks(
            titleQuery = if (options.searchType == SearchType.TITLE) options.searchQuery else "",
            authorQuery = if (options.searchType == SearchType.AUTHOR) options.searchQuery else "",
            isbnQuery = if (options.searchType == SearchType.ISBN) options.searchQuery else "",
            possessionStatus = options.possessionStatus,
            readStatus = options.readStatus,
            eBookStatus = options.eBookStatus,
            sortBy = options.sortType,
        )
        _state.update { it.copy(books = books, isLoading = false) }
    }
}

private data class SearchOptions(
    val searchQuery: String,
    val possessionStatus: Boolean?,
    val readStatus: Boolean?,
    val eBookStatus: Boolean?,
    val sortType: SortType,
    val searchType: SearchType
)