package de.malteans.digishelf.core.presentation.add

import de.malteans.digishelf.core.domain.Book

sealed class AddAction {
    data object OnShowOverview: AddAction()
    data object OnDismissError: AddAction()
    data object OnScan: AddAction()

    data class OnShowBookDetail(val bookId: Long): AddAction()

    data class OnTitleChanged(val title: String): AddAction()
    data class OnAuthorChanged(val author: String): AddAction()
    data class OnIsbnChanged(val isbn: String): AddAction()

    // Search actions
    data object OnSearchClicked: AddAction()
    data class OnSearchResultSelected(val book: Book): AddAction()
    data object OnDismissSearchBottomSheet: AddAction()
    
    // Duplicate handling
    data object OnConfirmAddDuplicate: AddAction()
    data object OnDismissDuplicateDialog: AddAction()

    object AddBook: AddAction()
    object ClearFields: AddAction()
}