package de.malteans.digishelf.core.presentation.add

import de.malteans.digishelf.core.domain.BookSeries

sealed class AddAction {
    data object OnShowOverview: AddAction()
    data object OnDismissError: AddAction()
    data object OnShowIncompleteError: AddAction()
    data object OnDismissIncompleteError: AddAction()
    data object OnScan: AddAction()

    data class OnShowBookDetail(val bookId: Long): AddAction()

    data class OnAutoComplete(
        val isbn: String? = null,
        val title: String? = null,
        val author: String? = null
    ): AddAction()

    data class OnTitleChanged(val title: String): AddAction()
    data class OnAuthorChanged(val author: String): AddAction()
    data class OnIsbnChanged(val isbn: String): AddAction()
    data class OnPossessionStatusChanged(val possessionStatus: Boolean): AddAction()
    data class OnReadStatusChanged(val readStatus: Boolean): AddAction()
    data class OnEbookStatusChanged(val ebookStatus: Boolean): AddAction()
    data class OnRatingChanged(val rating: Int): AddAction()
    data class OnBookSeriesChanged(val bookSeries: BookSeries?): AddAction()
    data class OnPagesChanged(val pages: String): AddAction()
    data class OnPriceChanged(val price: String): AddAction()
    data class OnImageUrlChanged(val imageUrl: String): AddAction()

    object AddBook: AddAction()
    object ClearFields: AddAction()
}