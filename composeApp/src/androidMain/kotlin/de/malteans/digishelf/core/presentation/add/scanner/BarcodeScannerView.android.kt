package de.malteans.digishelf.core.presentation.add.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import de.malteans.digishelf.core.presentation.add.isIsbnFormat
import de.malteans.digishelf.core.presentation.components.CustomDialog
import digishelf.composeapp.generated.resources.Res
import digishelf.composeapp.generated.resources.camera_permission_desc
import digishelf.composeapp.generated.resources.camera_permission_heading
import org.jetbrains.compose.resources.stringResource
import java.util.concurrent.Executors

@OptIn(ExperimentalGetImage::class)
@Composable
actual fun BarcodeScannerView(
    onBack: () -> Unit,
    onBarcodeScanned: (String?) -> Unit
) {
    val context = LocalContext.current
    // Check initial permission state
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    // Launcher to request CAMERA permission
    val permissionLauncher = rememberLauncherForActivityResult(contract = RequestPermission()) { granted ->
        hasCameraPermission = granted
    }
    // If permission is not granted, immediately request it on composition
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    // If still not granted, show fallback UI dialog
    if (!hasCameraPermission) {
        CustomDialog(
            onDismissRequest = { onBack() },
            title = { Text(
                text = stringResource(Res.string.camera_permission_heading),
                textAlign = TextAlign.Center
            ) },
            rightIcons = @Composable {
                IconButton({ permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Grant Permission",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            )
        ) {
            Text(text = stringResource(Res.string.camera_permission_desc))
        }
        return
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    var camera: Camera? by remember { mutableStateOf(null) }
    var flashOn by remember { mutableStateOf(false) }
    val executor = remember { Executors.newSingleThreadExecutor() }
    var lastScannedBarcode by remember { mutableStateOf<String?>(null) }
    var scanner by remember { mutableStateOf<com.google.mlkit.vision.barcode.BarcodeScanner?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            scanner?.close()
            executor.shutdown()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    // Set up the camera preview use case.
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    // Set up image analysis with MLKit barcode scanning.
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    val options = BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                            Barcode.FORMAT_EAN_13,
                            Barcode.FORMAT_EAN_8,
                            Barcode.FORMAT_UPC_A,
                            Barcode.FORMAT_UPC_E,
                            Barcode.FORMAT_CODE_128,
                            Barcode.FORMAT_CODE_39
                        )
                        .build()

                    // Close the old scanner before creating a new one
                    scanner?.close()
                    scanner = BarcodeScanning.getClient(options)

                    imageAnalysis.setAnalyzer(executor) { imageProxy ->
                        val mediaImage = imageProxy.image
                        if (mediaImage != null) {
                            val image = InputImage.fromMediaImage(
                                mediaImage,
                                imageProxy.imageInfo.rotationDegrees
                            )
                            scanner?.process(image)
                                ?.addOnSuccessListener { barcodes ->
                                    if (barcodes.isNotEmpty()) {
                                        val barcode = barcodes[0]
                                        lastScannedBarcode = barcode.rawValue
                                        if (lastScannedBarcode?.isIsbnFormat() == true) {
                                            onBarcodeScanned(lastScannedBarcode)
                                        }
                                    }
                                }
                                ?.addOnFailureListener { e ->
                                    Log.e("BarcodeScanner", "Barcode scanning failed: ${e.message}")
                                }
                                ?.addOnCompleteListener {
                                    imageProxy.close()
                                }
                        } else {
                            imageProxy.close()
                        }
                    }

                    // Bind use cases to lifecycle.
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    try {
                        // Clean up old camera and scanner
                        cameraProvider.unbindAll()
                        camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )
                    } catch (exc: Exception) {
                        Log.e("BarcodeScanner", "Use case binding failed", exc)
                        scanner?.close()
                        scanner = null
                    }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            }
        )

        val outlineColor = MaterialTheme.colorScheme.primary

        // Draw an overlay with a semi-transparent dark layer and a clear cut-out in the center.
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(color = Color(0x99000000))
            val cutOutWidth = size.width * 0.8f
            val cutOutHeight = cutOutWidth * 0.75f
            val left = (size.width - cutOutWidth) / 2f
            val top = (size.height - cutOutHeight) / 2f
            drawRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = Size(cutOutWidth, cutOutHeight),
                blendMode = BlendMode.Clear,
            )
            drawRect(
                color = outlineColor,
                topLeft = Offset(left, top),
                size = Size(cutOutWidth, cutOutHeight),
                blendMode = BlendMode.Clear,
                style = Stroke(width = 1f.dp.toPx())
            )
        }

        // Back button
        IconButton(
            onClick = { onBack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        // Flash toggle button at the bottom center.
        IconButton(
            onClick = {
                flashOn = !flashOn
                camera?.cameraControl?.enableTorch(flashOn)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
                .scale(2f)
        ) {
            Icon(
                imageVector = if (flashOn) CustomFlashlightOnIcon else CustomFlashlightOffIcon,
                contentDescription = "Toggle Flash",
                tint = Color.White,
            )
        }
    }
}
