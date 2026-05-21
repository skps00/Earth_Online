package com.earthonline.app.ui.share

import android.content.Context
import android.content.Intent
import android.net.Uri

object ShareHelper {
    fun shareAchievement(
        context: Context,
        title: String,
        description: String,
        points: Int
    ) {
        val uri = ShareCardGenerator.generate(context, title, description, points)
        val shareText = "我在「地球 Online」解鎖了成就：$title！\n$description\n+$points 點數"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_TEXT, shareText)
            if (uri != null) putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, null))
    }
}
