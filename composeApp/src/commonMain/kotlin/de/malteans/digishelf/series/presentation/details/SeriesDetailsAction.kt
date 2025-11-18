package de.malteans.digishelf.series.presentation.details

sealed interface SeriesDetailsAction {
    data class SetSeriesId(val seriesId: Long) : SeriesDetailsAction

    data object NavigateBack : SeriesDetailsAction
    data class NavigateToBookDetails(val bookId: Long) : SeriesDetailsAction
}