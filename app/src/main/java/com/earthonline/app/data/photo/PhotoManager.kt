package com.earthonline.app.data.photo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.earthonline.app.AppConstants
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
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
            val dir = File(context.filesDir, AppConstants.PHOTOS_DIR)
            if (!dir.exists()) dir.mkdirs()
            return dir
        }

    fun createPhotoUri(): Uri {
        val timestamp = SimpleDateFormat(AppConstants.PHOTO_TIMESTAMP_FORMAT, Locale.getDefault()).format(Date())
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

    fun compressPhoto(originalUri: Uri): Uri {
        val originalFile = getPhotoFileFromUri(originalUri)
            ?: return originalUri

        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(originalFile.absolutePath, options)
        val origWidth = options.outWidth
        val origHeight = options.outHeight

        if (origWidth <= 0 || origHeight <= 0) return originalUri

        val maxDim = AppConstants.MAX_PHOTO_DIM
        val sampleSize = if (origWidth > maxDim || origHeight > maxDim) {
            var ratio = 1
            while (origWidth / ratio > maxDim || origHeight / ratio > maxDim) ratio *= 2
            ratio
        } else 1

        val decodeOpts = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
            inMutable = true
        }
        val bitmap = BitmapFactory.decodeFile(originalFile.absolutePath, decodeOpts)
            ?: return originalUri

        val fixed = fixExifOrientation(originalFile.absolutePath, bitmap)

        var quality = AppConstants.INITIAL_WEBP_QUALITY
        var bytes: ByteArray
        do {
            val stream = ByteArrayOutputStream()
            fixed.compress(Bitmap.CompressFormat.WEBP, quality, stream)
            bytes = stream.toByteArray()
            quality -= AppConstants.QUALITY_STEP
        } while (bytes.size > AppConstants.MAX_COMPRESSED_SIZE_BYTES && quality > AppConstants.MIN_WEBP_QUALITY)

        fixed.recycle()

        val compressedFile = File(photoDir, originalFile.nameWithoutExtension + AppConstants.WEBP_EXTENSION)
        compressedFile.writeBytes(bytes)

        originalFile.delete()

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            compressedFile
        )
    }

    fun deletePhoto(uriString: String) {
        try {
            val uri = Uri.parse(uriString)
            val file = getPhotoFileFromUri(uri)
            file?.delete()
        } catch (_: Exception) { }
    }

    private fun fixExifOrientation(path: String, bitmap: Bitmap): Bitmap {
        return try {
            val exif = ExifInterface(path)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            val matrix = android.graphics.Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
                else -> return bitmap
            }
            val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            if (rotated != bitmap) bitmap.recycle()
            rotated
        } catch (_: Exception) {
            bitmap
        }
    }
}
