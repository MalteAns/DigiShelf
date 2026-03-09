package de.malteans.digishelf.core.domain

import de.malteans.digishelf.core.domain.errorHandling.DataError
import de.malteans.digishelf.core.domain.errorHandling.Result
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    fun getBook(id: Long): Flow<Book?>
    fun getBookByIsbn(isbn: String): Flow<Book?>
    suspend fun addBook(book: Book): Long
    suspend fun updateBook(book: Book): Long
    suspend fun trashBook(id: Long)
    suspend fun restoreBook(id: Long)
    suspend fun emptyTrash()
    suspend fun restoreAllBooks()
    suspend fun deleteBook(id: Long)
    fun queryBooks(
        sortBy: SortType = SortType.TITLE,
        titleQuery: String = "",
        authorQuery: String = "",
        isbnQuery: String = "",
        seriesQuery: String = "",
        readStatus: Boolean? = null,
        possessionStatus: Boolean? = null,
        eBookStatus: Boolean? = null,
        includeDeleted: Boolean = false,
    ): Flow<List<Book>>
    suspend fun fetchLocalBooks(
        sortBy: SortType = SortType.TITLE,
        titleQuery: String = "",
        authorQuery: String = "",
        isbnQuery: String = "",
        seriesQuery: String = "",
        readStatus: Boolean? = null,
        possessionStatus: Boolean? = null,
        eBookStatus: Boolean? = null,
        includeDeleted: Boolean = false,
    ): List<Book>

    fun getSeries(id: Long): Flow<BookSeries?>
    suspend fun addSeries(series: BookSeries): Long
    suspend fun updateSeries(series: BookSeries): Long
    suspend fun deleteSeries(id: Long)
    fun querySeries(
        titleQuery: String = "",
    ): Flow<List<BookSeries>>

    suspend fun fetchBookFromRemote(
        isbn: String? = null, title: String? = null, author: String? = null
    ): Result<Book, DataError.Remote>
}