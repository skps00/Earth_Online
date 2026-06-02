package com.earthonline.app.ui.share

// 成就分享卡片生成器：使用 Android Canvas 繪製 1080x1080 分享圖片，含標題、描述、點數與品牌資訊
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import androidx.core.content.FileProvider
import com.earthonline.app.AppConstants
import com.earthonline.app.R
import java.io.File
import java.io.FileOutputStream

private const val SHARE_CARD_WIDTH = 1080
private const val SHARE_CARD_HEIGHT = 1080
private const val SHARE_CARD_PNG_QUALITY = 95
private const val COLOR_DEEP_BLUE = 0xFF1A1A2E.toInt()
private const val COLOR_GOLD = 0xFFFFD700.toInt()
private const val COLOR_TEXT_SECONDARY = 0xFFB0B0B0.toInt()
private const val COLOR_GOLD_TRANSLUCENT_44 = 0x44FFD700.toInt()
private const val COLOR_EMERALD_TRANSLUCENT_88 = 0x8850C878.toInt()
private const val COLOR_GRAY_TRANSLUCENT_44 = 0x44888888.toInt()

object ShareCardGenerator {

// 生成成就分享卡片：Canvas 繪製 1080x1080 圖片，含金色星形、標題、描述、點數與品牌標語
    fun generate(context: Context, title: String, description: String, points: Int): Uri? {
        val width = SHARE_CARD_WIDTH
        val height = SHARE_CARD_HEIGHT
        val (bitmap, canvas) = createBitmap(width, height)

        drawCardBackground(canvas, width, height)
        drawGoldHeader(canvas, width)
        drawAchievementTitle(canvas, title, width)
        drawAchievementDescription(canvas, description, width)
        drawPointsDisplay(canvas, points, width, context)
        drawBottomAppName(canvas, width, context)

        return saveAndReturn(context, bitmap)
    }

    private fun createBitmap(width: Int, height: Int): Pair<Bitmap, Canvas> {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        return Pair(bitmap, canvas)
    }

    private fun drawCardBackground(canvas: Canvas, width: Int, height: Int) {
        canvas.drawColor(COLOR_DEEP_BLUE)
    }

    private fun drawGoldHeader(canvas: Canvas, width: Int) {
        val accentPaint = Paint().apply { color = COLOR_GOLD }
        canvas.drawRoundRect(RectF(60f, 80f, width - 60f, 88f), 4f, 4f, accentPaint)

        val starPaint = Paint().apply {
            color = COLOR_GOLD
            textSize = 120f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }
        canvas.drawText("\u2605", width / 2f, 280f, starPaint)
    }

    private fun drawAchievementTitle(canvas: Canvas, title: String, width: Int) {
        val titlePaint = Paint().apply {
            color = COLOR_GOLD
            textSize = 52f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText(title, width / 2f, 420f, titlePaint)
    }

    private fun drawAchievementDescription(canvas: Canvas, description: String, width: Int) {
        val descPaint = Paint().apply {
            color = COLOR_TEXT_SECONDARY
            textSize = 32f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(description, width / 2f, 480f, descPaint)
    }

    private fun drawPointsDisplay(canvas: Canvas, points: Int, width: Int, context: Context) {
        val pointsPaint = Paint().apply {
            color = COLOR_GOLD
            textSize = 44f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText(context.getString(R.string.share_points_format, points), width / 2f, 560f, pointsPaint)
    }

    private fun drawBottomAppName(canvas: Canvas, width: Int, context: Context) {
        canvas.drawLine(width / 4f, 620f, width * 3 / 4f, 620f, Paint().apply {
            color = COLOR_GOLD_TRANSLUCENT_44
            strokeWidth = 2f
        })

        val appPaint = Paint().apply {
            color = COLOR_EMERALD_TRANSLUCENT_88
            textSize = 28f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(context.getString(R.string.app_name), width / 2f, 700f, appPaint)

        val taglinePaint = Paint().apply {
            color = COLOR_GRAY_TRANSLUCENT_44
            textSize = 22f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(context.getString(R.string.life_rpg_subtitle), width / 2f, 750f, taglinePaint)
    }

    private fun saveAndReturn(context: Context, bitmap: Bitmap): Uri? {
        val file = File(context.cacheDir, "share_achievement.png")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, SHARE_CARD_PNG_QUALITY, it) }
        bitmap.recycle()

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}
