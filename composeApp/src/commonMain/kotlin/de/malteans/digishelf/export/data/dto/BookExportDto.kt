package de.malteans.digishelf.export.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class BookExportDto (
    val title: String,
    val author: String,
    val isbn: String,
    val description: String = "",
    val onlineDescription: String? = null,
    val imageUrl: String = "",
    val pageCount: Int? = null,

    val price: Double? = null,
    val currency: String? = null,

    val bookSeriesId: Long? = null,

    val rating: Int? = null,
    val tensionLevel: Int? = null,
    val spiceLevel: Int? = null,
    val emotionLevel: Int? = null,
    val readStatus: Boolean = false,
    val readingTime: Int? = null,
    val possessionStatus: Boolean = false,
    val deletedSince: Long = 0,
)
