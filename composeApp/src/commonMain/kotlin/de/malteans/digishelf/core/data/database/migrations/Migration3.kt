package de.malteans.digishelf.core.data.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import de.malteans.digishelf.core.data.database.BookDatabase

val BookDatabase.Companion.MIGRATION2_3: Migration
    get() = object : Migration(2, 3) {
        override fun migrate(connection: SQLiteConnection) {
            // Renamed book field ebookStatus to eBookStatus
            connection.execSQL("ALTER TABLE books RENAME COLUMN ebookStatus TO eBookStatus")
        }
    }