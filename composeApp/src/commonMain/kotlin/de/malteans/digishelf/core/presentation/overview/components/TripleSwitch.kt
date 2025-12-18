package de.malteans.digishelf.core.presentation.overview.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TripleSwitch(
    onSelectionChange: (String) -> Unit = {},
    states: List<String> = listOf("+", "•", "-"),
    curState: String = "•"
) {
    var selectedOption by remember {
        mutableStateOf(curState)
    }
    val onSelectionChangeIntern = { text: String ->
        selectedOption = text
        onSelectionChange(text)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .wrapContentSize()
    ) {
        Row(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            states.forEach { text ->
                Text(
                    text = text,
                    color = if (text == selectedOption) {
                        when (text.trim()) {
                            "+" -> MaterialTheme.colorScheme.onPrimaryContainer
                            "-" -> MaterialTheme.colorScheme.onErrorContainer
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    fontSize = 16.sp,
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(12.dp))
                        .clickable { onSelectionChangeIntern(text) }
                        .background(
                            if (text == selectedOption) {
                                when (text.trim()) {
                                    "+" -> MaterialTheme.colorScheme.primaryContainer
                                    "-" -> MaterialTheme.colorScheme.errorContainer
                                    else -> MaterialTheme.colorScheme.surfaceContainerHighest
                                }
                            } else {
                                MaterialTheme.colorScheme.surfaceContainer
                            }
                        )
                        .aspectRatio(1f)
                )
            }
        }
    }
}