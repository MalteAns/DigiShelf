package de.malteans.digishelf.core.presentation.settings

import de.malteans.digishelf.core.domain.Book
import de.malteans.digishelf.core.domain.BookSeries
import de.malteans.digishelf.core.presentation.main.components.CurScreen

data class SettingsState(
    val curScreen: CurScreen = CurScreen.Settings,

    val trashIsEmpty: Boolean = true,
    val trashedBooks: List<Book> = emptyList(),

    val export: Boolean = false,
    val import: Boolean = false,
    val allBooks: List<Book>? = null,
    val allBookSeries: List<BookSeries>? = null,
    val cloudCompletionInProgress: Boolean = false,
    val cloudCompletionDone: Boolean = false,
)
