package de.malteans.digishelf.series.presentation.overview

import de.malteans.digishelf.core.domain.BookSeries

sealed interface SeriesOverviewAction {
    data object OnOpenDrawer: SeriesOverviewAction

    data class SearchQueryChanged(val searchQuery: String): SeriesOverviewAction
    data class SubmitSeries(val series: BookSeries): SeriesOverviewAction
    data class DeleteSeries(val series: BookSeries): SeriesOverviewAction
}
