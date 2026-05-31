package com.earthonline.app.ui.share

// 分享輔助工具：生成分享卡片圖片並調用系統分享選單，支援圖文同時分享
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.earthonline.app.R

object ShareHelper {
// 分享成就：生成分享圖片 URI 並啟動系統分享選單，同時附加文字內容
    fun shareAchievement(
        context: Context,
        title: String,
        description: String,
        points: Int
    ) {
        val uri = ShareCardGenerator.generate(context, title, description, points)
        val shareText = context.getString(R.string.share_achievement_template, title, description, points)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_TEXT, shareText)
            if (uri != null) putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, null))
    }
}
