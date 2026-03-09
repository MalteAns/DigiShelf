package de.malteans.digishelf.core.presentation.overview

import de.malteans.digishelf.core.domain.Book
import de.malteans.digishelf.core.domain.SortType
import de.malteans.digishelf.core.presentation.overview.components.SearchType

sealed interface OverviewAction {
    data object OnOpenDrawer: OverviewAction
    data object OnAddBook: OverviewAction
    data object OnAddBookWithScanner: OverviewAction
    data class OnOpenBook(val bookId: Long): OverviewAction

    data class ChangeFilterList(val possessionStatus: Boolean?, val readStatus: Boolean?, val eBookStatus: Boolean?,
        val sortType: SortType, val searchType: SearchType,
    ): OverviewAction

    data object ReloadBooks: OverviewAction

    data object ResetFilter: OverviewAction
    data class DeleteBook(val book: Book): OverviewAction
    data class AddBook(val book: Book): OverviewAction
    data class RestoreBook(val book: Book): OverviewAction
    data class SearchQueryChanged(val searchQuery: String): OverviewAction
}