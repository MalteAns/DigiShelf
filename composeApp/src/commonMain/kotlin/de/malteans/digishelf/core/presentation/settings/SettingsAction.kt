package de.malteans.digishelf.core.presentation.settings

import de.malteans.digishelf.core.domain.Book
import de.malteans.digishelf.export.presentation.ImportFileType
import de.malteans.legal.presentation.navigation.LegalRoute

sealed interface SettingsAction {
    data object OnBack: SettingsAction
    data object OnOpenDrawer: SettingsAction

    data object SettingsOpened: SettingsAction

    data object OnTrashClicked : SettingsAction
    data class OnNavigateToLegalScreen(val legalRoute: LegalRoute) : SettingsAction
    
    data class OnTrashRestoreClicked(val book: Book) : SettingsAction
    data class OnTrashDeleteClicked(val book: Book) : SettingsAction
    data object OnTrashDeleteAllClicked : SettingsAction
    data object OnTrashRestoreAllClicked : SettingsAction
    data object OnExportClicked : SettingsAction
    data object OnCloudCompleteClicked: SettingsAction

    data object ResetExportData : SettingsAction

    data class OnImport(val fileType: ImportFileType, val fileContent: String) : SettingsAction
}
