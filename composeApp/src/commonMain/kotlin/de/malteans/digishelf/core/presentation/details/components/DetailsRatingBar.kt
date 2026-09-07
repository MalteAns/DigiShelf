package de.malteans.digishelf.core.presentation.details.components

import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import de.malteans.digishelf.core.presentation.add.components.LevelBar

@Composable
fun DetailsRatingBar(
    current: Int,
    onLevelChanged: (Int) -> Unit,
    enabled: Boolean,
    activeColor: Color,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    label: String
) {
    LevelBar(
        current = current,
        onLevelChanged = onLevelChanged,
        enabled = enabled,
        activeColor = activeColor,
        inactiveColor = MaterialTheme.colorScheme.outlineVariant,
        activeIcon = activeIcon,
        inactiveIcon = inactiveIcon,
        contentDescription = label,
        modifier = Modifier.width(150.dp)
    )
}