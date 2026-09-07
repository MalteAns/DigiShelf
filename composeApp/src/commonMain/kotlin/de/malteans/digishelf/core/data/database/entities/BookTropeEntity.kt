package de.malteans.digishelf.core.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "book_tropes",
    primaryKeys = ["bookId", "tropeId"],
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = TropeEntity::class,
            parentColumns = ["id"],
            childColumns = ["tropeId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index(value = ["bookId"]),
        Index(value = ["tropeId"]),
    ]
)
data class BookTropeEntity(
    val bookId: Long,
    val tropeId: Long,
)