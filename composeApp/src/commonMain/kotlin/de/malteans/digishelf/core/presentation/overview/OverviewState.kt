package de.malteans.digishelf.core.presentation.overview

import de.malteans.digishelf.core.domain.Book
import de.malteans.digishelf.core.domain.SortType
import de.malteans.digishelf.core.presentation.overview.components.SearchType

data class OverviewState(
    val isLoading: Boolean = true,

    val books: List<Book> = emptyList(),
    val sortType: SortType = SortType.TITLE,
    val possessionStatus: Boolean? = null,
    val readStatus: Boolean? = null,
    val eBookStatus: Boolean? = null,

    val searchQuery: String = "",
    val searchType: SearchType = SearchType.TITLE,
)
