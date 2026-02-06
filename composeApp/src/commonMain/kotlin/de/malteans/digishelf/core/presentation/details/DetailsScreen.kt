package de.malteans.digishelf.core.presentation.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.digishelf.core.presentation.add.components.RatingBar
import de.malteans.digishelf.core.presentation.components.CustomAlertDialog
import de.malteans.digishelf.core.presentation.components.CustomBookIcon
import de.malteans.digishelf.core.presentation.components.customReadIcon
import de.malteans.digishelf.core.presentation.details.components.*
import digishelf.composeapp.generated.resources.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
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
        if (state.isEditing && state.somethingChanged) showConfirmLeaveDialog = true
        else onAction(DetailsAction.OnBack)
    }

    BackHandler {
        onDismiss()
    }

    var showEditDialog by remember { mutableStateOf(false) }
    var curEditType by remember { mutableStateOf(EditType.TITLE) }

    if (showConfirmLeaveDialog) {
        CustomAlertDialog(
            title = stringResource(Res.string.error_save_changes),
            text = stringResource(Res.string.error_msg_save_changes),
            onDismiss = {
                showConfirmLeaveDialog = false
                onAction(DetailsAction.OnBack)
            },
            onConfirm = {
                if (state.title.isBlank()) {
                    showConfirmLeaveDialog = false
                    showNoTitleDialog = true
                } else {
                    onAction(DetailsAction.UpdateBook)
                    onAction(DetailsAction.OnBack)
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            )
        )
    }

    if (showNoTitleDialog) {
        CustomAlertDialog(
            title = stringResource(Res.string.error),
            text = stringResource(Res.string.error_msg_no_title),
            onDismiss = { showNoTitleDialog = false },
            onConfirm = { showNoTitleDialog = false }
        )
    }

    if (showEditDialog) {
        DetailsEditDialog(
            curEditType = curEditType,
            values = DetailsEditValues(
                imageUrl = state.imageUrl,
                isbn = state.isbn,
                title = state.title,
                author = state.author,
                pageCount = state.pageCount,
                price = state.price,
                description = state.description,
                readStatus = state.readStatus,
                readingTime = state.readingTime,
                possessionStatus = state.possessionStatus,
                ebookStatus = state.ebookStatus,
                series = state.series,
                bookSeriesList = state.bookSeriesList,
            ),
            callbacks = DetailsEditCallbacks(
                onImageUrlChanged = { onAction(DetailsAction.ImageUrlChanged(it)) },
                onIsbnChanged = { onAction(DetailsAction.IsbnChanged(it)) },
                onTitleChanged = { onAction(DetailsAction.TitleChanged(it)) },
                onAuthorChanged = { onAction(DetailsAction.AuthorChanged(it)) },
                onPageCountChanged = { onAction(DetailsAction.PageCountChanged(it)) },
                onPriceChanged = { onAction(DetailsAction.PriceChanged(it)) },
                onStatusChanged = { owned, read, ebook -> onAction(DetailsAction.StatusChanged(owned, read, ebook)) },
                onReadingTimeChanged = { onAction(DetailsAction.ReadingTimeChanged(it)) },
                onSeriesChanged = { onAction(DetailsAction.SeriesChanged(it)) },
                onDescriptionChanged = { onAction(DetailsAction.DescriptionChanged(it)) },
            ),
            onOpenImagePicker = { onResult ->
                ImagePicker(onResult)
            },
            onClose = { showEditDialog = false },
        )
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
                        RatingBar(
                            current = state.rating,
                            onRatingChanged = { newRating -> onAction(DetailsAction.RatingChanged(newRating)) },
                            enabled = state.isEditing,
                            activeColor = if (state.ratingChanged) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.tertiary,
                            inactiveColor = if (state.ratingChanged) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            modifier = Modifier.fillMaxWidth(0.6f)
                        )
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
                                .clip(MaterialTheme.shapes.large)
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
                                .clip(MaterialTheme.shapes.large)
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
                                .clip(MaterialTheme.shapes.large)
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
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = vectorResource(Res.drawable.ic_tablet),
                                    contentDescription = "eBook Status",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                        alpha = if (state.ebookStatus) 1f else 0.4f
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
                                .clip(MaterialTheme.shapes.large)
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
                                .clip(MaterialTheme.shapes.large)
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
                            BookChip(size = ChipSize.LARGE) {
                                Text(
                                    text = state.series?.title ?: "–",
                                    textAlign = TextAlign.Center,
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
