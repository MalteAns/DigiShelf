package de.malteans.digishelf.core.presentation.details.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import de.malteans.digishelf.core.presentation.add.components.LevelBar

@Composable
fun DetailsMoodLevelRow(
    label: String,
    value: Int,
    icon: ImageVector,
    color: Color,
    onValueChange: (Int) -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, modifier = Modifier.size(20.dp), tint = color)
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            modifier = Modifier.weight(1f)
        )
        LevelBar(
            current = value,
            onLevelChanged = onValueChange,
            enabled = enabled,
            activeColor = color,
            inactiveColor = MaterialTheme.colorScheme.outlineVariant,
            activeIcon = icon,
            inactiveIcon = icon,
            contentDescription = label,
            modifier = Modifier.width(130.dp)
        )
    }
}