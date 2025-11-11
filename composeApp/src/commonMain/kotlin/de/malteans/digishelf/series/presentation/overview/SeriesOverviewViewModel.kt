package de.malteans.digishelf.series.presentation.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.digishelf.core.domain.BookRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SeriesOverviewViewModel (
    private val repository: BookRepository
): ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    private val _seriesList = _searchQuery
        .flatMapLatest { searchQuery ->
            repository.querySeries(
                titleQuery = searchQuery
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _state = MutableStateFlow(SeriesOverviewState())

    val state = combine(_state, _seriesList) { state, books ->
        state.copy(
            seriesList = books,
            searchQuery = _searchQuery.value
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SeriesOverviewState())

    fun onAction(event: SeriesOverviewAction) {
        when(event) {
            is SeriesOverviewAction.SearchQueryChanged -> {
                _searchQuery.value = event.searchQuery
            }
            is SeriesOverviewAction.SubmitSeries -> {
                viewModelScope.launch {
                    repository.updateSeries(event.series)
                }
            }
            is SeriesOverviewAction.DeleteSeries -> {
                viewModelScope.launch {
                    repository.deleteSeries(event.series.id)
                }
            }

            else -> TODO("This should not happen")
        }
    }
}