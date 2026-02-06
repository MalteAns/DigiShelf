package de.malteans.digishelf.core.data.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import de.malteans.digishelf.core.data.database.BookDatabase

val BookDatabase.Companion.MIGRATION1_2: Migration
    get() = object : Migration(1, 2) {
        override fun migrate(connection: SQLiteConnection) {
            connection.execSQL(
                """
                ALTER TABLE books ADD COLUMN ebookStatus INTEGER NOT NULL DEFAULT 0
                """.trimIndent()
            )
        }
    }