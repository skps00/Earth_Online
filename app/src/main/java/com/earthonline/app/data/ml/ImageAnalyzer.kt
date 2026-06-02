package com.earthonline.app.data.ml

// 影像分析器 — 使用 ML Kit ImageLabeling 分析圖片並回傳標籤列表

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

// 影像分析器 — 初始化 ML Kit 標籤器，信心度門檻 0.6
@Singleton
class ImageAnalyzer @Inject constructor(@ApplicationContext private val context: Context) {
    private val labeler = ImageLabeling.getClient(
        ImageLabelerOptions.Builder()
            .setConfidenceThreshold(ML_CONFIDENCE)
            .build()
    )

    // 分析圖片 URI — 回傳最多 5 個標籤，格式為「標籤名 (信心度%)」
    suspend fun analyze(uri: Uri): List<String> = withContext(Dispatchers.IO) {
        try {
            val image = InputImage.fromFilePath(context, uri)
            val labels = Tasks.await(labeler.process(image))
            labels.take(ML_MAX_LABELS).map { "${it.text} (${(it.confidence * 100).toInt()}%)" }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to analyze image", e)
            emptyList()
        }
    }

    fun close() { labeler.close() }
}

private const val ML_CONFIDENCE = 0.6f
private const val ML_MAX_LABELS = 5
private const val TAG = "ImageAnalyzer"
