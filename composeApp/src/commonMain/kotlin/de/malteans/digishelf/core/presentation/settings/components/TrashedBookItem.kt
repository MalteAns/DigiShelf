package de.malteans.digishelf.core.presentation.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.malteans.digishelf.core.domain.Book
import de.malteans.digishelf.core.presentation.settings.components.icons.CustomDeleteForeverIcon
import de.malteans.digishelf.core.presentation.settings.components.icons.CustomRestoreFromTrashIcon
import digishelf.composeapp.generated.resources.Res
import digishelf.composeapp.generated.resources.delete
import digishelf.composeapp.generated.resources.deleted
import digishelf.composeapp.generated.resources.restore
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Composable
fun TrashedBookItem(book: Book, onClick: () -> Unit, onRestore: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(12.dp, 0.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(1f)
        ) {
            Text(
                text = book.title,
                modifier = Modifier.padding(end = 8.dp),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = book.author,
                modifier = Modifier.padding(end = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            )
            Text(
                text = "${stringResource(Res.string.deleted)}: ${book.getDeletedSinceString()}",
                modifier = Modifier
                    .padding(end = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            )
        }
        Column(
            horizontalAlignment = Alignment.Companion.End,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Icon(
                imageVector = CustomRestoreFromTrashIcon,
                contentDescription = stringResource(Res.string.restore),
                modifier = Modifier.Companion
                    .clickable { onRestore() }
                    .size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Icon(
                imageVector = CustomDeleteForeverIcon,
                contentDescription = stringResource(Res.string.delete),
                modifier = Modifier.Companion
                    .clickable { onDelete() }
                    .size(32.dp),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
fun Book.getDeletedSinceString() : String {
    if (this.deletedSince == 0L) return ""
    val localDateTime = Instant.fromEpochMilliseconds(this.deletedSince).toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.hour}:${localDateTime.minute} " +
            "${if (localDateTime.day < 10) "0" else ""}${localDateTime.day}." +
            "${if (localDateTime.month.number < 10) "0" else ""}${localDateTime.month.number}." +
            "${localDateTime.year}"
}