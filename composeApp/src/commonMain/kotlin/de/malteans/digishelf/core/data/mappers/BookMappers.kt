package de.malteans.digishelf.core.data.mappers

import de.malteans.digishelf.core.data.database.entities.BookEntity
import de.malteans.digishelf.core.data.network.dto.OpenLibraryDoc
import de.malteans.digishelf.core.domain.Book
import de.malteans.digishelf.core.domain.BookSeries
import de.malteans.digishelf.core.domain.Trope

// Updated to include new fields and default parameter for bookSeries
fun BookEntity.toDomain(bookSeries: BookSeries?, tropes: List<Trope> = emptyList()): Book {
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
        tensionLevel = this.tensionLevel,
        spiceLevel = this.spiceLevel,
        emotionLevel = this.emotionLevel,
        chapterLength = this.chapterLength,
        endingRating = this.endingRating,
        plotRating = this.plotRating,
        readStatus = this.readStatus,
        readingTime = this.readingTime,
        possessionStatus = this.possessionStatus,
        eBookStatus = this.eBookStatus,
        deletedSince = this.deletedSince,
        favoriteCharacter = this.favoriteCharacter,
        favoriteScene = this.favoriteScene,
        tropes = tropes,
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
        tensionLevel = this.tensionLevel,
        spiceLevel = this.spiceLevel,
        emotionLevel = this.emotionLevel,
        chapterLength = this.chapterLength,
        endingRating = this.endingRating,
        plotRating = this.plotRating,
        readStatus = this.readStatus,
        readingTime = this.readingTime,
        possessionStatus = this.possessionStatus,
        eBookStatus = this.eBookStatus,
        deletedSince = this.deletedSince,
        favoriteCharacter = this.favoriteCharacter,
        favoriteScene = this.favoriteScene,
    )
}

fun OpenLibraryDoc.toDomain(): Book {
    val isbn13 = this.isbn?.find { it.length == 13 }
    val chosenIsbn = isbn13 ?: this.isbn?.firstOrNull() ?: ""

    val imageUrl = when {
        this.coverI != null -> "https://covers.openlibrary.org/b/id/${this.coverI}-L.jpg"
        this.coverEditionKey != null -> "https://covers.openlibrary.org/b/olid/${this.coverEditionKey}-L.jpg"
        chosenIsbn.isNotBlank() -> "https://covers.openlibrary.org/b/isbn/$chosenIsbn-L.jpg"
        else -> ""
    }

    return Book(
        id = 0L, // id is generated locally
        title = this.title ?: "",
        author = this.authorName?.joinToString(", ") ?: "",
        isbn = chosenIsbn,
        description = "",
        onlineDescription = null,
        imageUrl = imageUrl,
        pageCount = this.numberOfPagesMedian ?: this.numberOfPages,
        price = null,
        currency = null,
        bookSeries = null,
        rating = null,
        readStatus = false,
        readingTime = null,
        possessionStatus = false,
        eBookStatus = false,
        deletedSince = 0L,
        favoriteCharacter = null,
        favoriteScene = null,
        tropes = emptyList(),
    )
}
