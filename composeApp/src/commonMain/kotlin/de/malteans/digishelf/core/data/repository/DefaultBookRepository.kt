package de.malteans.digishelf.core.data.repository

import de.malteans.digishelf.core.data.database.BookDao
import de.malteans.digishelf.core.data.database.entities.BookEntity
import de.malteans.digishelf.core.data.mappers.toDomain
import de.malteans.digishelf.core.data.mappers.toEntity
import de.malteans.digishelf.core.data.network.RemoteBookDataSource
import de.malteans.digishelf.core.domain.Book
import de.malteans.digishelf.core.domain.BookRepository
import de.malteans.digishelf.core.domain.BookSeries
import de.malteans.digishelf.core.domain.SortType
import de.malteans.digishelf.core.domain.errorHandling.DataError
import de.malteans.digishelf.core.domain.errorHandling.Result
import de.malteans.digishelf.core.domain.errorHandling.map
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

class DefaultBookRepository(
    private val bookDao: BookDao,
    private val remoteDataSource: RemoteBookDataSource
) : BookRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getBook(id: Long): Flow<Book?> {
        return bookDao.getBookById(id).flatMapLatest { bookEntity ->
            bookEntity?.addSeries() ?: flowOf(null)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getBookByIsbn(isbn: String): Flow<Book?> {
        return bookDao.getBookByIsbn(isbn).flatMapLatest { bookEntity ->
            bookEntity?.addSeries() ?: flowOf(null)
        }
    }

    private fun BookEntity.addSeries(): Flow<Book> {
        return if (this.bookSeriesId != null)
            this@DefaultBookRepository.getSeries(this.bookSeriesId).map { bookSeries ->
                this.toDomain(bookSeries)
            }
        else flowOf(this.toDomain(null))
    }
    
    override suspend fun addBook(book: Book): Long {
        return bookDao.upsertBook(book.toEntity())
    }

    override suspend fun updateBook(book: Book): Long {
        return bookDao.upsertBook(book.toEntity())
    }

    override suspend fun trashBook(id: Long) {
        bookDao.trashBookById(id)
    }

    override suspend fun restoreBook(id: Long) {
        bookDao.restoreBookById(id)
    }

    override suspend fun emptyTrash() {
        bookDao.emptyTrash()
    }

    override suspend fun restoreAllBooks() {
        bookDao.restoreAllBooks()
    }

    override suspend fun deleteBook(id: Long) {
        bookDao.deleteBookById(id)
    }

    override fun queryBooks(
        sortBy: SortType,
        titleQuery: String,
        authorQuery: String,
        isbnQuery: String,
        seriesQuery: String,
        readStatus: Boolean?,
        possessionStatus: Boolean?,
        eBookStatus: Boolean?,
        includeDeleted: Boolean,
    ): Flow<List<Book>> {
        val booksFlow = bookDao.queryBooks(
            titleQuery,
            authorQuery,
            isbnQuery,
            seriesQuery,
            readStatus,
            possessionStatus,
            eBookStatus,
            sortBy.queryValue,
            includeDeleted,
        )
        // Combine with all series (fetched with an empty query to get all series)
        val allSeriesFlow = this.querySeries()
        return combine(booksFlow, allSeriesFlow) { bookEntities, allSeries ->
            bookEntities.map { bookEntity ->
                bookEntity.toDomain(
                    bookEntity.bookSeriesId?.let { seriesId ->
                        allSeries.find { it.id == seriesId }
                    }
                )
            }
        }
    }

    override suspend fun fetchLocalBooks(
        sortBy: SortType,
        titleQuery: String,
        authorQuery: String,
        isbnQuery: String,
        seriesQuery: String,
        readStatus: Boolean?,
        possessionStatus: Boolean?,
        eBookStatus: Boolean?,
        includeDeleted: Boolean
    ): List<Book> {
        val books = bookDao.queryBooks(
            titleQuery,
            authorQuery,
            isbnQuery,
            seriesQuery,
            readStatus,
            possessionStatus,
            eBookStatus,
            sortBy.queryValue,
            includeDeleted
        ).first()
        // Combine with all series (fetched with an empty query to get all series)
        val allSeries = this.querySeries().first()
        return books.map { bookEntity ->
            bookEntity.toDomain(
                bookEntity.bookSeriesId?.let { seriesId ->
                    allSeries.find { it.id == seriesId }
                }
            )
        }
    }

    override fun getSeries(id: Long): Flow<BookSeries?> {
        return combine(
            bookDao.getSeriesById(id),
            bookDao.getBooksBySeriesId(id)
        ) { seriesEntity, bookEntities ->
            seriesEntity?.let { se ->
                // Create the series domain object first (with an empty list)
                val seriesDomain = se.toDomain(emptyList())
                // Map each book, setting its 'bookSeries' to the current seriesDomain
                val booksList = bookEntities.map { it.toDomain(seriesDomain) }
                // Then return the series domain with the updated books list
                se.toDomain(booksList)
            }
        }
    }

    override suspend fun addSeries(series: BookSeries): Long {
        return bookDao.upsertSeries(series.toEntity())
    }

    override suspend fun updateSeries(series: BookSeries): Long {
        return bookDao.upsertSeries(series.toEntity())
    }

    override suspend fun deleteSeries(id: Long) {
        bookDao.deleteSeriesById(id)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun querySeries(titleQuery: String): Flow<List<BookSeries>> {
        return bookDao.querySeries(titleQuery).flatMapLatest { seriesEntities ->
            if (seriesEntities.isEmpty()) {
                flowOf(emptyList())
            } else {
                bookDao.getBooksBySeriesIds(seriesEntities.map { it.id }).map { bookEntities ->
                    seriesEntities.map { seriesEntity ->
                        // Filter books for this series
                        val booksForSeries = bookEntities.filter { it.bookSeriesId == seriesEntity.id }
                        // First map without series info
                        val mappedBooks = booksForSeries.map { it.toDomain(null) }
                        // Create a series domain object (without books)
                        val seriesDomain = seriesEntity.toDomain(emptyList())
                        // Update each book to have its bookSeries set to seriesDomain
                        val updatedBooks = mappedBooks.map { it.copy(bookSeries = seriesDomain) }
                        // Return the series domain with the updated books list
                        seriesEntity.toDomain(updatedBooks)
                    }
                }
            }
        }
    }

    override suspend fun fetchBookFromRemote(isbn: String?, title: String?, author: String?): Result<Book, DataError.Remote> {
        return remoteDataSource
            .fetchBook(isbn, title, author)
            .map { bookResponse ->
                bookResponse.items?.firstOrNull()?.toDomain()
                    ?: return Result.Error(DataError.Remote.NO_RESULT)
            }
    }
}
