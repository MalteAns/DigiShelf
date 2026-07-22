package de.malteans.digishelf.core.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "books",
    foreignKeys = [
        ForeignKey(
            entity = BookSeriesEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookSeriesId"],
            onDelete = ForeignKey.SET_NULL,
        )
    ],
    indices = [
        Index("bookSeriesId")
    ]
)
data class BookEntity (
    @PrimaryKey(autoGenerate = true)
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

    val bookSeriesId: Long? = null,

    val rating: Int? = null,
    val tensionLevel: Int? = null,
    val spiceLevel: Int? = null,
    val emotionLevel: Int? = null,
    val readStatus: Boolean = false,
    val readingTime: Int? = null,
    val possessionStatus: Boolean = false,
    val eBookStatus: Boolean = false,
    val deletedSince: Long = 0,
)