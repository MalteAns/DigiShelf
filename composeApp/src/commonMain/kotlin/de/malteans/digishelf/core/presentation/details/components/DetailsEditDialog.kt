package de.malteans.digishelf.core.presentation.details.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.malteans.digishelf.core.domain.BookSeries
import de.malteans.digishelf.core.presentation.add.isIsbnFormat
import de.malteans.digishelf.core.presentation.components.CustomBookIcon
import de.malteans.digishelf.core.presentation.components.CustomDialog
import de.malteans.digishelf.core.presentation.components.customReadIcon
import de.malteans.digishelf.core.presentation.details.toPriceString
import de.malteans.digishelf.core.presentation.overview.components.SeriesDropdown
import digishelf.composeapp.generated.resources.Res
import digishelf.composeapp.generated.resources.done
import digishelf.composeapp.generated.resources.ebook
import digishelf.composeapp.generated.resources.edit_title
import digishelf.composeapp.generated.resources.ic_tablet
import digishelf.composeapp.generated.resources.minutes_short
import digishelf.composeapp.generated.resources.new_label
import digishelf.composeapp.generated.resources.owned
import digishelf.composeapp.generated.resources.pick_image
import digishelf.composeapp.generated.resources.read
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

data class DetailsEditValues(
    val imageUrl: String,
    val isbn: String,
    val title: String,
    val author: String,
    val rating: Int,
    val tensionLevel: Int,
    val spiceLevel: Int,
    val emotionLevel: Int,
    val chapterLength: Int,
    val endingRating: Int,
    val plotRating: Int,
    val pageCount: Int?,
    val price: Double?,
    val description: String,
    val readStatus: Boolean,
    val readingTime: Int?,
    val possessionStatus: Boolean,
    val ebookStatus: Boolean,
    val series: BookSeries?,
    val bookSeriesList: List<BookSeries>,
)

data class DetailsEditCallbacks(
    val onImageUrlChanged: (String) -> Unit,
    val onIsbnChanged: (String) -> Unit,
    val onTitleChanged: (String) -> Unit,
    val onAuthorChanged: (String) -> Unit,
    val onRatingChanged: (Int) -> Unit,
    val onTensionLevelChanged: (Int) -> Unit,
    val onSpiceLevelChanged: (Int) -> Unit,
    val onEmotionLevelChanged: (Int) -> Unit,
    val onChapterLengthChanged: (Int) -> Unit,
    val onEndingRatingChanged: (Int) -> Unit,
    val onPlotRatingChanged: (Int) -> Unit,
    val onPageCountChanged: (Int?) -> Unit,
    val onPriceChanged: (Double?) -> Unit,
    val onStatusChanged: (owned: Boolean, read: Boolean, ebook: Boolean) -> Unit,
    val onReadingTimeChanged: (Int) -> Unit,
    val onSeriesChanged: (BookSeries?) -> Unit,
    val onDescriptionChanged: (String) -> Unit,
)

@Composable
fun DetailsEditDialog(
    curEditType: EditType,
    values: DetailsEditValues,
    callbacks: DetailsEditCallbacks,
    onOpenImagePicker: @Composable (onSelected: (imagePath: String?) -> Unit) -> Unit,
    onClose: () -> Unit,
) {
    var tempString by remember { mutableStateOf("") }
    var tempSeries by remember { mutableStateOf(values.series) }
    var tempStatus by remember { mutableStateOf(StatusValues(values.possessionStatus, values.readStatus, values.ebookStatus)) }
    var tempRating by remember { mutableStateOf(values.rating) }
    var tempTensionLevel by remember { mutableStateOf(values.tensionLevel) }
    var tempSpiceLevel by remember { mutableStateOf(values.spiceLevel) }
    var tempEmotionLevel by remember { mutableStateOf(values.emotionLevel) }
    var tempChapterLength by remember { mutableStateOf(values.chapterLength) }
    var tempEndingRating by remember { mutableStateOf(values.endingRating) }
    var tempPlotRating by remember { mutableStateOf(values.plotRating) }

    var readyToFinish by remember { mutableStateOf(false) }

    LaunchedEffect(curEditType, tempString, tempSeries, tempRating, tempTensionLevel, tempSpiceLevel, tempEmotionLevel, tempChapterLength, tempEndingRating, tempPlotRating) {
        readyToFinish = when (curEditType) {
            EditType.ISBN -> tempString.isIsbnFormat()
            EditType.TITLE, EditType.AUTHOR -> tempString.isNotBlank()
            EditType.PAGE_COUNT -> tempString.let { value ->
                value.isEmpty() || (value.toIntOrNull() != null && value.toInt() >= 0)
            }
            EditType.READING_TIME -> tempString.let { value ->
                value.isNotBlank() && value.toIntOrNull() != null
            }
            EditType.PRICE -> tempString.let { value ->
                value.isEmpty() || (value.replace(",", ".").let {
                    it.toDoubleOrNull() != null && it.toDouble() >= 0
                })
            }
            EditType.COVER_IMAGE, EditType.STATUS, EditType.BOOK_SERIES, EditType.DESCRIPTION -> true
            EditType.RATING, EditType.TENSION_LEVEL, EditType.SPICE_LEVEL, EditType.EMOTION_LEVEL,
            EditType.CHAPTER_LENGTH, EditType.ENDING_RATING, EditType.PLOT_RATING -> true
        }
    }

    LaunchedEffect(key1 = curEditType) {
        tempString = when (curEditType) {
            EditType.COVER_IMAGE -> values.imageUrl
            EditType.ISBN -> values.isbn
            EditType.TITLE -> values.title
            EditType.AUTHOR -> values.author
            EditType.PAGE_COUNT -> values.pageCount?.toString() ?: ""
            EditType.PRICE -> values.price?.toPriceString(null) ?: ""
            EditType.DESCRIPTION -> values.description
            EditType.STATUS, EditType.READING_TIME, EditType.BOOK_SERIES -> ""
            EditType.RATING -> values.rating.toString()
            EditType.TENSION_LEVEL -> values.tensionLevel.toString()
            EditType.SPICE_LEVEL -> values.spiceLevel.toString()
            EditType.EMOTION_LEVEL -> values.emotionLevel.toString()
            EditType.CHAPTER_LENGTH -> values.chapterLength.toString()
            EditType.ENDING_RATING -> values.endingRating.toString()
            EditType.PLOT_RATING -> values.plotRating.toString()
        }
        // Initialize level values when switching to level edit types
        when (curEditType) {
            EditType.RATING -> tempRating = values.rating
            EditType.TENSION_LEVEL -> tempTensionLevel = values.tensionLevel
            EditType.SPICE_LEVEL -> tempSpiceLevel = values.spiceLevel
            EditType.EMOTION_LEVEL -> tempEmotionLevel = values.emotionLevel
            EditType.CHAPTER_LENGTH -> tempChapterLength = values.chapterLength
            EditType.ENDING_RATING -> tempEndingRating = values.endingRating
            EditType.PLOT_RATING -> tempPlotRating = values.plotRating
            else -> {}
        }
    }

    fun onDoneClicked() {
        if (readyToFinish) {
            when (curEditType) {
                EditType.COVER_IMAGE -> callbacks.onImageUrlChanged(tempString)
                EditType.ISBN -> callbacks.onIsbnChanged(tempString)
                EditType.TITLE -> callbacks.onTitleChanged(tempString)
                EditType.AUTHOR -> callbacks.onAuthorChanged(tempString)
                EditType.RATING -> callbacks.onRatingChanged(tempRating)
                EditType.TENSION_LEVEL -> callbacks.onTensionLevelChanged(tempTensionLevel)
                EditType.SPICE_LEVEL -> callbacks.onSpiceLevelChanged(tempSpiceLevel)
                EditType.EMOTION_LEVEL -> callbacks.onEmotionLevelChanged(tempEmotionLevel)
                EditType.CHAPTER_LENGTH -> callbacks.onChapterLengthChanged(tempChapterLength)
                EditType.ENDING_RATING -> callbacks.onEndingRatingChanged(tempEndingRating)
                EditType.PLOT_RATING -> callbacks.onPlotRatingChanged(tempPlotRating)
                EditType.PAGE_COUNT -> callbacks.onPageCountChanged(tempString.toIntOrNull())
                EditType.PRICE -> callbacks.onPriceChanged(tempString.toDoubleOrNull())
                EditType.STATUS -> callbacks.onStatusChanged(tempStatus.owned, tempStatus.read, tempStatus.ebook)
                EditType.READING_TIME -> callbacks.onReadingTimeChanged(
                    (values.readingTime ?: 0) + (tempString.toIntOrNull() ?: 0)
                )

                EditType.BOOK_SERIES -> callbacks.onSeriesChanged(tempSeries)
                EditType.DESCRIPTION -> callbacks.onDescriptionChanged(tempString)
            }
            onClose()
        }
    }

    CustomDialog(
        title = {
            Text(
                text = stringResource(
                    Res.string.edit_title,
                    stringResource(curEditType.getTypeStringResource)
                )
            )
        },
        onDismissRequest = { onClose() },
        rightIcons = {
            IconButton(
                onClick = ::onDoneClicked,
                enabled = readyToFinish,
            ) {
                Icon(
                    imageVector = when(curEditType) {
                        EditType.READING_TIME -> Icons.Default.AddCircle
                        else -> Icons.Default.Check
                    },
                    contentDescription = stringResource(Res.string.done),
                    tint = if (readyToFinish) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        },
    ) {
        when (curEditType) {
            EditType.STATUS -> {
                listOf(
                    StatusEditValues(
                        stringRes = Res.string.owned,
                        icon = CustomBookIcon,
                        currentValue = tempStatus.owned,
                        onValueChange = { tempStatus = tempStatus.copy(owned = it) }
                    ),
                    StatusEditValues(
                        stringRes = Res.string.read,
                        icon = customReadIcon(),
                        currentValue = tempStatus.read,
                        onValueChange = { tempStatus = tempStatus.copy(read = it) }
                    ),
                    StatusEditValues(
                        stringRes = Res.string.ebook,
                        icon = vectorResource(Res.drawable.ic_tablet),
                        currentValue = tempStatus.ebook,
                        onValueChange = { tempStatus = tempStatus.copy(ebook = it) }
                    ),
                ).forEach { statusValues ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${stringResource(statusValues.stringRes)}:",
                            modifier = Modifier
                                .weight(0.4f)
                        )
                        IconButton(
                            onClick = { statusValues.onValueChange(!statusValues.currentValue) },
                            modifier = Modifier
                                .weight(0.6f),
                        ) {
                            Icon(
                                imageVector = statusValues.icon,
                                contentDescription = "${stringResource(statusValues.stringRes)} Status",
                                tint = if (statusValues.currentValue) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            }

            EditType.BOOK_SERIES -> {
                val options: Map<Any?, String> =
                    values.bookSeriesList.associateBy({ it as BookSeries? }, { it.title })
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
                var showImagePicker by remember { mutableStateOf(false) }
                if (showImagePicker) {
                    onOpenImagePicker { imagePath ->
                        if (!imagePath.isNullOrBlank()) {
                            tempString = imagePath
                        }
                        showImagePicker = false
                    }
                }

                OutlinedTextField(
                    value = tempString,
                    onValueChange = { tempString = it },
                    label = {
                        Text(
                            text = stringResource(
                                Res.string.new_label,
                                stringResource(curEditType.getTypeStringResource)
                            ),
                        )
                    },
                    trailingIcon = {
                        IconButton({ showImagePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.FileOpen,
                                contentDescription = stringResource(Res.string.pick_image)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onDoneClicked() }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            EditType.RATING -> {
                LevelSlider(
                    value = tempRating,
                    onValueChange = { tempRating = it },
                    label = stringResource(curEditType.getTypeStringResource)
                )
            }

            EditType.TENSION_LEVEL -> {
                LevelSlider(
                    value = tempTensionLevel,
                    onValueChange = { tempTensionLevel = it },
                    label = stringResource(curEditType.getTypeStringResource)
                )
            }

            EditType.SPICE_LEVEL -> {
                LevelSlider(
                    value = tempSpiceLevel,
                    onValueChange = { tempSpiceLevel = it },
                    label = stringResource(curEditType.getTypeStringResource)
                )
            }

            EditType.EMOTION_LEVEL -> {
                LevelSlider(
                    value = tempEmotionLevel,
                    onValueChange = { tempEmotionLevel = it },
                    label = stringResource(curEditType.getTypeStringResource)
                )
            }

            EditType.CHAPTER_LENGTH -> {
                LevelSlider(
                    value = tempChapterLength,
                    onValueChange = { tempChapterLength = it },
                    label = stringResource(curEditType.getTypeStringResource)
                )
            }

            EditType.ENDING_RATING -> {
                LevelSlider(
                    value = tempEndingRating,
                    onValueChange = { tempEndingRating = it },
                    label = stringResource(curEditType.getTypeStringResource)
                )
            }

            EditType.PLOT_RATING -> {
                LevelSlider(
                    value = tempPlotRating,
                    onValueChange = { tempPlotRating = it },
                    label = stringResource(curEditType.getTypeStringResource)
                )
            }

            else -> {
                OutlinedTextField(
                    value = tempString,
                    onValueChange = { tempString = it },
                    label = {
                        Text(
                            text = stringResource(
                                Res.string.new_label,
                                stringResource(curEditType.getTypeStringResource)
                            ),
                        )
                    },
                    suffix = {
                        when (curEditType) {
                            EditType.PRICE -> Text(text = "EUR") // TODO: Add currency selection
                            EditType.READING_TIME -> Text(text = stringResource(Res.string.minutes_short))
                            EditType.ISBN, EditType.TITLE, EditType.AUTHOR, EditType.PAGE_COUNT,
                            EditType.STATUS, EditType.BOOK_SERIES, EditType.DESCRIPTION, EditType.COVER_IMAGE,
                            EditType.RATING, EditType.TENSION_LEVEL, EditType.SPICE_LEVEL, EditType.EMOTION_LEVEL,
                            EditType.CHAPTER_LENGTH, EditType.ENDING_RATING, EditType.PLOT_RATING
                                -> Text(text = "")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = when (curEditType) {
                            EditType.ISBN, EditType.PAGE_COUNT, EditType.PRICE, EditType.READING_TIME -> KeyboardType.Number
                            EditType.TITLE, EditType.AUTHOR, EditType.DESCRIPTION -> KeyboardType.Text
                            EditType.STATUS, EditType.BOOK_SERIES, EditType.COVER_IMAGE,
                            EditType.RATING, EditType.TENSION_LEVEL, EditType.SPICE_LEVEL, EditType.EMOTION_LEVEL,
                            EditType.CHAPTER_LENGTH, EditType.ENDING_RATING, EditType.PLOT_RATING
                                -> throw IllegalStateException("Something went weirdly wrong")
                        },
                        imeAction = when (curEditType) {
                            EditType.ISBN, EditType.TITLE, EditType.AUTHOR, EditType.PAGE_COUNT,
                            EditType.PRICE, EditType.READING_TIME -> ImeAction.Done

                            EditType.DESCRIPTION -> ImeAction.Default
                            EditType.STATUS, EditType.BOOK_SERIES, EditType.COVER_IMAGE,
                            EditType.RATING, EditType.TENSION_LEVEL, EditType.SPICE_LEVEL, EditType.EMOTION_LEVEL,
                            EditType.CHAPTER_LENGTH, EditType.ENDING_RATING, EditType.PLOT_RATING
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

private data class StatusValues(
    val owned: Boolean,
    val read: Boolean,
    val ebook: Boolean,
)
private data class StatusEditValues(
    val stringRes: StringResource,
    val icon: ImageVector,
    val currentValue: Boolean,
    val onValueChange: (Boolean) -> Unit,
)

@Composable
private fun LevelSlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..5f,
            steps = 4,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        Text(
            text = "$value/5",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
