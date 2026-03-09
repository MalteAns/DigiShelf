package de.malteans.digishelf.core.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.malteans.digishelf.core.data.database.entities.BookEntity
import de.malteans.digishelf.core.data.database.entities.BookSeriesEntity
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Dao
interface BookDao {

    @Query("SELECT * FROM books WHERE id = :id")
    fun getBookById(id: Long): Flow<BookEntity?>

    @Query("SELECT * FROM books WHERE isbn = :isbn")
    fun getBookByIsbn(isbn: String): Flow<BookEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBook(book: BookEntity): Long

    @Query("UPDATE books SET deletedSince = :deletedSince WHERE id = :id")
    suspend fun trashBookById(id: Long, deletedSince: Long = Clock.System.now().toEpochMilliseconds())

    @Query("UPDATE books SET deletedSince = 0 WHERE id = :id")
    suspend fun restoreBookById(id: Long)

    @Query("DELETE FROM books WHERE deletedSince > 0")
    suspend fun emptyTrash()

    @Query("UPDATE books SET deletedSince = 0 WHERE deletedSince > 0")
    suspend fun restoreAllBooks()

    @Query("DELETE FROM books WHERE id = :id")
    suspend fun deleteBookById(id: Long)

    @Query(
        """
        SELECT * FROM books 
        WHERE 
          (:includeDeleted OR deletedSince = 0) AND
          title LIKE '%' || :titleQuery || '%' AND
          author LIKE '%' || :authorQuery || '%' AND
          isbn LIKE '%' || :isbnQuery || '%' AND
          (CASE WHEN :seriesQuery = '' THEN 1 
                ELSE (bookSeriesId IN (SELECT id FROM book_series WHERE title LIKE '%' || :seriesQuery || '%')) 
           END) = 1 AND
          (:isRead IS NULL OR readStatus = :isRead) AND
          (:isOwned IS NULL OR possessionStatus = :isOwned) AND
          (:isEBook IS NULL OR eBookStatus = :isEBook)
        ORDER BY 
          CASE WHEN :sortBy = 'title' THEN title END COLLATE NOCASE ASC,
          CASE WHEN :sortBy = 'author' THEN author END COLLATE NOCASE ASC,
          CASE WHEN :sortBy = 'series' THEN (CASE WHEN bookSeriesId IS NULL THEN 'ZZZZZZ' ELSE (SELECT title FROM book_series WHERE id == bookSeriesId) END) END COLLATE NOCASE ASC
        """
    )
    fun queryBooks(
        titleQuery: String = "",
        authorQuery: String = "",
        isbnQuery: String = "",
        seriesQuery: String = "",
        isRead: Boolean? = null,
        isOwned: Boolean? = null,
        isEBook: Boolean? = null,
        sortBy: String = "title",
        includeDeleted: Boolean = false
    ): Flow<List<BookEntity>>

    @Query("SELECT * FROM book_series WHERE id = :id")
    fun getSeriesById(id: Long): Flow<BookSeriesEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSeries(series: BookSeriesEntity): Long

    @Query("DELETE FROM book_series WHERE id = :id")
    suspend fun deleteSeriesById(id: Long)

    @Query("SELECT * FROM book_series WHERE title LIKE '%' || :titleQuery || '%'")
    fun querySeries(titleQuery: String = ""): Flow<List<BookSeriesEntity>>

    @Query("SELECT * FROM books WHERE bookSeriesId = :seriesId AND deletedSince = 0")
    fun getBooksBySeriesId(seriesId: Long): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE bookSeriesId IN (:seriesIds) AND deletedSince = 0")
    fun getBooksBySeriesIds(seriesIds: List<Long>): Flow<List<BookEntity>>
}
