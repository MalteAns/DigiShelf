package de.malteans.digishelf.core.presentation.details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun DetailsInfoChip(
    label: String,
    icon: ImageVector? = null,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.Companion,
    content: @Composable (() -> Unit)? = null
) {
    AssistChip(
        onClick = onClick,
        label = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (content != null) {
                    content()
                } else {
                    icon?.let {
                        Icon(it, null, Modifier.size(18.dp), tint = color)
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(text = label, color = color)
                }
            }
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = modifier
    )
}