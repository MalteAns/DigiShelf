package de.malteans.digishelf.core.data.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import de.malteans.digishelf.core.data.database.BookDatabase

val BookDatabase.Companion.MIGRATION3_4: Migration
    get() = object : Migration(3, 4) {
        override fun migrate(connection: SQLiteConnection) {
            connection.execSQL("ALTER TABLE books ADD COLUMN tensionLevel INTEGER DEFAULT NULL")
            connection.execSQL("ALTER TABLE books ADD COLUMN spiceLevel INTEGER DEFAULT NULL")
            connection.execSQL("ALTER TABLE books ADD COLUMN emotionLevel INTEGER DEFAULT NULL")
        }
    }
