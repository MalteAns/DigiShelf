package de.malteans.digishelf.export.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import de.malteans.digishelf.core.presentation.components.CustomDialog
import de.malteans.digishelf.core.presentation.components.OutlinedText
import de.malteans.digishelf.core.presentation.settings.components.icons.CustomFileOpenIcon
import de.malteans.digishelf.export.presentation.ImportFileType.CSV
import de.malteans.digishelf.export.presentation.ImportFileType.JSON
import digishelf.composeapp.generated.resources.Res
import digishelf.composeapp.generated.resources.file
import digishelf.composeapp.generated.resources.import_data
import digishelf.composeapp.generated.resources.no_file_selected
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

enum class ImportFileType {
    CSV,
    JSON,
}

private fun String.getFileType(): ImportFileType? {
    return when {
        this.endsWith(".csv", ignoreCase = true) -> CSV
        this.endsWith(".json", ignoreCase = true) -> JSON
        else -> null
    }
}

@Composable
fun ImportDialog(
    onDismiss: () -> Unit,
    onFinish: (fileType: ImportFileType, fileContent: String) -> Unit,
) {
    val scope = rememberCoroutineScope()

    // Local state to hold selected file info
    var fileName by remember { mutableStateOf("") }
    var fileContent by remember { mutableStateOf<String?>(null) }

    // Flag to show that file picking is in progress or finished
    val filePicker = rememberFilePickerLauncher(
        type = FileKitType.File(listOf("csv", "CSV", "json")),
    ) {
        it?.let { file ->
            scope.launch(Dispatchers.IO) {
                fileName = file.name
                fileContent = file.readString()
            }
        }
    }

    // A simple dialog UI – you can style as needed.
    CustomDialog(
        onDismissRequest = onDismiss,
        title = stringResource(Res.string.import_data),
        rightIcons = {
            IconButton(
                onClick = {
                    onFinish(
                        fileName.getFileType() ?: return@IconButton,
                        fileContent!!
                    )
                },
                enabled = fileContent != null,
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Submit",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    filePicker.launch()
                },
        ) {
            OutlinedText(
                text = { Text (
                    text = fileName.ifEmpty { stringResource(Res.string.no_file_selected) },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = if (fileName.isEmpty()) 0.5f else 1f
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.StartEllipsis,
                )},
                label = { Text(stringResource(Res.string.file)) },
                trailingIcon = {
                    Icon(
                        imageVector = CustomFileOpenIcon,
                        contentDescription = "Open file",
                    )
                }
            )
        }
    }
}
