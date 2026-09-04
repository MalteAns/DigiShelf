package de.malteans.digishelf.core.data.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tropes",
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class TropeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
)