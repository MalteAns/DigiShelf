package de.malteans.digishelf.series.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.digishelf.core.domain.BookRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
class SeriesDetailsViewModel(
    private val repository: BookRepository,
) : ViewModel() {

    private val _seriesId = MutableStateFlow<Long?>(null)
    private val _series = _seriesId
        .flatMapLatest { it?.let { seriesId ->
            repository.getSeries(seriesId)
        } ?: flowOf(null) }

    private val _state = MutableStateFlow(SeriesDetailsState())

    val state = combine(
        _state,
        _series
    ) { state, series ->
        state.copy(
            isLoading = series == null, // TODO: Maybe change this logic
            series = series,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SeriesDetailsState()
    )

    fun onAction(action: SeriesDetailsAction) {
        when (action) {
            is SeriesDetailsAction.SetSeriesId -> _seriesId.update { action.seriesId }

            else -> throw NotImplementedError("Action '$action' not implemented.")
        }
    }
}