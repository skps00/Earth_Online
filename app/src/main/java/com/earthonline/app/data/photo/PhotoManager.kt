package com.earthonline.app.data.photo

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val photoDir: File
        get() {
            val dir = File(context.filesDir, "photos")
            if (!dir.exists()) dir.mkdirs()
            return dir
        }

    fun createPhotoUri(): Uri {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(photoDir, "EARTH_ONLINE_${timestamp}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    fun getPhotoFileFromUri(uri: Uri): File? {
        val filename = uri.lastPathSegment ?: return null
        val file = File(photoDir, filename)
        return if (file.exists()) file else null
    }
}
