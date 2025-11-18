package de.malteans.digishelf.core.presentation.main.components

enum class CurScreen(val enableDrawer: Boolean = true) {
    BooksOverview,
    Add,
    Details,
    Scanner,
    SeriesOverview,
    SeriesDetails(false),
    Settings,
    Trash(false),
    Legals(false),
}