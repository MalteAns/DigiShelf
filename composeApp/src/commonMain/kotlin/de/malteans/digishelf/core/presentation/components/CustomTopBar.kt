package de.malteans.digishelf.core.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import digishelf.composeapp.generated.resources.Res
import digishelf.composeapp.generated.resources.app_icon
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    title: @Composable () -> Unit,
    navigationAction: () -> Unit = {},
    navigationIcon: @Composable () -> Unit = {
        IconButton(navigationAction) {
            Image(
                painter = painterResource(Res.drawable.app_icon),
                contentDescription = "Menu",
                modifier = Modifier.size(48.dp)
            )
        }
    },
    actions: @Composable RowScope.() -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    shape: Shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 24.dp, bottomEnd = 24.dp),
    padding: PaddingValues = PaddingValues(bottom = 8.dp),
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = title,
        modifier = modifier
            .padding(padding)
            .clip(shape = shape),
        navigationIcon = navigationIcon,
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            titleContentColor = contentColor,
        ),
    )
}