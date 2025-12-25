package de.malteans.digishelf.navigation

enum class CurScreen(val enableDrawer: Boolean) {
    BooksOverview(true),
    Add(false),
    Details(false),
    Scanner(false),
    SeriesOverview(true),
    SeriesDetails(false),
    Settings(true),
    Trash(false),
    Legals(false),
}