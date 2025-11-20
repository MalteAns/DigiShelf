package de.malteans.digishelf.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import digishelf.composeapp.generated.resources.Res
import digishelf.composeapp.generated.resources.close
import org.jetbrains.compose.resources.stringResource

@Composable
fun CustomDialog(
    onDismissRequest: () -> Unit,
    title: String,
    leftIcons: @Composable RowScope.() -> Unit = {
        IconButton(onDismissRequest) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(Res.string.close),
            )
        }
    },
    rightIcons: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
    contentPadding: PaddingValues = PaddingValues(
        start = 16.dp,
        top = 4.dp,
        end = 16.dp,
        bottom = 16.dp,
    ),
    content: @Composable ColumnScope.() -> Unit,
) {
    CustomDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = title) },
        leftIcons = leftIcons,
        rightIcons = rightIcons,
        modifier = modifier,
        properties = properties,
        contentPadding = contentPadding,
        content = content,
    )
}

@Composable
fun CustomDialog(
    onDismissRequest: () -> Unit,
    title: @Composable RowScope.() -> Unit,
    leftIcons: @Composable RowScope.() -> Unit = {
        IconButton(onDismissRequest) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(Res.string.close),
            )
        }
    },
    rightIcons: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
    contentPadding: PaddingValues = PaddingValues(
        start = 16.dp,
        top = 4.dp,
        end = 16.dp,
        bottom = 16.dp,
    ),
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        Box (
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row (
                        modifier = Modifier
                            .weight(1f),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        leftIcons()
                    }
                    Row (
                        modifier = Modifier
                            .wrapContentWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProvideTextStyle(MaterialTheme.typography.titleLarge) {
                            title()
                        }
                    }
                    Row (
                        modifier = Modifier
                            .weight(1f),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        rightIcons()
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 4.dp))
                Column(
                    modifier = Modifier
                        .padding(contentPadding)
                ) {
                    content()
                }
            }
        }
    }
}