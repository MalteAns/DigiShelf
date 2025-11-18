package de.malteans.digishelf.series.presentation.details

import de.malteans.digishelf.core.domain.BookSeries

data class SeriesDetailsState(
    val isLoading: Boolean = true,

    val series: BookSeries? = null,
)
