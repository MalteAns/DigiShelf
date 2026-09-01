package de.malteans.digishelf.core.presentation.add.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LevelBar(
    max: Int = 5,
    current: Int,
    onLevelChanged: (Int) -> Unit,
    enabled: Boolean = true,
    activeColor: Color = MaterialTheme.colorScheme.tertiary,
    inactiveColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        for (i in 1..max) {
            Icon(
                imageVector = if (i <= current) activeIcon else inactiveIcon,
                contentDescription = "$contentDescription: $i",
                modifier = Modifier
                    .combinedClickable(
                        enabled = enabled,
                        onClick = { onLevelChanged(i) },
                        onLongClick = { onLevelChanged(0) },
                    )
                    .weight(1f)
                    .aspectRatio(1f),
                tint = if (i <= current) activeColor else inactiveColor,
            )
        }
    }
}