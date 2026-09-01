package de.malteans.digishelf.core.domain

data class Book(
    val id: Long = 0L,
    val title: String,
    val author: String,
    val isbn: String,
    val description: String = "",
    val onlineDescription: String? = null,
    val imageUrl: String = "",
    val pageCount: Int? = null,

    val price: Double? = null,
    val currency: String? = null,

    val bookSeries: BookSeries? = null,

    val rating: Int? = null,
    val tensionLevel: Int? = null,
    val spiceLevel: Int? = null,
    val emotionLevel: Int? = null,
    val chapterLength: Int? = null,
    val endingRating: Int? = null,
    val plotRating: Int? = null,
    val readStatus: Boolean = false,
    val readingTime: Int? = null,
    val possessionStatus: Boolean = false,
    val eBookStatus: Boolean = false,
    val deletedSince: Long = 0,
)