package de.malteans.digishelf.core.presentation.details

import de.malteans.digishelf.core.domain.BookSeries


sealed class DetailsAction {
    data object OnBack: DetailsAction()
    data class OnAuthorSearch(val author: String): DetailsAction()

    data class SetBookId(val bookId: Long): DetailsAction()

    data class IsbnChanged(val isbn: String): DetailsAction()
    data class TitleChanged(val title: String): DetailsAction()
    data class AuthorChanged(val author: String): DetailsAction()
    data class RatingChanged(val rating: Int): DetailsAction()
    data class PageCountChanged(val pages: Int?): DetailsAction()
    data class PriceChanged(val price: Double?, val currency: String = "EUR"): DetailsAction()
    data class StatusChanged(val possessionStatus: Boolean, val readStatus: Boolean, val ebookStatus: Boolean): DetailsAction()
    data class DescriptionChanged(val description: String): DetailsAction()
    data class SeriesChanged(val series: BookSeries?): DetailsAction()
    data class ReadingTimeChanged(val readingTime: Int?): DetailsAction()

    data class ImageUrlChanged(val coverImage: String): DetailsAction()
    data class SetOnlineDescription(val onlineDescription: String): DetailsAction()

    data object SwitchEditing: DetailsAction()

    data object UpdateBook: DetailsAction()
    data object DeleteBook: DetailsAction()
    data object ResetState: DetailsAction()
}