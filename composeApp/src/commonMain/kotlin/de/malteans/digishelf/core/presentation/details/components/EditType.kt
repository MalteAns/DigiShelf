package de.malteans.digishelf.core.presentation.details.components

import digishelf.composeapp.generated.resources.Res
import digishelf.composeapp.generated.resources.author
import digishelf.composeapp.generated.resources.book_series
import digishelf.composeapp.generated.resources.cover_image
import digishelf.composeapp.generated.resources.description
import digishelf.composeapp.generated.resources.emotion_level
import digishelf.composeapp.generated.resources.isbn
import digishelf.composeapp.generated.resources.pages
import digishelf.composeapp.generated.resources.price
import digishelf.composeapp.generated.resources.rating
import digishelf.composeapp.generated.resources.reading_time
import digishelf.composeapp.generated.resources.spice_level
import digishelf.composeapp.generated.resources.status
import digishelf.composeapp.generated.resources.tension_level
import digishelf.composeapp.generated.resources.title
import org.jetbrains.compose.resources.StringResource

enum class EditType {
    COVER_IMAGE,
    ISBN,
    TITLE,
    AUTHOR,
    RATING,
    TENSION_LEVEL,
    SPICE_LEVEL,
    EMOTION_LEVEL,
    PAGE_COUNT,
    PRICE,
    STATUS,
    READING_TIME,
    BOOK_SERIES,
    DESCRIPTION;

    val getTypeStringResource: StringResource
        get() = when(this) {
            COVER_IMAGE -> Res.string.cover_image
            ISBN -> Res.string.isbn
            TITLE -> Res.string.title
            AUTHOR -> Res.string.author
            RATING -> Res.string.rating
            TENSION_LEVEL -> Res.string.tension_level
            SPICE_LEVEL -> Res.string.spice_level
            EMOTION_LEVEL -> Res.string.emotion_level
            PAGE_COUNT -> Res.string.pages
            PRICE -> Res.string.price
            STATUS -> Res.string.status
            READING_TIME -> Res.string.reading_time
            BOOK_SERIES -> Res.string.book_series
            DESCRIPTION -> Res.string.description
        }
}