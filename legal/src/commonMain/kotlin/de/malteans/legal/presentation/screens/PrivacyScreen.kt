package de.malteans.legal.presentation.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.malteans.legal.presentation.components.WebViewContent
import de.malteans.legal.resources.Res
import de.malteans.legal.resources.navigate_back
import de.malteans.legal.resources.privacy_title
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyScreen(
    htmlData: String?,
    navigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.privacy_title)) },
                navigationIcon = {
                    IconButton(navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.navigate_back),
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        WebViewContent(
            htmlData = htmlData,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}