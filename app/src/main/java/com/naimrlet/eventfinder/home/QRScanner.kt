package com.naimrlet.eventfinder.home

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

@Composable
fun QRScanner(onQRCodeScanned: (String) -> Unit) {
    val context = LocalContext.current

    // Remember the camera provider and preview view state
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }

    // Camera permission state (you should handle permissions properly in production)
    var hasCameraPermission by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        hasCameraPermission = context.checkSelfPermission(Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    if (hasCameraPermission) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize(),
            update = {
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    bindPreviewAndAnalyzer(
                        context,
                        cameraProvider,
                        previewView,
                        onQRCodeScanned
                    )
                }, ContextCompat.getMainExecutor(context))
            }
        )
    } else {
        // Handle cases where camera permission is not granted (e.g., show a message)
        Log.e("QRScanner", "Camera permission not granted")
    }
}

private fun bindPreviewAndAnalyzer(
    context: Context,
    cameraProvider: ProcessCameraProvider,
    previewView: PreviewView,
    onQRCodeScanned: (String) -> Unit
) {
    val preview = Preview.Builder().build().apply {
        setSurfaceProvider(previewView.surfaceProvider)
    }

    val barcodeScanner = BarcodeScanning.getClient()

    val imageAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .also { analysis ->
            analysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                processImageProxy(barcodeScanner, imageProxy, onQRCodeScanned)
            }
        }

    try {
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            context as androidx.lifecycle.LifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            imageAnalysis
        )
    } catch (exc: Exception) {
        Log.e("QRScanner", "Use case binding failed", exc)
    }
}

@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    barcodeScanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    imageProxy: ImageProxy,
    onQRCodeScanned: (String) -> Unit
) {
    val mediaImage = imageProxy.image ?: return

    val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

    barcodeScanner.process(inputImage)
        .addOnSuccessListener { barcodes ->
            // Define a label for the loop
            for (barcode in barcodes) {
                barcode.rawValue?.let { qrCode ->
                    onQRCodeScanned(qrCode)
                    return@addOnSuccessListener // Exit the lambda after processing one QR code
                }
            }
        }
        .addOnFailureListener { e ->
            Log.e("QRScanner", "Barcode scanning failed", e)
        }
        .addOnCompleteListener {
            imageProxy.close() // Close the image proxy to free up resources
        }

}
