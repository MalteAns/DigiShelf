package de.malteans.digishelf.series.presentation.overview

import de.malteans.digishelf.core.domain.BookSeries

data class SeriesOverviewState(
    val seriesList: List<BookSeries> = emptyList(),
    val searchQuery: String = "",
)
