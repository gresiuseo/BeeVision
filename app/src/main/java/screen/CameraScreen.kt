package com.beevision.app.screen

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat

@Composable
fun CameraScreen(
    frameType: String,
    onBack: () -> Unit,
    onPhotoCaptured: (Uri) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var imageCapture by remember {
        mutableStateOf<ImageCapture?>(null)
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            hasPermission = granted
        }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val orientationHint =
        when (frameType) {
            "Українська" -> "Тримай телефон вертикально"
            "Дадан 300", "Магазин 145", "Рута" -> "Тримай телефон горизонтально"
            else -> "Тримай телефон так, щоб рамка повністю вмістилась у кадр"
        }

    if (!hasPermission) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Потрібен дозвіл на камеру",
                    color = Color.White
                )

                Button(
                    onClick = {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                ) {
                    Text("Дати дозвіл")
                }

                Button(
                    onClick = onBack
                ) {
                    Text("Назад")
                }
            }
        }

        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx)

                val cameraProviderFuture =
                    ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener(
                    {
                        val cameraProvider =
                            cameraProviderFuture.get()

                        val preview =
                            Preview.Builder()
                                .build()
                                .also { previewUseCase ->
                                    previewUseCase.setSurfaceProvider(
                                        previewView.surfaceProvider
                                    )
                                }

                        val capture =
                            ImageCapture.Builder()
                                .setCaptureMode(
                                    ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
                                )
                                .build()

                        imageCapture = capture

                        try {
                            cameraProvider.unbindAll()

                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                capture
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    ContextCompat.getMainExecutor(ctx)
                )

                previewView
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Тип рамки: $frameType",
                    color = Color.White,
                    modifier = Modifier
                        .background(
                            color = Color.Black.copy(alpha = 0.55f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )

                Text(
                    text = orientationHint,
                    color = Color.White,
                    modifier = Modifier
                        .background(
                            color = Color.Black.copy(alpha = 0.55f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        takePhoto(
                            context = context,
                            imageCapture = imageCapture,
                            onPhotoCaptured = onPhotoCaptured
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Зробити фото")
                }

                Button(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Назад")
                }
            }
        }
    }
}

private fun takePhoto(
    context: android.content.Context,
    imageCapture: ImageCapture?,
    onPhotoCaptured: (Uri) -> Unit
) {
    val capture = imageCapture ?: return

    val name =
        "beevision_${System.currentTimeMillis()}"

    val contentValues =
        ContentValues().apply {
            put(
                MediaStore.MediaColumns.DISPLAY_NAME,
                name
            )

            put(
                MediaStore.MediaColumns.MIME_TYPE,
                "image/jpeg"
            )

            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                "Pictures/BeeVision"
            )
        }

    val outputOptions =
        ImageCapture.OutputFileOptions.Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

    capture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {

            override fun onImageSaved(
                outputFileResults: ImageCapture.OutputFileResults
            ) {
                val savedUri =
                    outputFileResults.savedUri

                if (savedUri != null) {
                    onPhotoCaptured(savedUri)
                }
            }

            override fun onError(
                exception: ImageCaptureException
            ) {
                exception.printStackTrace()
            }
        }
    )
}