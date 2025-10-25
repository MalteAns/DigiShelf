package de.malteans.digishelf.core.presentation.main

import de.malteans.digishelf.core.presentation.main.components.CurScreen

data class MainState(
    val selectedCurScreen: CurScreen = CurScreen.BooksOverview
)
