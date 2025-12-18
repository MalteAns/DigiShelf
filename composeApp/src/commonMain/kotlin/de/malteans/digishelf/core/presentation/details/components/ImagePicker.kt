package de.malteans.digishelf.core.presentation.details.components

import androidx.compose.runtime.Composable

@Composable
expect fun ImagePicker(
    onImageSelected: (imagePath: String?) -> Unit,
)