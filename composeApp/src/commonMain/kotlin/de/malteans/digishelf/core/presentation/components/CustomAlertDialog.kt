package de.malteans.digishelf.core.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import digishelf.composeapp.generated.resources.Res
import digishelf.composeapp.generated.resources.close
import org.jetbrains.compose.resources.stringResource

@Composable
fun CustomAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    onDismiss: (() -> Unit)? = null,
    noConfirmButton: Boolean = false
) {
    CustomDialog(
        onDismissRequest = onDismissRequest,
        leftIcons = {
            IconButton({ if (onDismiss != null) onDismiss() else onDismissRequest() }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(Res.string.close),
                )
            }
        },
        title = { title() },
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
    ) {
        text()
    }
}