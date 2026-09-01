package de.malteans.digishelf.core.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import de.malteans.digishelf.core.data.database.migrations.MIGRATION1_2
import de.malteans.digishelf.core.data.database.migrations.MIGRATION2_3
import de.malteans.digishelf.core.data.database.migrations.MIGRATION3_4
import de.malteans.digishelf.core.data.database.migrations.MIGRATION4_5

actual class DatabaseFactory(
    private val context: Context
) {
    actual fun create(): RoomDatabase.Builder<BookDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(BookDatabase.DB_NAME)
        return Room.databaseBuilder<BookDatabase>(
            context = appContext,
            name = dbFile.absolutePath
        )
            .addMigrations(
                BookDatabase.MIGRATION1_2, BookDatabase.MIGRATION2_3, BookDatabase.MIGRATION3_4, BookDatabase.MIGRATION4_5,
            )
    }
}