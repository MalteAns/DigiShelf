package de.malteans.digishelf.export.data.mappers

import de.malteans.digishelf.core.data.database.entities.BookEntity
import de.malteans.digishelf.export.data.dto.BookExportDto

fun BookEntity.toExportDto() = BookExportDto(
    title = this.title,
    author = this.author,
    isbn = this.isbn,
    description = this.description,
    onlineDescription = this.onlineDescription,
    imageUrl = this.imageUrl,
    pageCount = this.pageCount,
    price = this.price,
    currency = this.currency,
    bookSeriesId = this.bookSeriesId,
    rating = this.rating,
    readStatus = this.readStatus,
    readingTime = this.readingTime,
    possessionStatus = this.possessionStatus,
    deletedSince = this.deletedSince,
)

fun BookExportDto.toEntity() = BookEntity(
    title = this.title,
    author = this.author,
    isbn = this.isbn,
    description = this.description,
    onlineDescription = this.onlineDescription,
    imageUrl = this.imageUrl,
    pageCount = this.pageCount,
    price = this.price,
    currency = this.currency,
    bookSeriesId = this.bookSeriesId,
    rating = this.rating,
    readStatus = this.readStatus,
    readingTime = this.readingTime,
    possessionStatus = this.possessionStatus,
    deletedSince = this.deletedSince,
)