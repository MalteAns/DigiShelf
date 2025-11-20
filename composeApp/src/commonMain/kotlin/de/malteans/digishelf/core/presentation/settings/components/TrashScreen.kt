package de.malteans.digishelf.core.presentation.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.malteans.digishelf.core.domain.Book
import de.malteans.digishelf.core.presentation.components.CustomDialog
import de.malteans.digishelf.core.presentation.settings.SettingsAction
import de.malteans.digishelf.core.presentation.settings.SettingsState
import de.malteans.digishelf.core.presentation.settings.SettingsViewModel
import de.malteans.digishelf.core.presentation.settings.components.icons.CustomDeleteForeverIcon
import de.malteans.digishelf.core.presentation.settings.components.icons.CustomRestoreFromTrashIcon
import digishelf.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TrashScreenRoot(
    viewModel: SettingsViewModel = koinViewModel(),
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    TrashScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is SettingsAction.OnBack -> onBack()

                else -> viewModel.onAction(action)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit
) {

    var currentBook: Book? = null
    var showDialog by remember { mutableStateOf(false) }

    var confirmType by remember { mutableStateOf(TrashConfirmType.RESTORE_ALL) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(Res.string.trash))
                },
                navigationIcon = {
                    IconButton(onClick = { onAction(SettingsAction.OnBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        confirmType = TrashConfirmType.RESTORE_ALL
                        showConfirmDialog = true
                    }) {
                        Icon(
                            imageVector = CustomRestoreFromTrashIcon,
                            contentDescription = stringResource(Res.string.restore_all)
                        )
                    }
                    IconButton(onClick = {
                        confirmType = TrashConfirmType.DELETE_ALL
                        showConfirmDialog = true
                    }) {
                        Icon(
                            imageVector = CustomDeleteForeverIcon,
                            contentDescription = stringResource(Res.string.delete_all)
                        )
                    }
                }
            )
        },
    ) { pad ->
        if (showDialog && currentBook != null) {
            CustomDialog(
                onDismissRequest = { showDialog = false },
                rightIcons = {
                    Icon(
                        imageVector = CustomRestoreFromTrashIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable {
                                onAction(SettingsAction.OnTrashRestoreClicked(currentBook!!))
                                showDialog = false
                            }
                    )
                    Icon(
                        imageVector = CustomDeleteForeverIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .clickable {
                                onAction(SettingsAction.OnTrashDeleteClicked(currentBook!!))
                                showDialog = false
                            }
                    )
                },
                leftIcons = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier
                            .clickable { showDialog = false }
                    )
                },
                title = {
                    Text(stringResource(Res.string.restore_or_delete))
                }
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(stringResource(Res.string.restore_or_delete_desc, currentBook!!.title))
                }
            }
        }

        if (showConfirmDialog) {
            CustomDialog(
                onDismissRequest = { showConfirmDialog = false },
                rightIcons = {
                    Icon(
                        imageVector = when (confirmType) {
                            TrashConfirmType.DELETE_ALL -> CustomDeleteForeverIcon
                            TrashConfirmType.RESTORE_ALL -> CustomRestoreFromTrashIcon
                        },
                        contentDescription = null,
                        tint = when (confirmType) {
                            TrashConfirmType.DELETE_ALL -> MaterialTheme.colorScheme.error
                            TrashConfirmType.RESTORE_ALL -> MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier
                            .clickable {
                                when (confirmType) {
                                    TrashConfirmType.DELETE_ALL -> onAction(SettingsAction.OnTrashDeleteAllClicked)
                                    TrashConfirmType.RESTORE_ALL -> onAction(SettingsAction.OnTrashRestoreAllClicked)
                                }
                                showConfirmDialog = false
                            }
                    )
                },
                leftIcons = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier
                            .clickable {
                                showConfirmDialog = false
                            }
                    )
                },
                title = {
                    Text(
                        text = when (confirmType) {
                            TrashConfirmType.DELETE_ALL -> stringResource(Res.string.delete_all)
                            TrashConfirmType.RESTORE_ALL -> stringResource(Res.string.restore_all)
                        }
                    )
                },
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = when (confirmType) {
                            TrashConfirmType.DELETE_ALL -> stringResource(Res.string.delete_all_desc)
                            TrashConfirmType.RESTORE_ALL -> stringResource(Res.string.restore_all_desc)
                        }
                    )
                }
            }
        }

        Column (
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
        ) {
            LazyColumn {
                items(state.trashedBooks) { book ->
                    Column {
                        Row {
                            TrashedBookItem(
                                book = book,
                                onClick = {
                                    currentBook = book
                                    showDialog = true
                                },
                                onRestore = { onAction(SettingsAction.OnTrashRestoreClicked(book)) },
                                onDelete = { onAction(SettingsAction.OnTrashDeleteClicked(book)) },
                            )
                        }
                        Row {
                            Spacer(modifier = Modifier
                                .height(2.dp)
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)),
                            )
                        }
                    }
                }
            }
        }
    }
}

