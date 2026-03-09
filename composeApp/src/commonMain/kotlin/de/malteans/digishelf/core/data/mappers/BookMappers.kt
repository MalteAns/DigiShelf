package de.malteans.digishelf.core.data.mappers

import de.malteans.digishelf.core.data.database.entities.BookEntity
import de.malteans.digishelf.core.data.network.dto.BookItem
import de.malteans.digishelf.core.domain.Book
import de.malteans.digishelf.core.domain.BookSeries

// Updated to include new fields and default parameter for bookSeries
fun BookEntity.toDomain(bookSeries: BookSeries?): Book {
    return Book(
        id = this.id,
        title = this.title,
        author = this.author,
        isbn = this.isbn,
        description = this.description,
        onlineDescription = this.onlineDescription,
        imageUrl = this.imageUrl,
        pageCount = this.pageCount,
        price = this.price,
        currency = this.currency,
        bookSeries = bookSeries,
        rating = this.rating,
        readStatus = this.readStatus,
        readingTime = this.readingTime,
        possessionStatus = this.possessionStatus,
        eBookStatus = this.eBookStatus,
        deletedSince = this.deletedSince
    )
}

fun Book.toEntity(): BookEntity {
    return BookEntity(
        id = this.id,
        title = this.title,
        author = this.author,
        isbn = this.isbn,
        description = this.description,
        onlineDescription = this.onlineDescription,
        imageUrl = this.imageUrl,
        pageCount = this.pageCount,
        price = this.price,
        currency = this.currency,
        bookSeriesId = this.bookSeries?.id,
        rating = this.rating,
        readStatus = this.readStatus,
        readingTime = this.readingTime,
        possessionStatus = this.possessionStatus,
        eBookStatus = this.eBookStatus,
        deletedSince = this.deletedSince
    )
}

fun BookItem.toDomain(): Book {
    return Book(
        id = 0L, // id is generated locally
        title = this.volumeInfo.title,
        author = this.volumeInfo.authors?.joinToString(", ") ?: "",
        isbn = this.volumeInfo.industryIdentifiers?.find { it.type == "ISBN_13" }?.identifier ?: "",
        description = "",
        onlineDescription = this.volumeInfo.description,
        imageUrl = this.volumeInfo.imageLinks?.extraLarge
            ?: this.volumeInfo.imageLinks?.large
            ?: this.volumeInfo.imageLinks?.medium
            ?: this.volumeInfo.imageLinks?.small
            ?: this.volumeInfo.imageLinks?.thumbnail
            ?: this.volumeInfo.imageLinks?.smallThumbnail
            ?: "",
        pageCount = this.volumeInfo.pageCount,
        price = this.saleInfo.listPrice?.amount,
        currency = this.saleInfo.listPrice?.currencyCode,
        bookSeries = null,
        rating = null,
        readStatus = false,
        readingTime = null,
        possessionStatus = false,
        eBookStatus = this.saleInfo.isEbook,
        deletedSince = 0L
    )
}
