package de.malteans.digishelf.core.presentation.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.malteans.digishelf.core.presentation.add.components.SearchBottomSheet
import de.malteans.digishelf.core.presentation.components.CustomAlertDialog
import de.malteans.digishelf.core.presentation.components.customIconBarcodeScanner
import digishelf.composeapp.generated.resources.Res
import digishelf.composeapp.generated.resources.add_book
import digishelf.composeapp.generated.resources.author
import digishelf.composeapp.generated.resources.back
import digishelf.composeapp.generated.resources.isbn
import digishelf.composeapp.generated.resources.scan
import digishelf.composeapp.generated.resources.search
import digishelf.composeapp.generated.resources.title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddScreenRoot(
    viewModel: AddViewModel = koinViewModel(),
    onShowScanner: () -> Unit,
    onShowOverview: () -> Unit,
    onShowBookDetail: (Long) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    AddScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is AddAction.OnShowOverview -> onShowOverview()
                is AddAction.OnScan -> onShowScanner()
                is AddAction.OnShowBookDetail -> {
                    onShowBookDetail(action.bookId)
                    viewModel.onAction(AddAction.ClearFields)
                }

                else -> viewModel.onAction(action)
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    state: AddState,
    onAction: (AddAction) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // Automatically navigate to details when a book is added
    LaunchedEffect(state.addedBookId) {
        if (state.addedBookId != null) {
            onAction(AddAction.OnShowBookDetail(state.addedBookId))
        }
    }

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onAction(AddAction.OnShowOverview) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.back))
                    }
                },
                title = {
                    Text(
                        text = stringResource(Res.string.add_book),
                        modifier = Modifier.clickable { onAction(AddAction.ClearFields) }
                    )
                },
            )
        },
        snackbarHost = { SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .padding(bottom = 48.dp)
        ) { snackbarData ->
            Snackbar(
                snackbarData = snackbarData,
                shape = RoundedCornerShape(30),
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.onSurface,
                actionColor = MaterialTheme.colorScheme.primary,
                actionContentColor = MaterialTheme.colorScheme.primary,
                dismissActionContentColor = MaterialTheme.colorScheme.onSurface,
            )
        } },
        modifier = Modifier.fillMaxSize(),
    ) { pad ->
        val scrollState = rememberScrollState()

        // Error dialog
        if (state.showError) {
            CustomAlertDialog(
                title = state.errorTitle.asString(),
                text = state.errorMessage.asString(),
                onDismiss = { onAction(AddAction.OnDismissError) },
                onConfirm = { onAction(AddAction.OnDismissError) },
                noConfirmButton = true,
            )
        }

        // Duplicate ISBN dialog for search results
        if (state.pendingBook != null) {
            CustomAlertDialog(
                title = "Duplicate ISBN",
                text = "A book with this ISBN already exists. Add anyway?",
                onDismiss = { onAction(AddAction.OnDismissDuplicateDialog) },
                onConfirm = { onAction(AddAction.OnConfirmAddDuplicate) },
            )
        }

        // Clear focus when loading
        LaunchedEffect(state.isLoading) {
            if (state.isLoading) {
                focusManager.clearFocus()
            }
        }

        // Loading overlay
        AnimatedVisibility(
            visible = state.isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        // Search bottom sheet
        if (state.showSearchBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { onAction(AddAction.OnDismissSearchBottomSheet) },
                sheetState = sheetState,
            ) {
                SearchBottomSheet(
                    isSearching = state.isSearching,
                    searchResults = state.searchResults,
                    onResultSelected = { book ->
                        onAction(AddAction.OnSearchResultSelected(book))
                    },
                    onDismiss = { onAction(AddAction.OnDismissSearchBottomSheet) }
                )
            }
        }

        // Main content
        Column(
            modifier = Modifier
                .padding(12.dp, pad.calculateTopPadding(), 12.dp, 12.dp)
                .verticalScroll(scrollState)
                .blur(if (state.isLoading) 1.dp else 0.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title field
            OutlinedTextField(
                value = state.title,
                onValueChange = { onAction(AddAction.OnTitleChanged(it)) },
                label = { Text(stringResource(Res.string.title)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            // Author field
            OutlinedTextField(
                value = state.author,
                onValueChange = { onAction(AddAction.OnAuthorChanged(it)) },
                label = { Text(stringResource(Res.string.author)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            // ISBN field
            OutlinedTextField(
                value = state.isbn,
                onValueChange = { onAction(AddAction.OnIsbnChanged(it)) },
                label = { Text(stringResource(Res.string.isbn)) },
                isError = state.isDuplicateIsbn,
                supportingText = {
                    AnimatedVisibility (
                        visible = state.isDuplicateIsbn,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Text(
                            text = "ISBN already exists",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    IconButton(
                        onClick = { onAction(AddAction.OnScan) },
                        shape = RectangleShape,
                    ) {
                        Icon(
                            imageVector = customIconBarcodeScanner(),
                            contentDescription = stringResource(Res.string.scan)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Search button
                Button(
                    onClick = { onAction(AddAction.OnSearchClicked) },
                    enabled = state.title.length >= 10 || 
                            state.isbn.isIsbnFormat() || 
                            (state.title.length >= 6 && state.author.length >= 6),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(Res.string.search)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(Res.string.search))
                }

                // Just add button
                Button(
                    onClick = { onAction(AddAction.AddBook) },
                    enabled = state.title.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(Res.string.add_book)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Just add")
                }
            }
        }
    }
}
