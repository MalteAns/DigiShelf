package de.malteans.digishelf.core.presentation.overview.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
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
            IconButton(onReset) {
                Icon(
                    imageVector = CustomFiltersOffIcon,
                    contentDescription = null,
                )
            }
        },
        rightIcons = {
            IconButton(
                onClick = onPositiveClick,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                )
            }
        }
    ) {
        Column(Modifier.padding(top = 8.dp)) {
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
                                    "•" -> null
                                    "-" -> false
                                    else -> null
                                }
                            )
                        },
                        curState = when (filterStates[filterItemsList.indexOf(item)]) {
                            true -> "+"
                            false -> "-"
                            else -> "•"
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