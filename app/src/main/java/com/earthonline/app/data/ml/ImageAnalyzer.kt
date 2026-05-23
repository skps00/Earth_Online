package com.earthonline.app.data.ml

import android.content.Context
import android.net.Uri
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageAnalyzer @Inject constructor(@ApplicationContext private val context: Context) {
    private val labeler = ImageLabeling.getClient(
        ImageLabelerOptions.Builder()
            .setConfidenceThreshold(ML_CONFIDENCE)
            .build()
    )

    suspend fun analyze(uri: Uri): List<String> = withContext(Dispatchers.IO) {
        try {
            val image = InputImage.fromFilePath(context, uri)
            val labels = Tasks.await(labeler.process(image))
            labels.take(ML_MAX_LABELS).map { "${it.text} (${(it.confidence * 100).toInt()}%)" }
        } catch (_: Exception) {
            emptyList()
        }
    }
}

private const val ML_CONFIDENCE = 0.6f
private const val ML_MAX_LABELS = 5
