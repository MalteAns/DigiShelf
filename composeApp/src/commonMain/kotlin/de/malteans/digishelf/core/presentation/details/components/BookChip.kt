package de.malteans.digishelf.core.presentation.details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class ChipSize {
    REGULAR, LARGE
}

@Composable
fun BookChip(
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    modifier: Modifier = Modifier,
    size: ChipSize = ChipSize.REGULAR,
    chipContent: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = modifier
            .heightIn(
                min = 48.dp
            )
            .widthIn(
                min = when (size) {
                    ChipSize.REGULAR -> 72.dp
                    ChipSize.LARGE -> 128.dp
                }
            )
            .clip(MaterialTheme.shapes.large)
            .background(containerColor)
            .padding(
                vertical = 8.dp,
                horizontal = 12.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CompositionLocalProvider(
                LocalContentColor provides contentColor
            ) {
                chipContent()
            }
        }
    }
}