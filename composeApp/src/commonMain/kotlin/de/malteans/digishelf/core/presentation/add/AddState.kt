package de.malteans.digishelf.core.presentation.add

import de.malteans.digishelf.core.domain.BookSeries
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
    val showIncompleteError: Boolean = false,

    val title: String = "",
    val author: String = "",
    val isbn: String = "",
    val possessionStatus: Boolean = true,
    val readStatus: Boolean = false,
    val eBookStatus: Boolean = false,
    val rating: Int = 0,
    val tensionLevel: Int = 0,
    val spiceLevel: Int = 0,
    val emotionLevel: Int = 0,
    val cameraPermissionGranted: Boolean = false,
    val isScanning: Boolean = false,
    val isDoubleIsbn: Boolean = false,
    val showCompleteWithIsbn: Boolean = false,
    val bookSeries: BookSeries? = null,
    val bookSeriesList: List<BookSeries> = emptyList(),
    val pages: String = "",
    val pagesError: Boolean = false,
    val price: String = "",
    val priceError: Boolean = false,
    val imageUrl: String = "",
)
