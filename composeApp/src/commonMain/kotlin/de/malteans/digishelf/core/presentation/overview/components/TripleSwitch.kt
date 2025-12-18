package de.malteans.digishelf.core.presentation.overview.components

import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TripleSwitch(
    onSelectionChange: (Boolean?) -> Unit = {},
    curState: Boolean? = null,
) {
    var selectedOption by remember {
        mutableStateOf(curState)
    }
    fun onSelectionChangeIntern(newOption: Boolean?) {
        selectedOption = newOption
        onSelectionChange(newOption)
    }

    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .width(112.dp)
    ) {
        listOf(true, null, false).forEachIndexed { index, option ->

            // Define colors based on the specific option (preserves your original logic)
            val colorScheme = MaterialTheme.colorScheme
            val activeColor = when (option) {
                true -> colorScheme.primaryContainer
                null -> colorScheme.surfaceContainerHighest
                false -> colorScheme.errorContainer
            }
            val activeContentColor = when (option) {
                true -> colorScheme.onPrimaryContainer
                null -> colorScheme.onSurface
                false -> colorScheme.onErrorContainer
            }

            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = 3),
                onClick = { onSelectionChangeIntern(option) },
                selected = option == selectedOption,
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = activeColor,
                    activeContentColor = activeContentColor,
                    inactiveContainerColor = colorScheme.surfaceContainer, // Matches your background
                    inactiveContentColor = colorScheme.onSurface
                ),
                icon = {},
                label = {
                    Icon(
                        imageVector = when (option) {
                            true -> Icons.Default.Check
                            null -> Icons.Default.Circle
                            false -> Icons.Default.Close
                        },
                        contentDescription = null,
                    )
                }
            )
        }
    }
}
