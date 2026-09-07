package de.malteans.digishelf.core.data.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import de.malteans.digishelf.core.data.database.BookDatabase

val BookDatabase.Companion.MIGRATION5_6: Migration
    get() = object : Migration(5, 6) {
        override fun migrate(connection: SQLiteConnection) {
            // Create tropes table
            connection.execSQL("""
                CREATE TABLE IF NOT EXISTS tropes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    CONSTRAINT unique_name UNIQUE (name)
                )
            """)

            // Create unique index on tropes name (to match the entity definition)
            connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_tropes_name ON tropes(name)")

            // Create book_tropes junction table
            connection.execSQL("""
                CREATE TABLE IF NOT EXISTS book_tropes (
                    bookId INTEGER NOT NULL,
                    tropeId INTEGER NOT NULL,
                    PRIMARY KEY (bookId, tropeId),
                    FOREIGN KEY (bookId) REFERENCES books(id) ON DELETE CASCADE,
                    FOREIGN KEY (tropeId) REFERENCES tropes(id) ON DELETE CASCADE
                )
            """)

            // Create indexes for book_tropes
            connection.execSQL("CREATE INDEX IF NOT EXISTS index_book_tropes_bookId ON book_tropes(bookId)")
            connection.execSQL("CREATE INDEX IF NOT EXISTS index_book_tropes_tropeId ON book_tropes(tropeId)")

            // Add favoriteCharacter and favoriteScene columns to books table
            connection.execSQL("ALTER TABLE books ADD COLUMN favoriteCharacter TEXT DEFAULT NULL")
            connection.execSQL("ALTER TABLE books ADD COLUMN favoriteScene TEXT DEFAULT NULL")
        }
    }