package de.malteans.digishelf.core.presentation.main

import de.malteans.digishelf.core.presentation.main.components.CurScreen

sealed interface MainAction {
    data class SetScreen(val curScreen: CurScreen) : MainAction
}