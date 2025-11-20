package de.malteans.digishelf.core.presentation.overview.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.malteans.digishelf.core.presentation.components.CustomDialog
import digishelf.composeapp.generated.resources.Res
import digishelf.composeapp.generated.resources.filter_options
import org.jetbrains.compose.resources.stringResource

@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    onPositiveClick: () -> Unit,
    onReset: () -> Unit,
    onFilterChange: (Int, Boolean?) -> Unit,
    onTypeChange: (Int, Int) -> Unit,
    filterItemsList: List<String>,
    typeItemsList: Map<String, List<String>>,
    filterStates: List<Boolean?>,
    typeStates: List<String>
) {
    CustomDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = { Text(stringResource(Res.string.filter_options)) },
        leftIcons = {
            Icon(
                imageVector = CustomFiltersOffIcon,
                contentDescription = null,
                modifier = Modifier.clickable {
                    onReset()
                }
            )
        },
        rightIcons = {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    onPositiveClick()
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(top = 8.dp)
        ) {
            filterItemsList.forEach { item ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item,
                        fontSize = 16.sp
                    )
                    TripleSwitch(
                        onSelectionChange = { text ->
                            onFilterChange(
                                filterItemsList.indexOf(item), when (text.trim()) {
                                    "+" -> true
                                    "o" -> null
                                    "-" -> false
                                    else -> null
                                }
                            )
                        },
                        curState = when (filterStates[filterItemsList.indexOf(item)]) {
                            true -> " + "
                            false -> " - "
                            else -> " o "
                        }
                    )
                }
                Spacer(modifier = Modifier.padding(4.dp))
            }
            typeItemsList.forEach { (label, options) ->
                var selectedOption by remember {
                    mutableStateOf(typeStates[typeItemsList.keys.indexOf(label)])
                }

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DynamicSelectTextField(
                        selectedOption = selectedOption,
                        options = options,
                        label = label,
                        onValueChanged = { text ->
                            selectedOption = text
                            onTypeChange(typeItemsList.keys.indexOf(label), options.indexOf(text))
                        }
                    )
                }
                Spacer(modifier = Modifier.padding(4.dp))
            }
        }
    }
}