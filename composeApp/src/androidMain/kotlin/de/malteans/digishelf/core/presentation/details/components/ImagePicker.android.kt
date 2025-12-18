package de.malteans.digishelf.core.presentation.details.components

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

private const val TAG = "PhotoPicker"

@Composable
actual fun ImagePicker(
    onImageSelected: (imagePath: String?) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Registers a photo picker activity launcher in single-select mode.
    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            Log.d(TAG, "Selected URI: $uri")
            scope.launch(Dispatchers.IO) {
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val fileName = "img_${System.currentTimeMillis()}.jpg"
                    val file = File(context.filesDir, fileName)
                    val outputStream = FileOutputStream(file)

                    inputStream?.use { input ->
                        outputStream.use { output ->
                            input.copyTo(output)
                        }
                    }

                    withContext(Dispatchers.Main) {
                        onImageSelected(file.absolutePath)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error copying file", e)
                    withContext(Dispatchers.Main) {
                        onImageSelected(null)
                    }
                }
            }
        } else {
            Log.d(TAG, "No media selected")
            onImageSelected(null)
        }
    }

    LaunchedEffect(Unit) {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}