package de.malteans.digishelf.core.data.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import de.malteans.digishelf.core.data.database.BookDatabase

val BookDatabase.Companion.MIGRATION4_5: Migration
    get() = object : Migration(4, 5) {
        override fun migrate(connection: SQLiteConnection) {
            connection.execSQL("ALTER TABLE books ADD COLUMN chapterLength INTEGER DEFAULT NULL")
            connection.execSQL("ALTER TABLE books ADD COLUMN endingRating INTEGER DEFAULT NULL")
            connection.execSQL("ALTER TABLE books ADD COLUMN plotRating INTEGER DEFAULT NULL")
        }
    }
