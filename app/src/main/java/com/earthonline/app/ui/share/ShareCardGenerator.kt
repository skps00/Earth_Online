package com.earthonline.app.ui.share

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object ShareCardGenerator {

    fun generate(context: Context, title: String, description: String, points: Int): Uri? {
        val width = 1080
        val height = 1080
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Background
        canvas.drawColor(0xFF1A1A2E.toInt())

        // Top accent bar
        val accentPaint = Paint().apply { color = 0xFFFFD700.toInt() }
        canvas.drawRoundRect(RectF(60f, 80f, width - 60f, 88f), 4f, 4f, accentPaint)

        // Star icon
        val starPaint = Paint().apply {
            color = 0xFFFFD700.toInt()
            textSize = 120f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }
        canvas.drawText("★", width / 2f, 280f, starPaint)

        // Achievement title
        val titlePaint = Paint().apply {
            color = 0xFFFFD700.toInt()
            textSize = 52f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText(title, width / 2f, 420f, titlePaint)

        // Description
        val descPaint = Paint().apply {
            color = 0xFFB0B0B0.toInt()
            textSize = 32f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(description, width / 2f, 480f, descPaint)

        // Points
        val pointsPaint = Paint().apply {
            color = 0xFFFFD700.toInt()
            textSize = 44f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText("+ $points 點數", width / 2f, 560f, pointsPaint)

        // Divider
        canvas.drawLine(width / 4f, 620f, width * 3 / 4f, 620f, Paint().apply {
            color = 0x44FFD700.toInt()
            strokeWidth = 2f
        })

        // App name
        val appPaint = Paint().apply {
            color = 0x8850C878.toInt()
            textSize = 28f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("地球 Online · Earth Online", width / 2f, 700f, appPaint)

        // Tagline
        val taglinePaint = Paint().apply {
            color = 0x44888888.toInt()
            textSize = 22f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("你的人生 RPG 正在進行中", width / 2f, 750f, taglinePaint)

        // Save to cache
        val file = File(context.cacheDir, "share_achievement.png")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 95, it) }
        bitmap.recycle()

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}
