package de.malteans.digishelf.core.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import de.malteans.digishelf.core.data.database.migrations.MIGRATION1_2
import de.malteans.digishelf.core.data.database.migrations.MIGRATION2_3
import de.malteans.digishelf.core.data.database.migrations.MIGRATION3_4
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<BookDatabase> {
        val dbFile = documentDirectory() + "/${BookDatabase.DB_NAME}"
        return Room.databaseBuilder<BookDatabase>(dbFile)
            .addMigrations(
                BookDatabase.MIGRATION1_2, BookDatabase.MIGRATION2_3, BookDatabase.MIGRATION3_4,
            )
    }

    private fun documentDirectory(): String {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        return requireNotNull(documentDirectory?.path)
    }
}