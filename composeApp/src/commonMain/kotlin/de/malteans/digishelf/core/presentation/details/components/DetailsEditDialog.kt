package de.malteans.digishelf.core.presentation.details.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import de.malteans.digishelf.core.domain.BookSeries
import de.malteans.digishelf.core.presentation.add.isIsbnFormat
import de.malteans.digishelf.core.presentation.components.CustomBookIcon
import de.malteans.digishelf.core.presentation.components.CustomDialog
import de.malteans.digishelf.core.presentation.components.customReadIcon
import de.malteans.digishelf.core.presentation.details.toPriceString
import de.malteans.digishelf.core.presentation.overview.components.SeriesDropdown
import digishelf.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

data class DetailsEditValues(
    val imageUrl: String,
    val isbn: String,
    val title: String,
    val author: String,
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

    var readyToFinish by remember { mutableStateOf(false) }

    LaunchedEffect(curEditType, tempString, tempSeries) {
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
        }
    }

    fun onDoneClicked() {
        if (readyToFinish) {
            when (curEditType) {
                EditType.COVER_IMAGE -> callbacks.onImageUrlChanged(tempString)
                EditType.ISBN -> callbacks.onIsbnChanged(tempString)
                EditType.TITLE -> callbacks.onTitleChanged(tempString)
                EditType.AUTHOR -> callbacks.onAuthorChanged(tempString)
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
                    contentDescription = "Done",
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
                                contentDescription = "Pick Image"
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
                            EditType.STATUS, EditType.BOOK_SERIES, EditType.DESCRIPTION, EditType.COVER_IMAGE
                                -> Text(text = "")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = when (curEditType) {
                            EditType.ISBN, EditType.PAGE_COUNT, EditType.PRICE, EditType.READING_TIME -> KeyboardType.Number
                            EditType.TITLE, EditType.AUTHOR, EditType.DESCRIPTION -> KeyboardType.Text
                            EditType.STATUS, EditType.BOOK_SERIES, EditType.COVER_IMAGE
                                -> throw IllegalStateException("Something went weirdly wrong")
                        },
                        imeAction = when (curEditType) {
                            EditType.ISBN, EditType.TITLE, EditType.AUTHOR, EditType.PAGE_COUNT,
                            EditType.PRICE, EditType.READING_TIME -> ImeAction.Done

                            EditType.DESCRIPTION -> ImeAction.Default
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
