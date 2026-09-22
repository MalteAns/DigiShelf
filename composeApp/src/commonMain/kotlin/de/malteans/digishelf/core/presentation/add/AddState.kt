package de.malteans.digishelf.core.presentation.add

import de.malteans.digishelf.core.domain.Book
import de.malteans.digishelf.core.presentation.components.UiText
import digishelf.composeapp.generated.resources.Res
import digishelf.composeapp.generated.resources.error
import digishelf.composeapp.generated.resources.error_unknown

data class AddState(
    val isLoading: Boolean = false,
    val addedBookId: Long? = null,

    val showError: Boolean = false,
    val errorTitle: UiText = UiText.StringResourceId(Res.string.error),
    val errorMessage: UiText = UiText.StringResourceId(Res.string.error_unknown),

    val title: String = "",
    val author: String = "",
    val isbn: String = "",
    
    // ISBN duplicate check
    val isDuplicateIsbn: Boolean = false,
    
    // Search functionality
    val searchResults: List<Book> = emptyList(),
    val showSearchBottomSheet: Boolean = false,
    val isSearching: Boolean = false,
    val pendingBook: Book? = null,
)
