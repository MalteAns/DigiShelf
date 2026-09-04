package de.malteans.digishelf.core.presentation.details

import de.malteans.digishelf.core.domain.BookSeries
import de.malteans.digishelf.core.domain.Trope


sealed class DetailsAction {
    data object OnBack: DetailsAction()
    data class OnAuthorSearch(val author: String): DetailsAction()

    data class SetBookId(val bookId: Long): DetailsAction()

    data class IsbnChanged(val isbn: String): DetailsAction()
    data class TitleChanged(val title: String): DetailsAction()
    data class AuthorChanged(val author: String): DetailsAction()
    data class RatingChanged(val rating: Int): DetailsAction()
    data class TensionLevelChanged(val level: Int): DetailsAction()
    data class SpiceLevelChanged(val level: Int): DetailsAction()
    data class EmotionLevelChanged(val level: Int): DetailsAction()
    data class ChapterLengthChanged(val level: Int): DetailsAction()
    data class EndingRatingChanged(val level: Int): DetailsAction()
    data class PlotRatingChanged(val level: Int): DetailsAction()
    data class PageCountChanged(val pages: Int?): DetailsAction()
    data class PriceChanged(val price: Double?, val currency: String = "EUR"): DetailsAction()
    data class StatusChanged(val possessionStatus: Boolean, val readStatus: Boolean, val ebookStatus: Boolean): DetailsAction()
    data class DescriptionChanged(val description: String): DetailsAction()
    data class SeriesChanged(val series: BookSeries?): DetailsAction()
    data class ReadingTimeChanged(val readingTime: Int?): DetailsAction()

    data class ImageUrlChanged(val coverImage: String): DetailsAction()
    data class SetOnlineDescription(val onlineDescription: String): DetailsAction()

    // Trope actions
    data class AddTrope(val trope: Trope): DetailsAction()
    data class RemoveTrope(val trope: Trope): DetailsAction()

    // Favorite character and scene actions
    data class FavoriteCharacterChanged(val character: String?): DetailsAction()
    data class FavoriteSceneChanged(val scene: String?): DetailsAction()

    data object SwitchEditing: DetailsAction()

    data object UpdateBook: DetailsAction()
    data object DeleteBook: DetailsAction()
    data object ResetState: DetailsAction()
}