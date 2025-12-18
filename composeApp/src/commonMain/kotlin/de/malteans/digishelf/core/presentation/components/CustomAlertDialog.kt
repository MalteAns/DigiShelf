package de.malteans.digishelf.core.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import digishelf.composeapp.generated.resources.Res
import digishelf.composeapp.generated.resources.close
import org.jetbrains.compose.resources.stringResource

@Composable
fun CustomAlertDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    text: String,
    noConfirmButton: Boolean = false,
    properties: DialogProperties = DialogProperties(),
) {
    CustomDialog(
        onDismissRequest = onDismiss,
        leftIcons = {
            IconButton(onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(Res.string.close),
                )
            }
        },
        title = title,
        rightIcons = {
            if (!noConfirmButton) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier
                        .clickable { onConfirm() }
                )
            }
        },
        properties = properties,
    ) {
        Text(text)
    }
}