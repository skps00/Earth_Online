package com.earthonline.app.data.photo

// 照片管理器 — 處理拍照、壓縮為 WebP、EXIF 旋轉修正與刪除

import android.util.Log
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

// 照片管理器 — 管理照片目錄與 FileProvider URI 生成
private const val TAG = "PhotoManager"

@Singleton
class PhotoManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // 照片儲存目錄 — 不存在時自動建立
    private val photoDir: File
        get() {
            val dir = File(context.filesDir, AppConstants.PHOTOS_DIR)
            if (!dir.exists()) dir.mkdirs()
            return dir
        }

    // 建立新照片的 FileProvider URI — 以時間戳記命名
    fun createPhotoUri(): Uri {
        val timestamp = SimpleDateFormat(AppConstants.PHOTO_TIMESTAMP_FORMAT, Locale.getDefault()).format(Date())
        val file = File(photoDir, "EARTH_ONLINE_${timestamp}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    // 從 URI 取得對應的 File 物件 — 用於檢查檔案是否存在
    fun getPhotoFileFromUri(uri: Uri): File? {
        val filename = uri.lastPathSegment ?: return null
        val file = File(photoDir, filename)
        return if (file.exists()) file else null
    }

    // 壓縮照片 — 縮小至最大尺寸、修正 EXIF 旋轉、轉 WebP 並遞減質量至目標大小
    fun compressPhoto(originalUri: Uri): Uri {
        val originalFile = getPhotoFileFromUri(originalUri)
            ?: return originalUri

        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(originalFile.absolutePath, options)
        val origWidth = options.outWidth
        val origHeight = options.outHeight

        if (origWidth <= 0 || origHeight <= 0) return originalUri

        // 計算採樣比例：若圖片任一邊超過最大尺寸則等比縮小
        val maxDim = AppConstants.MAX_PHOTO_DIM
        val sampleSize = if (origWidth > maxDim || origHeight > maxDim) {
            var ratio = 1
            while (origWidth / ratio > maxDim || origHeight / ratio > maxDim) ratio *= 2
            ratio
        } else 1

        val safeSampleSize = maxOf(sampleSize, 2)

        val decodeOpts = BitmapFactory.Options().apply {
            inSampleSize = safeSampleSize
            inMutable = true
        }
        val bitmap = BitmapFactory.decodeFile(originalFile.absolutePath, decodeOpts)
            ?: return originalUri

        val fixed = fixExifOrientation(originalFile.absolutePath, bitmap)

        // 遞減 WebP 質量直到檔案大小達標或質量低於下限
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

    // 刪除指定 URI 的照片檔案 — 用於取消證據照時清理
    fun deletePhoto(uriString: String) {
        try {
            val uri = Uri.parse(uriString)
            val file = getPhotoFileFromUri(uri)
            file?.delete()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete photo: $uriString", e)
        }
    }

    // 根據 EXIF 旋轉資訊修正 Bitmap 方向 — 處理拍照時的旋轉標記
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
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fix EXIF orientation", e)
            bitmap
        }
    }
}
