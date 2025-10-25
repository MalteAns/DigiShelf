package de.malteans.legal.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import digishelf.legal.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImprintScreen(
    navigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.imprint_title)) },
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
        Column(
            verticalArrangement = spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(Res.string.imprint_heading),
                style = MaterialTheme.typography.titleLarge,
            )

            Text("Malte Arians")
            Text("""
                Werner Hellweg 242,
                44894 Bochum,
                ${stringResource(Res.string.germany)}
            """.trimIndent())

            Spacer(Modifier.height(8.dp))

            Text("${stringResource(Res.string.imprint_email_label)}: leisure-activities@malteans.de")
//            Text("${stringResource(Res.string.impressum_phone_label)}: ")

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(Res.string.imprint_responsible_heading),
                style = MaterialTheme.typography.titleMedium,
            )
            Text("Malte Arians ${stringResource(Res.string.imprint_address_as_above)}")

            Spacer(Modifier.height(16.dp))

            Text(
                stringResource(Res.string.imprint_privacy_hint),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}