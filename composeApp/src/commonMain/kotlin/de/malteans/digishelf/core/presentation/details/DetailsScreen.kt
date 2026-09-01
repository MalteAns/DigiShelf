package de.malteans.digishelf.core.presentation.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.digishelf.core.domain.Book
import de.malteans.digishelf.core.presentation.components.CustomAlertDialog
import de.malteans.digishelf.core.presentation.components.CustomBookIcon
import de.malteans.digishelf.core.presentation.components.customReadIcon
import de.malteans.digishelf.core.presentation.details.components.BlurredImageBackground
import de.malteans.digishelf.core.presentation.details.components.CustomOpenInBrowserIcon
import de.malteans.digishelf.core.presentation.details.components.DetailsEditCallbacks
import de.malteans.digishelf.core.presentation.details.components.DetailsEditDialog
import de.malteans.digishelf.core.presentation.details.components.DetailsEditValues
import de.malteans.digishelf.core.presentation.details.components.DetailsInfoChip
import de.malteans.digishelf.core.presentation.details.components.DetailsMoodLevelRow
import de.malteans.digishelf.core.presentation.details.components.DetailsRatingBar
import de.malteans.digishelf.core.presentation.details.components.EditType
import de.malteans.digishelf.core.presentation.details.components.ImagePicker
import de.malteans.digishelf.theme.DigiShelfTheme
import digishelf.composeapp.generated.resources.Res
import digishelf.composeapp.generated.resources.chapter_length
import digishelf.composeapp.generated.resources.delete_book
import digishelf.composeapp.generated.resources.details_by
import digishelf.composeapp.generated.resources.ebook_status
import digishelf.composeapp.generated.resources.edit
import digishelf.composeapp.generated.resources.emotion_level
import digishelf.composeapp.generated.resources.ending_rating
import digishelf.composeapp.generated.resources.error
import digishelf.composeapp.generated.resources.error_msg_no_title
import digishelf.composeapp.generated.resources.error_msg_save_changes
import digishelf.composeapp.generated.resources.error_save_changes
import digishelf.composeapp.generated.resources.hours_short
import digishelf.composeapp.generated.resources.ic_tablet
import digishelf.composeapp.generated.resources.min_per_page
import digishelf.composeapp.generated.resources.no_description_available
import digishelf.composeapp.generated.resources.online_description
import digishelf.composeapp.generated.resources.open_in_browser
import digishelf.composeapp.generated.resources.own_description
import digishelf.composeapp.generated.resources.pages_short
import digishelf.composeapp.generated.resources.plot_rating
import digishelf.composeapp.generated.resources.possession_status
import digishelf.composeapp.generated.resources.rating
import digishelf.composeapp.generated.resources.read_status
import digishelf.composeapp.generated.resources.save
import digishelf.composeapp.generated.resources.spice_level
import digishelf.composeapp.generated.resources.status
import digishelf.composeapp.generated.resources.tension_level
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.seconds
import androidx.compose.ui.text.font.FontWeight as FontWeightCompose

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

    var showEditDialog by remember { mutableStateOf(false) }
    var curEditType by remember { mutableStateOf(EditType.TITLE) }

    fun onDismiss() {
        if (state.isEditing && state.somethingChanged) showConfirmLeaveDialog = true
        else onAction(DetailsAction.OnBack)
    }

    BackHandler {
        onDismiss()
    }

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
                rating = state.rating,
                tensionLevel = state.tensionLevel,
                spiceLevel = state.spiceLevel,
                emotionLevel = state.emotionLevel,
                chapterLength = state.chapterLength,
                endingRating = state.endingRating,
                plotRating = state.plotRating,
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
                onRatingChanged = { onAction(DetailsAction.RatingChanged(it)) },
                onTensionLevelChanged = { onAction(DetailsAction.TensionLevelChanged(it)) },
                onSpiceLevelChanged = { onAction(DetailsAction.SpiceLevelChanged(it)) },
                onEmotionLevelChanged = { onAction(DetailsAction.EmotionLevelChanged(it)) },
                onChapterLengthChanged = { onAction(DetailsAction.ChapterLengthChanged(it)) },
                onEndingRatingChanged = { onAction(DetailsAction.EndingRatingChanged(it)) },
                onPlotRatingChanged = { onAction(DetailsAction.PlotRatingChanged(it)) },
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

    val scrollState = rememberScrollState()

    BlurredImageBackground(
        imageUrl = state.imageUrl.replace("http://", "https://"),
        onBackClick = { onDismiss() },
        scrollState = scrollState,
        isEditing = state.isEditing,
        rightIcons = @Composable {
            val backgroundAlpha = remember { Animatable(0f) }
            LaunchedEffect(scrollState.canScrollBackward) {
                if (scrollState.canScrollBackward) {
                    backgroundAlpha.animateTo(0.5f)
                } else {
                    backgroundAlpha.animateTo(0f)
                }
            }

            IconButton({ uriHandler.openUri("https://www.thalia.de/suche?sq=" + state.isbn.ifBlank { state.title }) }) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = backgroundAlpha.value }
                        .background(MaterialTheme.colorScheme.surface)
                )
                Icon(
                    imageVector = CustomOpenInBrowserIcon,
                    contentDescription = stringResource(Res.string.open_in_browser),
                )
            }
            if (!state.isEditing) {
                IconButton({ onAction(DetailsAction.SwitchEditing) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { alpha = backgroundAlpha.value }
                            .background(MaterialTheme.colorScheme.surface)
                    )
                    Icon(
                        imageVector =  Icons.Filled.Edit,
                        contentDescription = stringResource(Res.string.edit),
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        if (state.title.isBlank()) {
                            showNoTitleDialog = true
                        } else {
                            if (state.somethingChanged) {
                                onAction(DetailsAction.UpdateBook)
                            }
                            onAction(DetailsAction.SwitchEditing)
                        }
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { alpha = backgroundAlpha.value }
                            .background(MaterialTheme.colorScheme.surface)
                    )
                    Icon(
                        imageVector =  Icons.Filled.Check,
                        contentDescription = stringResource(Res.string.save),
                        tint = if (state.somethingChanged) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            val deleteClicked = remember { mutableStateOf(false) }
            LaunchedEffect(deleteClicked.value) {
                if (deleteClicked.value) {
                    delay(3.seconds)
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = backgroundAlpha.value }
                        .background(MaterialTheme.colorScheme.surface)
                )
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(Res.string.delete_book),
                    tint = if (deleteClicked.value) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface
                )
            }
        },
        onImageClick = if (state.isEditing) {
            {
                curEditType = EditType.COVER_IMAGE
                showEditDialog = true
            }
        } else null,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            state.book?.let { book ->
                // 1. Hero Card (Title, Author, Rating)
                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.isbn,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (state.isbnChanged) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(
                                    if (state.isEditing) {
                                        Modifier.clickable {
                                            curEditType = EditType.ISBN
                                            showEditDialog = true
                                        }
                                    } else Modifier
                                )
                        )
                        Text(
                            text = state.title,
                            style = MaterialTheme.typography.headlineSmall,
                            color = if (state.titleChanged) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeightCompose.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(
                                    if (state.isEditing) {
                                        Modifier.clickable {
                                            curEditType = EditType.ISBN
                                            showEditDialog = true
                                        }
                                    } else Modifier
                                )
                        )
                        Text(
                            text = stringResource(Res.string.details_by, state.author),
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (state.authorChanged) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.then(
                                if (state.isEditing) {
                                    Modifier.clickable {
                                        curEditType = EditType.AUTHOR
                                        showEditDialog = true
                                    }
                                } else Modifier
                            )
                        )
                        Spacer(Modifier.height(12.dp))
                        DetailsRatingBar(
                            current = state.rating,
                            onLevelChanged = { onAction(DetailsAction.RatingChanged(it)) },
                            enabled = state.isEditing,
                            activeColor = if (state.ratingChanged) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.tertiary,
                            activeIcon = Icons.Filled.Star,
                            inactiveIcon = Icons.Outlined.Star,
                            label = stringResource(Res.string.rating)
                        )
                    }
                }

                // 2. Metadata Chips (PageCount, Price, Series, Reading Time, Status)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val smallChipModifier = Modifier.weight(1f).widthIn(min = 110.dp)
                    DetailsInfoChip(
                        label = "${state.pageCount ?: "–"} ${stringResource(Res.string.pages_short)}",
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        color = if (state.pagesChanged) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        onClick = {
                            if (state.isEditing) {
                                curEditType = EditType.PAGE_COUNT
                                showEditDialog = true
                            }
                        },
                        modifier = smallChipModifier
                    )
                    DetailsInfoChip(
                        label = state.price?.toPriceString(state.currency ?: "EUR") ?: "–",
                        icon = Icons.Default.Payments,
                        color = if (state.priceChanged) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        onClick = {
                            if (state.isEditing) {
                                curEditType = EditType.PRICE
                                showEditDialog = true
                            }
                        },
                        modifier = smallChipModifier
                    )
                    DetailsInfoChip(
                        label = stringResource(Res.string.status),
                        color = if (state.statusChanged) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        onClick = {
                            if (state.isEditing) {
                                curEditType = EditType.STATUS
                                showEditDialog = true
                            }
                        },
                        content = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = CustomBookIcon,
                                    contentDescription = stringResource(Res.string.possession_status),
                                    tint = if (state.possessionStatus) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Icon(
                                    imageVector = customReadIcon(),
                                    contentDescription = stringResource(Res.string.read_status),
                                    tint = if (state.readStatus) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Icon(
                                    imageVector = vectorResource(Res.drawable.ic_tablet),
                                    contentDescription = stringResource(Res.string.ebook_status),
                                    tint = if (state.ebookStatus) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        modifier = smallChipModifier
                    )
                    var perPageCounter by remember { mutableStateOf(1) }
                    var showPerPage by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        while (true) {
                            delay(1.seconds)
                            perPageCounter = (perPageCounter + 1) % 4
                        }
                    }
                    LaunchedEffect(perPageCounter) {
                        if (perPageCounter == 0) showPerPage = !showPerPage
                    }
                    DetailsInfoChip(
                        label = if (state.pageCount != null && showPerPage)
                            state.readingTime?.toReadingTimePerPageString(state.pageCount) ?: "–"
                        else state.readingTime?.toReadingTimeString() ?: "–",
                        icon = Icons.Default.Timer,
                        color = if (state.readingTimeChanged) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        onClick = {
                            if (state.isEditing) {
                                curEditType = EditType.READING_TIME
                                showEditDialog = true
                            } else {
                                if (perPageCounter == 0) showPerPage = !showPerPage
                                else perPageCounter = 0
                            }
                        },
                        modifier = smallChipModifier
                    )
                    DetailsInfoChip(
                        label = state.series?.title ?: "–",
                        icon = Icons.AutoMirrored.Filled.LibraryBooks,
                        color = if (state.seriesChanged) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        onClick = {
                            if (state.isEditing) {
                                curEditType = EditType.BOOK_SERIES
                                showEditDialog = true
                            } else {
                                // TODO: Open Series
                            }
                        },
                        modifier = smallChipModifier
                    )
                }

                // 3. Mood Levels (Tension, Spice, Emotion, Chapter Length, Ending, Plot)
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        DetailsMoodLevelRow(
                            label = stringResource(Res.string.tension_level),
                            value = state.tensionLevel,
                            icon = Icons.Filled.ElectricBolt,
                            color = if (state.tensionLevelChanged) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            onValueChange = { onAction(DetailsAction.TensionLevelChanged(it)) },
                            enabled = state.isEditing
                        )
                        DetailsMoodLevelRow(
                            label = stringResource(Res.string.spice_level),
                            value = state.spiceLevel,
                            icon = Icons.Filled.LocalFireDepartment,
                            color = if (state.spiceLevelChanged) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            onValueChange = { onAction(DetailsAction.SpiceLevelChanged(it)) },
                            enabled = state.isEditing
                        )
                        DetailsMoodLevelRow(
                            label = stringResource(Res.string.emotion_level),
                            value = state.emotionLevel,
                            icon = Icons.Filled.WaterDrop,
                            color = if (state.emotionLevelChanged) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            onValueChange = { onAction(DetailsAction.EmotionLevelChanged(it)) },
                            enabled = state.isEditing
                        )
                        DetailsMoodLevelRow(
                            label = stringResource(Res.string.chapter_length),
                            value = state.chapterLength,
                            icon = Icons.Filled.HourglassBottom,
                            color = if (state.chapterLengthChanged) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            onValueChange = { onAction(DetailsAction.ChapterLengthChanged(it)) },
                            enabled = state.isEditing
                        )
                        DetailsMoodLevelRow(
                            label = stringResource(Res.string.ending_rating),
                            value = state.endingRating,
                            icon = Icons.Filled.Flag,
                            color = if (state.endingRatingChanged) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            onValueChange = { onAction(DetailsAction.EndingRatingChanged(it)) },
                            enabled = state.isEditing
                        )
                        DetailsMoodLevelRow(
                            label = stringResource(Res.string.plot_rating),
                            value = state.plotRating,
                            icon = Icons.Filled.Terrain,
                            color = if (state.plotRatingChanged) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            onValueChange = { onAction(DetailsAction.PlotRatingChanged(it)) },
                            enabled = state.isEditing
                        )
                    }
                }

                // 4. Descriptions
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (state.isEditing) {
                                Modifier.clickable {
                                    curEditType = EditType.DESCRIPTION
                                    showEditDialog = true
                                }
                            } else Modifier
                        )
                ) {
                    if (state.description.isBlank() && book.onlineDescription == null) {
                        Text(
                            text = stringResource(Res.string.no_description_available),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
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
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp, start = 8.dp)
                            )
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                )
                            ) {
                                Text(
                                    text = state.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                    if (book.onlineDescription != null) {
                        Column {
                            Text(
                                text = stringResource(Res.string.online_description),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp, start = 8.dp)
                            )
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                )
                            ) {
                                Text(
                                    text = book.onlineDescription,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxWidth().height(400.dp),
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

@Preview
@Composable
fun DetailsScreenPreview() {
    val mockBook = Book(
        title = "The Great Gatsby",
        author = "F. Scott Fitzgerald",
        isbn = "9780743273565",
        rating = 4,
        tensionLevel = 3,
        spiceLevel = 1,
        emotionLevel = 5,
        chapterLength = 4,
        endingRating = 5,
        plotRating = 4,
        pageCount = 180,
        price = 12.99,
        readingTime = 150,
        description = "A classic novel about the American Dream in the Roaring Twenties. It tells the story of Jay Gatsby and his unrequited love for Daisy Buchanan.",
        imageUrl = ""
    )

    var editMode by remember { mutableStateOf(false) }

    val mockState by remember(editMode) { mutableStateOf( DetailsState(
        book = mockBook,
        title = mockBook.title,
        author = mockBook.author,
        isbn = mockBook.isbn,
        rating = mockBook.rating ?: 0,
        tensionLevel = mockBook.tensionLevel ?: 0,
        spiceLevel = mockBook.spiceLevel ?: 0,
        emotionLevel = mockBook.emotionLevel ?: 0,
        chapterLength = mockBook.chapterLength ?: 0,
        endingRating = mockBook.endingRating ?: 0,
        plotRating = mockBook.plotRating ?: 0,
        pageCount = mockBook.pageCount,
        price = mockBook.price,
        readingTime = mockBook.readingTime,
        description = mockBook.description,
        imageUrl = mockBook.imageUrl,

        isEditing = editMode
    ) ) }

    DigiShelfTheme(useDarkTheme = true) {
        DetailsScreen(
            state = mockState,
            onAction = { action ->
                when (action) {
                    DetailsAction.SwitchEditing -> editMode = !editMode
                    else -> {}
                }
            }
        )
    }
}
