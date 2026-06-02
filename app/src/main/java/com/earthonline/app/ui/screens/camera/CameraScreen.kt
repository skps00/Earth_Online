package com.earthonline.app.ui.screens.camera

// 相機畫面，用於拍攝成就證據照片，支援閃光燈、準星與相簿選取

import android.util.Log
import android.graphics.BitmapFactory
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
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.earthonline.app.data.photo.PhotoManager
import com.earthonline.app.ui.theme.AccentOrange
import com.earthonline.app.ui.theme.EmeraldGreen
import com.earthonline.app.ui.theme.Gold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private const val TAG = "CameraScreen"

// 渲染相機取景畫面，包含準星動畫、閃光燈控制、證據縮圖列與快門按鈕
@Composable
fun CameraScreen(
    achievementId: String,
    existingEvidencePhotos: List<String>,
    photoManager: PhotoManager,
    onPhotoTaken: (achievementId: String, compressedUri: String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var camera by remember { mutableStateOf<androidx.camera.core.Camera?>(null) }
    var cameraError by remember { mutableStateOf<String?>(null) }
    val isCapturing = remember { mutableStateOf(false) }
    val flashEnabled = remember { mutableStateOf(false) }
    val showReticle = remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    val reticlePulse by rememberInfiniteTransition().animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse)
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                val compressedUri = photoManager.compressPhoto(uri)
                withContext(Dispatchers.Main) {
                    onPhotoTaken(achievementId, compressedUri.toString())
                }
            }
        }
    }

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).also { previewView ->
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    try {
                        val provider = cameraProviderFuture.get()
                        cameraProvider = provider
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val capture = ImageCapture.Builder()
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                            .setJpegQuality(95)
                            .build()
                        imageCapture = capture

                        provider.unbindAll()
                        camera = provider.bindToLifecycle(
                            lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, capture
                        )
                    } catch (e: Exception) {
                        cameraError = e.message
                    }
                }, ContextCompat.getMainExecutor(ctx))
            }
        },
        modifier = Modifier.fillMaxSize()
    )

    DisposableEffect(Unit) {
        onDispose {
            camera?.cameraControl?.enableTorch(false)
            imageCapture = null
            camera = null
            cameraProvider?.unbindAll()
            cameraProvider = null
        }
    }

    // Vignette
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.35f), Color.Black.copy(alpha = 0.8f)),
                    radius = 1200f
                )
            )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(top = 40.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Close, null, tint = Gold, modifier = Modifier.size(20.dp))
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
                    .border(1.dp, Gold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(EmeraldGreen)
                        .alpha(reticlePulse)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("SCANNING ENV", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                    .clickable { flashEnabled.value = !flashEnabled.value; camera?.cameraControl?.enableTorch(flashEnabled.value) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.FlashAuto, null,
                    tint = if (flashEnabled.value) Gold else Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Reticle
        if (showReticle.value) {
            Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .scale(reticlePulse)
                    .border(1.dp, Gold.copy(alpha = 0.15f))
            ) {
                val bracketLen = 24.dp
                val bracketW = 2.dp
                Box(Modifier.align(Alignment.TopStart).size(bracketLen, bracketW).background(Gold))
                Box(Modifier.align(Alignment.TopStart).size(bracketW, bracketLen).background(Gold))
                Box(Modifier.align(Alignment.TopEnd).size(bracketLen, bracketW).background(Gold))
                Box(Modifier.align(Alignment.TopEnd).size(bracketW, bracketLen).background(Gold))
                Box(Modifier.align(Alignment.BottomStart).size(bracketLen, bracketW).background(Gold))
                Box(Modifier.align(Alignment.BottomStart).size(bracketW, bracketLen).background(Gold))
                Box(Modifier.align(Alignment.BottomEnd).size(bracketLen, bracketW).background(Gold))
                Box(Modifier.align(Alignment.BottomEnd).size(bracketW, bracketLen).background(Gold))
                Box(
                    modifier = Modifier
                        .size(3.dp)
                        .clip(CircleShape)
                        .background(Gold.copy(alpha = 0.5f))
                        .align(Alignment.Center)
                )
            }
        }
        }

        // Gallery Strip
        if (existingEvidencePhotos.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 160.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 20.dp)
            ) {
                items(existingEvidencePhotos.take(5)) { photoPath ->
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.5.dp, Gold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    ) {
                        EvidenceThumbnail(photoPath)
                    }
                }
                item {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.MoreHoriz, null, tint = Color.White.copy(alpha = 0.5f))
                    }
                }
            }
        }

        // Bottom Controls
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 48.dp, start = 32.dp, end = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gallery — pick existing photo
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                    .clickable { galleryLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.PhotoLibrary, null, tint = Gold, modifier = Modifier.size(20.dp))
            }

            // Shutter
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clickable {
                        if (!isCapturing.value) {
                            val capture = imageCapture ?: return@clickable
                            isCapturing.value = true
                            val dir = java.io.File(context.filesDir, "photos")
                            if (!dir.exists()) dir.mkdirs()
                            val photoFile = java.io.File(dir, "CAPTURE_${System.currentTimeMillis()}.jpg")
                            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                            capture.takePicture(
                                outputOptions,
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                        isCapturing.value = false
                                        scope.launch(Dispatchers.IO) {
                                            val compressedUri = photoManager.compressPhoto(
                                                android.net.Uri.fromFile(photoFile)
                                            )
                                            withContext(Dispatchers.Main) {
                                                onPhotoTaken(achievementId, compressedUri.toString())
                                            }
                                        }
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        isCapturing.value = false
                                        cameraError = exception.message
                                    }
                                }
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .border(4.dp, Gold, CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                )
            }

            // Grid toggle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                    .clickable { showReticle.value = !showReticle.value },
                contentAlignment = Alignment.Center
            ) {
                GridIcon(active = showReticle.value)
            }
        }
    }

    // Error overlay
    if (cameraError != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Camera Error", color = AccentOrange, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(cameraError ?: "", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Tap to close", color = Gold, fontSize = 14.sp)
            }
        }
    }
}

// 渲染證據縮圖，從檔案路徑載入並顯示照片
@Composable
private fun EvidenceThumbnail(photoPath: String) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    LaunchedEffect(photoPath) {
        withContext(Dispatchers.IO) {
            try {
                val uri = Uri.parse(photoPath)
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val oldBitmap = bitmap
                    bitmap = BitmapFactory.decodeStream(stream)
                    oldBitmap?.recycle()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load evidence thumbnail", e)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { bitmap?.recycle() }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

// 渲染網格切換圖示，以四個圓角矩形表示準星顯示狀態
@Composable
private fun GridIcon(active: Boolean = true) {
    val c = if (active) Gold.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.3f)
    Canvas(modifier = Modifier.size(16.dp)) {
        val w = size.width
        val h = size.height
        val s = w / 2.5f
        val g = 2f
        drawRoundRect(c, Offset(0f, 0f), Size(s, s), CornerRadius(g))
        drawRoundRect(c, Offset(w - s - g, 0f), Size(s, s), CornerRadius(g))
        drawRoundRect(c, Offset(0f, h - s - g), Size(s, s), CornerRadius(g))
        drawRoundRect(c, Offset(w - s - g, h - s - g), Size(s, s), CornerRadius(g))
    }
}
