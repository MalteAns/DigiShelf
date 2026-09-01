package de.malteans.digishelf.core.presentation.details

import de.malteans.digishelf.core.domain.Book
import de.malteans.digishelf.core.domain.BookSeries

data class DetailsState(
    val bookId: Long? = null,
    val book: Book? = null,

    val imageUrl: String = "",
    val imageUrlChanged: Boolean = false,

    val isbn: String = "",
    val isbnChanged: Boolean = false,
    val title: String = "",
    val titleChanged: Boolean = false,
    val author: String = "",
    val authorChanged: Boolean = false,
    val rating: Int = 0,
    val ratingChanged: Boolean = false,
    val tensionLevel: Int = 0,
    val tensionLevelChanged: Boolean = false,
    val spiceLevel: Int = 0,
    val spiceLevelChanged: Boolean = false,
    val emotionLevel: Int = 0,
    val emotionLevelChanged: Boolean = false,
    val chapterLength: Int = 0,
    val chapterLengthChanged: Boolean = false,
    val endingRating: Int = 0,
    val endingRatingChanged: Boolean = false,
    val plotRating: Int = 0,
    val plotRatingChanged: Boolean = false,
    val pageCount: Int? = null,
    val pagesChanged: Boolean = false,
    val price: Double? = null,
    val currency: String? = null,
    val priceChanged: Boolean = false,
    val possessionStatus: Boolean = false,
    val ebookStatus: Boolean = false,
    val readStatus: Boolean = false,
    val statusChanged: Boolean = false,
    val readingTime: Int? = null,
    val readingTimeChanged: Boolean = false,
    val description: String = "",
    val descriptionChanged: Boolean = false,

    val bookSeriesList: List<BookSeries> = emptyList(),
    val series: BookSeries? = null,
    val seriesId: Long? = null,
    val seriesChanged: Boolean = false,

    val somethingChanged: Boolean = false,

    val isEditing: Boolean = false,
    val isDoubleIsbn: Boolean = false,



    val onlineDescription: String? = null,
)
