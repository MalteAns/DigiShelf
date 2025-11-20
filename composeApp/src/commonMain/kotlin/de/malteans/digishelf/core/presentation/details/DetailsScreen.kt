package de.malteans.digishelf.core.presentation.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.digishelf.core.domain.BookSeries
import de.malteans.digishelf.core.presentation.add.components.RatingBar
import de.malteans.digishelf.core.presentation.add.isIsbnFormat
import de.malteans.digishelf.core.presentation.components.CustomAlertDialog
import de.malteans.digishelf.core.presentation.components.CustomDialog
import de.malteans.digishelf.core.presentation.components.customReadIcon
import de.malteans.digishelf.core.presentation.details.components.*
import de.malteans.digishelf.core.presentation.main.components.CustomBookIcon
import de.malteans.digishelf.core.presentation.overview.components.SeriesDropdown
import digishelf.composeapp.generated.resources.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.random.Random.Default.nextInt

@Composable
fun DetailsScreenRoot(
    viewModel: DetailsViewModel = koinViewModel(),
    onBack: () -> Unit,
    onAuthorSearch: (String) -> Unit,
    bookId: Long,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(bookId) {
        viewModel.onAction(DetailsAction.SetBookId(bookId))
    }

    DetailsScreen(
        state = state,
        onAction = { action -> 
            when (action) {
                is DetailsAction.OnBack -> onBack()
                is DetailsAction.OnAuthorSearch -> onAuthorSearch(action.author)
                is DetailsAction.DeleteBook -> {
                    onBack()
                    viewModel.onAction(action)
                }
                
                else -> viewModel.onAction(action)
            } 
        },
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun DetailsScreen(
    state: DetailsState,
    onAction: (DetailsAction) -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    var showConfirmLeaveDialog by remember { mutableStateOf(false) }
    var showNoTitleDialog by remember { mutableStateOf(false) }

    fun onDismiss() {
        if (state.isEditing && state.somethingChanged) {
            showConfirmLeaveDialog = true
        } else {
            onAction(DetailsAction.OnBack)
        }
    }

    BackHandler {
        onDismiss()
    }

    var showEditDialog by remember { mutableStateOf(false) }
    var curEditType by remember { mutableStateOf(EditType.TITLE) }

    if (showConfirmLeaveDialog) {
        CustomAlertDialog(
            title = { Text(stringResource(Res.string.error_save_changes)) },
            text = { Text(stringResource(Res.string.error_msg_save_changes)) },
            onDismissRequest = { showConfirmLeaveDialog = false },
            onConfirm = {
                if (state.title.isBlank()) {
                    showConfirmLeaveDialog = false
                    showNoTitleDialog = true
                } else {
                    onAction(DetailsAction.UpdateBook)
                    onAction(DetailsAction.OnBack)
                }
            },
            onDismiss = {
                onAction(DetailsAction.OnBack)
            }
        )
    }

    if (showNoTitleDialog) {
        CustomAlertDialog(
            title = { Text(text = stringResource(Res.string.error)) },
            text = { Text(text = stringResource(Res.string.error_msg_no_title)) },
            onDismissRequest = { showNoTitleDialog = false },
            onConfirm = { showNoTitleDialog = false }
        )
    }

    if (showEditDialog) {
        var tempString by remember { mutableStateOf("") }
        var tempSeries by remember { mutableStateOf(state.series) }
        var tempStatus by remember { mutableStateOf(state.possessionStatus to state.readStatus) }

        var readyToFinish by remember { mutableStateOf(false) }

        LaunchedEffect(curEditType, tempString, tempSeries) {
            readyToFinish = when (curEditType) {
                EditType.ISBN -> tempString.isIsbnFormat()
                EditType.TITLE, EditType.AUTHOR -> tempString.isNotBlank()
                EditType.PAGE_COUNT, EditType.READING_TIME -> tempString.let { value ->
                    value.isEmpty() || (value.toIntOrNull() != null && value.toInt() >= 0)
                }
                EditType.PRICE -> tempString.let { value ->
                    value.isEmpty() || (value.toDoubleOrNull() != null && value.toDouble() >= 0)
                }
                EditType.COVER_IMAGE, EditType.STATUS, EditType.BOOK_SERIES, EditType.DESCRIPTION -> true
            }
        }

        LaunchedEffect(key1 = curEditType) {
            tempString = when (curEditType) {
                EditType.COVER_IMAGE -> state.imageUrl
                EditType.ISBN -> state.isbn
                EditType.TITLE -> state.title
                EditType.AUTHOR -> state.author
                EditType.PAGE_COUNT -> state.pageCount?.toString() ?: ""
                EditType.PRICE -> state.price?.toPriceString(null) ?: ""
                EditType.DESCRIPTION -> state.description
                EditType.STATUS, EditType.READING_TIME, EditType.BOOK_SERIES -> ""
            }
        }

        fun onDoneClicked() {
            if (readyToFinish) {
                when (curEditType) {
                    EditType.COVER_IMAGE -> onAction(DetailsAction.ImageUrlChanged(tempString))
                    EditType.ISBN -> onAction(DetailsAction.IsbnChanged(tempString))
                    EditType.TITLE -> onAction(DetailsAction.TitleChanged(tempString))
                    EditType.AUTHOR -> onAction(DetailsAction.AuthorChanged(tempString))
                    EditType.PAGE_COUNT -> onAction(DetailsAction.PageCountChanged(tempString.toIntOrNull()))
                    EditType.PRICE -> onAction(DetailsAction.PriceChanged(tempString.toDoubleOrNull()))
                    EditType.STATUS -> onAction(DetailsAction.StatusChanged(tempStatus.first, tempStatus.second))
                    EditType.READING_TIME -> onAction(
                        DetailsAction.ReadingTimeChanged(
                            (state.readingTime ?: 0) + (tempString.toIntOrNull() ?: 0)
                        )
                    )
                    EditType.BOOK_SERIES -> onAction(DetailsAction.SeriesChanged(tempSeries))
                    EditType.DESCRIPTION -> onAction(DetailsAction.DescriptionChanged(tempString))
                }
                showEditDialog = false
            }
        }

        CustomDialog (
            title = {
                Text(text = stringResource(
                    Res.string.edit_title,
                    stringResource(curEditType.getTypeStringResource)
                ))
            },
            onDismissRequest = { showEditDialog = false },
            rightIcons = {
                Row {
                    if (curEditType == EditType.READING_TIME) {
                        Icon (
                            imageVector = CustomRemoveIcon,
                            contentDescription = "Subtract Time",
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .clickable {
                                    onAction(
                                        DetailsAction.ReadingTimeChanged(
                                            ((state.readingTime ?: 0) - (tempString.toIntOrNull() ?: 0)).coerceAtLeast(0)
                                        ))
                                    showEditDialog = false
                                }
                        )
                    }
                    Icon (
                        imageVector = if (curEditType == EditType.READING_TIME) {
                            Icons.Default.AddCircle
                        } else {
                            Icons.Default.Check
                        },
                        contentDescription = "finish",
                        modifier = Modifier
                            .clickable {
                                onDoneClicked()
                            },
                        tint = if (readyToFinish) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            },
        ) {
            when (curEditType) {
                EditType.STATUS -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    )  {
                        Text(
                            text = "${stringResource(Res.string.owned)}:",
                            modifier = Modifier
                                .weight(0.4f)
                        )
                        IconButton(
                            onClick = { tempStatus = tempStatus.copy(first = !tempStatus.first) },
                            modifier = Modifier
                                .weight(0.6f),
                        ) {
                            Icon(
                                imageVector = CustomBookIcon,
                                contentDescription = "Possession Status",
                                tint = if (tempStatus.first) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${stringResource(Res.string.read)}:",
                            modifier = Modifier
                                .weight(0.4f)
                        )
                        IconButton(
                            onClick = { tempStatus = tempStatus.copy(second = !tempStatus.second) },
                            modifier = Modifier
                                .weight(0.6f),
                        ) {
                            Icon(
                                imageVector = customReadIcon(),
                                contentDescription = "Read Status",
                                tint = if (tempStatus.second) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
                EditType.BOOK_SERIES -> {
                    val options: Map<Any?, String> = state.bookSeriesList.associateBy({ it as BookSeries? }, { it.title })
                        .toMutableMap()
                        .apply { put(null, "–") }
                        .toMap()

                    SeriesDropdown(
                        selectedOption = Pair<Any?, String>(tempSeries, tempSeries?.title ?: "–"),
                        options = options,
                        onValueChanged = { newSeries ->
                            tempSeries = newSeries as BookSeries?
                        },
                        onValueAdded = { newSeriesName ->
                            tempSeries = BookSeries(
                                id = 0L,
                                title = newSeriesName,
                            )
                        },
                        label = stringResource(
                            Res.string.new_label,
                            stringResource(curEditType.getTypeStringResource)
                        ),
                    )
                }
                EditType.COVER_IMAGE -> {
                    OutlinedTextField(
                        value = tempString,
                        onValueChange = { tempString = it },
                        label = { Text(text = stringResource(
                            Res.string.new_label,
                            stringResource(curEditType.getTypeStringResource)),
                        ) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onDoneClicked()
                            }
                        )
                    )
                }
                else -> {
                    OutlinedTextField(
                        value = tempString,
                        onValueChange = { tempString = it },
                        label = { Text(text = stringResource(
                            Res.string.new_label,
                            stringResource(curEditType.getTypeStringResource)),
                        ) },
                        suffix = { when (curEditType) {
                            EditType.PRICE
                                -> Text(text = "EUR") // TODO: Add currency selection
                            EditType.READING_TIME
                                -> Text(text = stringResource(Res.string.minutes_short))
                            EditType.ISBN, EditType.TITLE, EditType.AUTHOR, EditType.PAGE_COUNT,
                            EditType.STATUS, EditType.BOOK_SERIES, EditType.DESCRIPTION, EditType.COVER_IMAGE
                                -> Text(text = "")
                        } },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = when (curEditType) {
                                EditType.ISBN, EditType.PAGE_COUNT, EditType.PRICE, EditType.READING_TIME
                                    -> KeyboardType.Number
                                EditType.TITLE, EditType.AUTHOR, EditType.DESCRIPTION
                                    -> KeyboardType.Text
                                EditType.STATUS, EditType.BOOK_SERIES, EditType.COVER_IMAGE
                                    -> throw IllegalStateException("Something went weirdly wrong")
                            },
                            imeAction = when(curEditType) {
                                EditType.ISBN, EditType.TITLE, EditType.AUTHOR, EditType.PAGE_COUNT,
                                EditType.PRICE, EditType.READING_TIME
                                    -> ImeAction.Done
                                EditType.DESCRIPTION
                                    -> ImeAction.Default
                                EditType.STATUS, EditType.BOOK_SERIES, EditType.COVER_IMAGE
                                    -> throw IllegalStateException("Something went weirdly wrong")
                            }
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onDoneClicked()
                            }
                        )
                    )
                }
            }
        }
    }

    BlurredImageBackground(
        imageUrl = state.imageUrl.replace("http://", "https://"),
        onBackClick = { onDismiss() },
        rightIcons = @Composable {
            IconButton(
                onClick = { uriHandler.openUri("https://www.thalia.de/suche?sq=" + state.isbn.ifBlank { state.title }) }
            ) {
                Icon(
                    imageVector = CustomOpenInBrowserIcon,
                    contentDescription = "Open in Browser",
                )
            }
            if (!state.isEditing) {
                IconButton(onClick = {
                    onAction(DetailsAction.SwitchEditing)
                }) {
                    Icon(
                        imageVector =  Icons.Filled.Edit,
                        contentDescription = stringResource(Res.string.edit),
                    )
                }
            } else {
                IconButton(onClick = {
                    if (state.title.isBlank()) {
                        showNoTitleDialog = true
                    } else {
                        if (state.somethingChanged) {
                            onAction(DetailsAction.UpdateBook)
                        }
                        onAction(DetailsAction.SwitchEditing)
                    }
                }) {
                    Icon(
                        imageVector =  Icons.Filled.Check,
                        contentDescription = stringResource(Res.string.save),
                    )
                }
            }
            val deleteClicked = remember { mutableStateOf(false) }
            LaunchedEffect(deleteClicked.value) {
                if (deleteClicked.value) {
                    delay(3000)
                    deleteClicked.value = false
                }
            }
            IconButton(
                onClick = {
                    if (!deleteClicked.value) {
                        deleteClicked.value = true
                    } else {
                        onAction(DetailsAction.DeleteBook)
                        deleteClicked.value = false
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Recipe",
                    tint = if (deleteClicked.value) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurface
                )
            }
        },
        onImageClick = {
            if (state.isEditing) {
                curEditType = EditType.COVER_IMAGE
                showEditDialog = true
            }
        },
        onImageLongClick = {
            if (!state.isEditing) onAction(DetailsAction.SwitchEditing)
            curEditType = EditType.COVER_IMAGE
            showEditDialog = true
        },
        errorImageId = (state.book?.bookSeries?.id?.rem(5) ?: state.book?.id?.rem(5))?.toInt() ?: nextInt(5),
    ) {
        Column(
            modifier = Modifier
                .padding(
                    horizontal = 8.dp
                )
                .padding(
                    top = 8.dp
                )
                .fillMaxSize()
        ) {
            state.book?.let { book ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(
                            state = rememberScrollState()
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // ISBN + Title + Author +  Rating --------------------------------------------
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                bottom = 8.dp,
                                start = 16.dp,
                                end = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.isbn,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (state.isbnChanged) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = {
                                        if (state.isEditing) {
                                            curEditType = EditType.ISBN
                                            showEditDialog = true
                                        }
                                    },
                                    onLongClick = {
                                        if (!state.isEditing) onAction(DetailsAction.SwitchEditing)
                                        curEditType = EditType.ISBN
                                        showEditDialog = true
                                    }
                                ),
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(0.6f),
                        ) {
                            RatingBar(
                                current = state.rating,
                                onRatingChanged = { newRating ->
                                    onAction(DetailsAction.RatingChanged(newRating))
                                },
                                enabled = state.isEditing,
                                showText = false,
                                activeColor = if (state.ratingChanged) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.tertiary,
                                inactiveColor = if (state.ratingChanged) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            )
                        }
                        Text(
                            text = state.title,
                            style = MaterialTheme.typography.headlineSmall,
                            color = if (state.titleChanged) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable (
                                    onClick = {
                                        if (state.isEditing) {
                                            curEditType = EditType.TITLE
                                            showEditDialog = true
                                        }
                                    },
                                    onLongClick = {
                                        if (!state.isEditing) onAction(DetailsAction.SwitchEditing)
                                        curEditType = EditType.TITLE
                                        showEditDialog = true
                                    }
                                ),
                        )
                        Text(
                            text = stringResource(Res.string.details_by, state.author),
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (state.authorChanged) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable (
                                    onClick = {
                                        if (state.isEditing) {
                                            curEditType = EditType.AUTHOR
                                            showEditDialog = true
                                        }
                                    },
                                    onLongClick = {
                                        onAction(DetailsAction.OnAuthorSearch(state.author))
                                    }
                                )
                        )
                    }
                    // Pages, Price, Status -------------------------------------------------------
                    Row(
                        modifier = Modifier
                            .padding(
                                vertical = 8.dp,
                                horizontal = 16.dp
                            ),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        TitledContent(
                            title = stringResource(Res.string.pages),
                            color = if (state.pagesChanged) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .combinedClickable (
                                    onClick = {
                                        if (state.isEditing) {
                                            curEditType = EditType.PAGE_COUNT
                                            showEditDialog = true
                                        }
                                    },
                                    onLongClick = {
                                        if (!state.isEditing) onAction(DetailsAction.SwitchEditing)
                                        curEditType = EditType.PAGE_COUNT
                                        showEditDialog = true
                                    }
                                ),
                        ) {
                            BookChip {
                                Text(
                                    text = state.pageCount?.toString() ?: "–",
                                )
                            }
                        }
                        TitledContent(
                            title = stringResource(Res.string.price),
                            color = if (state.priceChanged) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .combinedClickable (
                                    onClick = {
                                        if (state.isEditing) {
                                            curEditType = EditType.PRICE
                                            showEditDialog = true
                                        }
                                    },
                                    onLongClick = {
                                        if (!state.isEditing) onAction(DetailsAction.SwitchEditing)
                                        curEditType = EditType.PRICE
                                        showEditDialog = true
                                    }
                                ),
                        ) {
                            BookChip {
                                Text(
                                    text = state.price?.toPriceString(state.currency ?: "EUR") ?: "–",
                                )
                            }
                        }
                        TitledContent(
                            title = stringResource(Res.string.status),
                            color = if (state.statusChanged) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .combinedClickable (
                                    onClick = {
                                        if (state.isEditing) {
                                            curEditType = EditType.STATUS
                                            showEditDialog = true
                                        }
                                    },
                                    onLongClick = {
                                        if (!state.isEditing) onAction(DetailsAction.SwitchEditing)
                                        curEditType = EditType.STATUS
                                        showEditDialog = true
                                    }
                                ),
                        ) {
                            BookChip {
                                Icon(
                                    imageVector = CustomBookIcon,
                                    contentDescription = "Possession Status",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                        alpha = if (state.possessionStatus) 1f else 0.4f
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = customReadIcon(),
                                    contentDescription = "Read Status",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                        alpha = if (state.readStatus) 1f else 0.4f
                                    )
                                )
                            }
                        }
                    }
                    // Reading Time, BookSeries ---------------------------------------------------
                    Row(
                        modifier = Modifier
                            .padding(
                                vertical = 8.dp,
                                horizontal = 16.dp
                            ),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        var perPageCounter by remember { mutableStateOf(1) }
                        LaunchedEffect(Unit) {
                            while (true) {
                                delay(1000L)
                                perPageCounter = (perPageCounter + 1) % 4
                            }
                        }

                        var showPerPage by remember { mutableStateOf(false) }
                        LaunchedEffect(perPageCounter) {
                            if (perPageCounter == 0) {
                                showPerPage = !showPerPage
                            }
                        }

                        TitledContent(
                            title = stringResource(Res.string.reading_time),
                            color = if (state.readingTimeChanged) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .combinedClickable (
                                    onClick = {
                                        if (state.isEditing) {
                                            curEditType = EditType.READING_TIME
                                            showEditDialog = true
                                        } else {
                                            perPageCounter = 0
                                        }
                                    },
                                    onLongClick = {
                                        if (!state.isEditing) onAction(DetailsAction.SwitchEditing)
                                        curEditType = EditType.READING_TIME
                                        showEditDialog = true
                                    }
                                ),
                        ) {
                            BookChip(
                                size = ChipSize.LARGE
                            ) {
                                Text(
                                    text = if (state.pageCount != null && showPerPage)
                                        state.readingTime?.toReadingTimePerPageString(state.pageCount) ?: "–"
                                    else state.readingTime?.toReadingTimeString() ?: "–",
                                )
                            }
                        }
                        TitledContent(
                            title = stringResource(Res.string.series),
                            color = if (state.seriesChanged) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .combinedClickable (
                                    onClick = {
                                        if (state.isEditing) {
                                            curEditType = EditType.BOOK_SERIES
                                            showEditDialog = true
                                        }
                                    },
                                    onLongClick = {
                                        if (!state.isEditing) onAction(DetailsAction.SwitchEditing)
                                        curEditType = EditType.BOOK_SERIES
                                        showEditDialog = true
                                    }
                                ),
                        ) {
                            BookChip(
                                size = ChipSize.LARGE
                            ) {
                                Text(
                                    text = state.series?.title ?: "–",
                                )
                            }
                        }
                    }
                    // Descriptions ---------------------------------------------------------------
                    Column(
                        modifier = Modifier
                            .padding(
                                vertical = 8.dp,
                                horizontal = 8.dp,
                            )
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = {
                                    if (state.isEditing) {
                                        curEditType = EditType.DESCRIPTION
                                        showEditDialog = true
                                    }
                                },
                                onLongClick = {
                                    if (!state.isEditing) onAction(DetailsAction.SwitchEditing)
                                    curEditType = EditType.DESCRIPTION
                                    showEditDialog = true
                                }
                            )
                    ) {
                        if (state.description.isBlank() && book.onlineDescription == null) {
                            Text(
                                text = stringResource(Res.string.no_description_available),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                        AnimatedVisibility(
                            visible = state.description.isNotBlank(),
                            enter = expandVertically(),
                            exit = shrinkVertically(),
                        ) {
                            Column {
                                Text(
                                    text = stringResource(Res.string.own_description),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (state.descriptionChanged) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .padding(
                                            bottom = 4.dp,
                                            start = 8.dp,
                                            end = 8.dp,
                                        )
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                ) {
                                    Text(
                                        text = state.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier
                                            .padding(8.dp)
                                    )
                                }
                            }
                        }
                        if (book.onlineDescription != null) {
                            Column {
                                Text(
                                    text = stringResource(Res.string.online_description),
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier
                                        .padding(
                                            top = 8.dp,
                                            bottom = 4.dp,
                                            start = 8.dp,
                                            end = 8.dp,
                                        )
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                ) {
                                    Text(
                                        text = book.onlineDescription,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier
                                            .padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } ?: run {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun Int.toReadingTimeString() : String {
    val minutes: Int = this % 60
    val hours: Int = (this - minutes) / 60
    return if (minutes < 10) "$hours:0$minutes ${stringResource(Res.string.hours_short)}"
    else "$hours:$minutes ${stringResource(Res.string.hours_short)}"
}

fun Double.toPriceString(currency: String?) : String {
    val price = this.let { value ->
        val splits = value.toString().split(".")
        "${splits[0]}.${splits[1].take(2).padEnd(2, '0')}"
    }

    return when (currency) {
        null -> price
        "EUR" -> "$price €"
        "USD" -> "$price $"
        "GBP" -> "$price £"
        else -> "$price $currency"
    }
}

@Composable
fun Int.toReadingTimePerPageString(pageCount: Int) : String {
    if (pageCount <= 0) return "– ${stringResource(Res.string.min_per_page)}"
    val minsPerPage = this / pageCount.toDouble()
    val minutes: Int = minsPerPage.toInt()
    val seconds: Int = ((minsPerPage - minutes) * 60).toInt()

    return if (seconds < 10) "$minutes:0$seconds ${stringResource(Res.string.min_per_page)}"
        else "$minutes:$seconds ${stringResource(Res.string.min_per_page)}"
}