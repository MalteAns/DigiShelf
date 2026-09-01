package de.malteans.digishelf.core.presentation.add

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.malteans.digishelf.core.domain.BookSeries
import de.malteans.digishelf.core.presentation.add.components.LevelBar
import de.malteans.digishelf.core.presentation.components.CustomAlertDialog
import de.malteans.digishelf.core.presentation.components.customIconBarcodeScanner
import de.malteans.digishelf.core.presentation.details.components.ImagePicker
import de.malteans.digishelf.core.presentation.overview.components.SeriesDropdown
import digishelf.composeapp.generated.resources.*
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
    val focusManger = LocalFocusManager.current

    val snackbarHostState = remember { SnackbarHostState() }

    val resBookAdded = stringResource(Res.string.book_added_success)
    val resShow = stringResource(Res.string.show)

    LaunchedEffect(state.addedBookId) {
        if (state.addedBookId != null) {
            val snackbarResult = snackbarHostState.showSnackbar(
                message = resBookAdded,
                actionLabel = resShow,
                withDismissAction = true,
            )
            if (snackbarResult == SnackbarResult.ActionPerformed) {
                onAction(AddAction.OnShowBookDetail(state.addedBookId))
            }
        }
    }

    var showImagePicker by remember { mutableStateOf(false) }
    if (showImagePicker) {
        ImagePicker { imagePath ->
            if (!imagePath.isNullOrBlank()) {
                onAction(AddAction.OnImageUrlChanged(imagePath))
            }
            showImagePicker = false
        }
    }

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onAction(AddAction.OnShowOverview) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.back)
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(Res.string.add_book),
                        modifier = Modifier.clickable { onAction(AddAction.ClearFields) }
                    )
                },
                actions = {
                    IconButton(onClick = {
                        if (state.title.isNotBlank() && !state.pagesError && !state.priceError) {
                            if (state.author.isBlank() || state.isbn.isBlank()) {
                                onAction(AddAction.OnShowIncompleteError)
                            } else {
                                onAction(AddAction.AddBook)
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = stringResource(Res.string.submit),
                            tint = if (state.title.isNotBlank() && !state.pagesError && !state.priceError)
                                MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
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

        if (state.showError) {
            CustomAlertDialog(
                title = state.errorTitle.asString(),
                text = state.errorMessage.asString(),
                onDismiss = { onAction(AddAction.OnDismissError) },
                onConfirm = { onAction(AddAction.OnDismissError) },
                noConfirmButton = true,
            )
        }

        if (state.showIncompleteError) {
            CustomAlertDialog(
                title = stringResource(Res.string.data_incomplete),
                text = stringResource(Res.string.error_msg_add_incomplete),
                onDismiss = { onAction(AddAction.OnDismissIncompleteError) },
                onConfirm = {
                    onAction(AddAction.OnDismissIncompleteError)
                    onAction(AddAction.AddBook)
                },
            )
        }

        LaunchedEffect(state.isLoading) {
            if (state.isLoading) {
                focusManger.clearFocus()
            }
        }

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

        Column(
            modifier = Modifier
                .padding(12.dp, pad.calculateTopPadding(), 12.dp, 12.dp)
                .verticalScroll(scrollState)
                .blur(if (state.isLoading) 1.dp else 0.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Title ------------------------------------------------------------------------------
            OutlinedTextField(
                value = state.title,
                onValueChange = {
                    onAction(AddAction.OnTitleChanged(it))
                },
                label = { Text(stringResource(Res.string.title)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    if (state.title.length >= 8) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(Res.string.auto_complete),
                            modifier = Modifier
                                .clickable {
                                    onAction(AddAction.OnAutoComplete(title = state.title))
                                }
                                .padding(end = 8.dp)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManger.moveFocus(FocusDirection.Down)
                    }
                )
            )
            // Author -----------------------------------------------------------------------------
            OutlinedTextField(
                value = state.author,
                onValueChange = {
                    onAction(AddAction.OnAuthorChanged(it))
                },
                label = { Text(stringResource(Res.string.author)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManger.moveFocus(FocusDirection.Down)
                    }
                )
            )
            // ISBN -------------------------------------------------------------------------------
            OutlinedTextField(
                value = state.isbn,
                onValueChange = {
                    onAction(AddAction.OnIsbnChanged(it))
                },
                label = { Text(stringResource(Res.string.isbn)) },
                isError = state.isDoubleIsbn,
                supportingText = {
                    AnimatedVisibility (
                        visible = state.isDoubleIsbn,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Text(
                            text = stringResource(Res.string.is_double_isbn),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    if (state.showCompleteWithIsbn) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(Res.string.auto_complete),
                            modifier = Modifier
                                .clickable {
                                    onAction(AddAction.OnAutoComplete(isbn = state.isbn))
                                }
                                .padding(end = 8.dp)
                        )
                    } else {
                        Icon(
                            imageVector = customIconBarcodeScanner(),
                            contentDescription = stringResource(Res.string.scan),
                            modifier = Modifier
                                .clickable {
                                    onAction(AddAction.OnScan)
                                }
                                .padding(end = 8.dp)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManger.moveFocus(FocusDirection.Down)
                    }
                ),
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Checkbox(
                        checked = state.possessionStatus,
                        onCheckedChange = {
                            onAction(AddAction.OnPossessionStatusChanged(it))
                        },
                    )
                    Text(stringResource(Res.string.owned))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Checkbox(
                        checked = state.readStatus,
                        onCheckedChange = {
                            onAction(AddAction.OnReadStatusChanged(it))
                        },
                    )
                    Text(stringResource(Res.string.read))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Checkbox(
                        checked = state.eBookStatus,
                        onCheckedChange = {
                            onAction(AddAction.OnEbookStatusChanged(it))
                        },
                    )
                    Text(stringResource(Res.string.ebook))
                }
                Spacer(modifier = Modifier.width(12.dp))
            }
            // Rating bar -----------------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                LevelBar(
                    current = state.rating,
                    onLevelChanged = { onAction(AddAction.OnRatingChanged(it)) },
                    activeIcon = Icons.Filled.Star,
                    inactiveIcon = Icons.Outlined.Star,
                    contentDescription = stringResource(Res.string.rating),
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
            }
            // Tension, Spice, Emotion bars ---------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                LevelBar(
                    current = state.tensionLevel,
                    onLevelChanged = { onAction(AddAction.OnTensionLevelChanged(it)) },
                    activeIcon = Icons.Filled.ElectricBolt,
                    inactiveIcon = Icons.Outlined.ElectricBolt,
                    contentDescription = stringResource(Res.string.tension_level),
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                LevelBar(
                    current = state.spiceLevel,
                    onLevelChanged = { onAction(AddAction.OnSpiceLevelChanged(it)) },
                    activeIcon = Icons.Filled.LocalFireDepartment,
                    inactiveIcon = Icons.Outlined.LocalFireDepartment,
                    contentDescription = stringResource(Res.string.spice_level),
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                LevelBar(
                    current = state.emotionLevel,
                    onLevelChanged = { onAction(AddAction.OnEmotionLevelChanged(it)) },
                    activeIcon = Icons.Filled.WaterDrop,
                    inactiveIcon = Icons.Outlined.WaterDrop,
                    contentDescription = stringResource(Res.string.emotion_level),
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
            }
            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                val options: Map<Any?, String> = state.bookSeriesList.associateBy({ it as BookSeries? }, { it.title })
                    .toMutableMap()
                    .apply { put(null, "–") }
                    .toMap()

                SeriesDropdown(
                    selectedOption = Pair<Any?, String>(state.bookSeries, state.bookSeries?.title ?: "–"),
                    options = options,
                    onValueChanged = { newSeries ->
                        onAction(
                            AddAction.OnBookSeriesChanged(newSeries as BookSeries?)
                        )
                    },
                    onValueAdded = { newSeriesName ->
                        onAction( AddAction.OnBookSeriesChanged(
                            BookSeries(
                                id = 0L,
                                title = newSeriesName,
                            )
                        ))
                    },
                    label = stringResource(Res.string.book_series),
                )
            }
            // Pages, Price -----------------------------------------------------------------------
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = state.pages,
                    onValueChange = {
                        onAction(AddAction.OnPagesChanged(it))
                    },
                    label = { Text(stringResource(Res.string.pages)) },
                    isError = state.pagesError,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManger.moveFocus(FocusDirection.Right)
                        }
                    )
                )
                OutlinedTextField(
                    value = state.price,
                    onValueChange = {
                        onAction(AddAction.OnPriceChanged(it))
                    },
                    label = { Text(stringResource(Res.string.price)) },
                    suffix = { Text("€") },
                    isError = state.priceError,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManger.moveFocus(FocusDirection.Down)
                        }
                    )
                )
            }
            // Image URL --------------------------------------------------------------------------
            OutlinedTextField(
                value = state.imageUrl,
                onValueChange = {
                    onAction(AddAction.OnImageUrlChanged(it))
                },
                label = { Text(stringResource(Res.string.cover_image)) },
                trailingIcon = {
                    IconButton({ showImagePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.FileOpen,
                            contentDescription = "Pick Image"
                        )
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManger.clearFocus()
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

//private suspend fun completeWithIsbn(
//    onEvent: (AddAction) -> Unit,
//    isbnFromNav: String,
//    pErrorTitleResource: Int,
//    state: AddState,
//    pErrorMessageResource: Int,
//    pShowError: Boolean
//): Triple<Int, Int, Boolean> {
//    var errorTitleResource = pErrorTitleResource FIXME: Complete with ISBN
//    var errorMessageResource = pErrorMessageResource
//    var showError = pShowError
//    val bookResponse = BookModel.bookService.getBook(
//        isbn = "isbn:$isbnFromNav"
//    )
//    try {
//        onEvent(AddAction.TitleChanged(""))
//        onEvent(AddAction.AuthorChanged(""))
//        onEvent(AddAction.TitleChanged(bookResponse.items[0].volumeInfo.title))
//        onEvent(AddAction.AuthorChanged(bookResponse.items[0].volumeInfo.authors.joinToString(", ")))
//    } catch (e: Exception) {
//        errorTitleResource = R.string.error_scan
//        errorMessageResource = if (state.title.isBlank()) {
//            R.string.error_msg_scan
//        } else {
//            R.string.error_msg_scan_author
//        }
//        showError = true
//    }
//    //return list of errorTitleResource, errorMessageResource, showError
//    return Triple(errorTitleResource, errorMessageResource, showError)
//}

/* RIP BookSeriesDropDown
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSeriesDropDown(
    selectedOption: String,
    options: List<String>,
    label: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var currentInput by remember { mutableStateOf(selectedOption) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
            if (expanded)
                currentInput = ""
        },
        modifier = modifier
    ) {
        OutlinedTextField(
            readOnly = !expanded,
            value = if (expanded) currentInput else selectedOption,
            onValueChange = {
                currentInput = it
                onValueChanged(it)
            },
            label = { Text(text = label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            singleLine = true,
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option: String ->
                if (option.contains(currentInput, ignoreCase = true)) {
                    DropdownMenuItem(
                        text = { Text(text = option) },
                        onClick = {
                            expanded = false
                            currentInput = option
                            onValueChanged(option)
                        }
                    )
                }
            }
            if (currentInput.isNotBlank() && !options.contains(currentInput)) {
                DropdownMenuItem(
                    text = { Text(text = currentInput) },
                    onClick = {
                        expanded = false
                        onValueChanged(currentInput)
                    }
                )
            }
        }
    }
}
*/

//@Preview
//@Composable
//fun AddBookScreenPreview() {
//    BookOverviewTheme {
//        AddBookScreen()
//    }
//}
