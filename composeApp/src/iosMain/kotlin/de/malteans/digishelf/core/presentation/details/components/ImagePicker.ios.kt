package de.malteans.digishelf.core.presentation.details.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.*
import platform.PhotosUI.*
import platform.UIKit.*
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun ImagePicker(onImageSelected: (imagePath: String?) -> Unit) {
    val delegate = remember {
        object : NSObject(), PHPickerViewControllerDelegateProtocol {
            override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
                picker.dismissViewControllerAnimated(true, null)

                val result = didFinishPicking.firstOrNull() as? PHPickerResult
                if (result == null) {
                    onImageSelected(null)
                    return
                }

                result.itemProvider.loadDataRepresentationForTypeIdentifier(
                    typeIdentifier = "public.image"
                ) { data, error ->
                    if (data != null && error == null) {
                        val image = UIImage(data = data)
                        val jpegData = UIImageJPEGRepresentation(image, 0.9)

                        if (jpegData != null) {
                            val documentsPath = NSSearchPathForDirectoriesInDomains(
                                NSDocumentDirectory,
                                NSUserDomainMask,
                                true
                            ).firstOrNull() as? String

                            if (documentsPath != null) {
                                val timestamp = NSDate().timeIntervalSince1970.toLong()
                                val fileName = "img_$timestamp.jpg"
                                val filePath = "$documentsPath/$fileName"

                                val success = NSFileManager.defaultManager.createFileAtPath(
                                    filePath,
                                    jpegData,
                                    null
                                )

                                if (success) {
                                    onImageSelected(filePath)
                                } else {
                                    onImageSelected(null)
                                }
                            } else {
                                onImageSelected(null)
                            }
                        } else {
                            onImageSelected(null)
                        }
                    } else {
                        onImageSelected(null)
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val configuration = PHPickerConfiguration()
        configuration.filter = PHPickerFilter.imagesFilter
        configuration.selectionLimit = 1

        val pickerController = PHPickerViewController(configuration = configuration)
        pickerController.delegate = delegate

        val windowScene = UIApplication.sharedApplication.connectedScenes
            .filterIsInstance<UIWindowScene>()
            .firstOrNull()
        val rootViewController = windowScene?.windows
            ?.filterIsInstance<UIWindow>()
            ?.firstOrNull { it.isKeyWindow() }
            ?.rootViewController
        rootViewController?.presentViewController(pickerController, animated = true, completion = null)
    }
}