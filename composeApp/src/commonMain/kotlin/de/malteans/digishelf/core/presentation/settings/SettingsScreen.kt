package de.malteans.digishelf.core.presentation.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import de.malteans.digishelf.core.presentation.components.CustomTopBar
import de.malteans.digishelf.core.presentation.settings.components.ImportDialog
import de.malteans.digishelf.core.presentation.settings.components.SettingsItem
import de.malteans.digishelf.theme.containerColor
import de.malteans.legal.presentation.components.LegalsList
import de.malteans.legal.presentation.navigation.LegalRoute
import digishelf.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreenRoot(
    viewModel: SettingsViewModel = koinViewModel(),
    openDrawer: () -> Unit,
    onTrashClicked: () -> Unit,
    navigateToLegalRoute: (LegalRoute) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    SettingsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is SettingsAction.OnTrashClicked -> {
                    onTrashClicked()
                    viewModel.onAction(action)
                }
                is SettingsAction.OnOpenDrawer -> openDrawer()
                is SettingsAction.OnNavigateToLegalScreen -> {
                    navigateToLegalRoute(action.legalRoute)
                }

                else -> viewModel.onAction(action)
            }

        }
    )
}

@Composable
fun SettingsScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit
) {
    var showImportDialog by remember { mutableStateOf(false) }

    // Show the import dialog when triggered.
    if (showImportDialog) {
        ImportDialog(
            onDismiss = { showImportDialog = false },
            onFinish = { fileContent ->
                onAction(SettingsAction.OnImport(fileContent))
                showImportDialog = false
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CustomTopBar(
                title = {
                    Text(text = stringResource(Res.string.settings))
                },
                navigationAction = { onAction(SettingsAction.OnOpenDrawer) }
            )
        },
    ) { pad ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 8.dp)
                .padding(pad)
                .fillMaxSize(),
        ) {
            Column(
                verticalArrangement = spacedBy(4.dp),
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
            ) {
                SettingsItem(
                    title = stringResource(Res.string.trash)
                            + if (state.trashIsEmpty) " (${stringResource(Res.string.empty)})" else "",
                    description = stringResource(Res.string.settings_trash_desc),
                    icon = Icons.Outlined.Delete,
                    onClick = { onAction(SettingsAction.OnTrashClicked) },
                    modifier = Modifier.fillMaxWidth()
                )
                SettingsItem(
                    title = stringResource(Res.string.export_data),
                    description = stringResource(Res.string.settings_export_desc),
                    icon = Icons.Outlined.FileDownload,
                    onClick = { onAction(SettingsAction.OnExportClicked) },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )
                SettingsItem(
                    title = stringResource(Res.string.import_data),
                    description = stringResource(Res.string.settings_import_desc),
                    icon = Icons.Outlined.UploadFile,
                    onClick = { showImportDialog = true },
                    modifier = Modifier.fillMaxWidth()
                )
                SettingsItem(
                    title = stringResource(Res.string.cloud_completion),
                    description = stringResource(Res.string.cloud_completion_desc),
                    icon = when {
                        state.cloudCompletionInProgress -> Icons.Outlined.CloudSync
                        state.cloudCompletionDone -> Icons.Outlined.Done
                        else -> Icons.Outlined.CloudDownload
                    },
                    onClick = { onAction(SettingsAction.OnCloudCompleteClicked) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.height(16.dp))
            LegalsList(
                tileContainerColor = MaterialTheme.colorScheme.containerColor,
                navigateToLegalScreen = { legalRoute ->
                    onAction(SettingsAction.OnNavigateToLegalScreen(legalRoute))
                },
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
            )
        }
    }

    // Loading ----------------------------------------------------------------
    AnimatedVisibility(
        visible = state.cloudCompletionInProgress,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures (
                        onTap = { /* Consume touches when loading */ }
                    )
                }
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}