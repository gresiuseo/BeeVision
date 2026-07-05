package com.beevision.app.screen

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.view.Surface
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors

@Composable
fun CameraScreen(
    frameType: String,
    onBack: () -> Unit,
    onPhotoCaptured: (Uri?) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val lifecycleOwner = LocalLifecycleOwner.current

    val isUkrainianFrame = frameType == "Українська"

    DisposableEffect(frameType) {
        activity?.requestedOrientation =
            if (isUkrainianFrame) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }

        onDispose {
            activity?.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            hasPermission = it
        }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (!hasPermission) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text("Потрібен доступ до камери")

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            ) {
                Text("Дозволити")
            }
        }
        return
    }

    val imageCapture = remember(frameType) {
        ImageCapture.Builder()
            .setTargetRotation(
                if (isUkrainianFrame) Surface.ROTATION_0
                else Surface.ROTATION_90
            )
            .build()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = if (isUkrainianFrame)
                "🇺🇦 Українська рамка: тримай телефон вертикально"
            else
                "📱 $frameType: тримай телефон горизонтально",
            modifier = Modifier.padding(12.dp)
        )

        AndroidView(
            modifier = Modifier.weight(1f),
            factory = { ctx ->

                val previewView = PreviewView(ctx)
                val cameraProviderFuture =
                    ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({

                    val cameraProvider =
                        cameraProviderFuture.get()

                    val preview =
                        androidx.camera.core.Preview.Builder()
                            .setTargetRotation(
                                if (isUkrainianFrame) Surface.ROTATION_0
                                else Surface.ROTATION_90
                            )
                            .build()

                    preview.surfaceProvider =
                        previewView.surfaceProvider

                    cameraProvider.unbindAll()

                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageCapture
                    )

                }, ContextCompat.getMainExecutor(ctx))

                previewView
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onBack) {
                Text("Назад")
            }

            Button(
                onClick = {
                    val name =
                        "BeeVision_${System.currentTimeMillis()}"

                    val values = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    }

                    val output =
                        ImageCapture.OutputFileOptions
                            .Builder(
                                context.contentResolver,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                values
                            )
                            .build()

                    imageCapture.takePicture(
                        output,
                        Executors.newSingleThreadExecutor(),
                        object : ImageCapture.OnImageSavedCallback {

                            override fun onImageSaved(
                                result: ImageCapture.OutputFileResults
                            ) {
                                onPhotoCaptured(result.savedUri)
                            }

                            override fun onError(
                                exception: ImageCaptureException
                            ) {
                            }
                        }
                    )
                }
            ) {
                Text("📷 Фото")
            }
        }
    }
}