package de.malteans.digishelf.core.presentation.details.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.malteans.digishelf.core.domain.Trope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Composable
fun TropeChip(
    trope: Trope,
    isEditing: Boolean,
    onRemove: (Trope) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    var showDeleteIcon by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        AssistChip(
            label = { Text(trope.name) },
            onClick = {
                if (isEditing) {
                    if (!showDeleteIcon) {
                        showDeleteIcon = true
                        scope.launch {
                            delay(3.seconds)
                            showDeleteIcon = false
                        }
                    } else {
                        onRemove(trope)
                    }
                }
            },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        )

        if (showDeleteIcon && isEditing) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove trope",
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
            )
        }
    }
}