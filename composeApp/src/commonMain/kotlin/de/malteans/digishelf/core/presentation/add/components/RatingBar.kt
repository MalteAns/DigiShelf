package de.malteans.digishelf.core.presentation.add.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import digishelf.composeapp.generated.resources.Res
import digishelf.composeapp.generated.resources.rating
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RatingBar(
    max: Int = 5,
    current: Int,
    onRatingChanged: (Int) -> Unit,
    enabled: Boolean = true,
    activeColor: Color = MaterialTheme.colorScheme.tertiary,
    inactiveColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        for (i in 1..max) {
            Icon(
                imageVector = if (i <= current) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "${stringResource(Res.string.rating)}: $i",
                modifier = Modifier
                    .combinedClickable(
                        enabled = enabled,
                        onClick = { onRatingChanged(i) },
                        onLongClick = { onRatingChanged(0) },
                    )
                    .padding(horizontal = 4.dp)
                    .weight(1f)
                    .aspectRatio(1f),
                tint = if (i <= current) activeColor else inactiveColor,
            )
        }
    }
}