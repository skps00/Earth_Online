package com.earthonline.app.data.media

// 音效播放器 — 播放 raw 資源中的音效檔，支援靜音控制

import com.earthonline.app.AppConstants
import android.content.Context
import android.media.MediaPlayer
import android.util.Log

// 單例音效播放器 — 同一時間只播放一個音效
object SoundPlayer {
    private const val TAG = "SoundPlayer"
    private var currentPlayer: MediaPlayer? = null

    // 播放指定資源名稱的音效 — 靜音時跳過，播放完自動釋放
    @Synchronized
    fun play(context: Context, resourceName: String) {
        val muted = context.getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(AppConstants.KEY_SOUND_MUTED, false)
        if (muted) return
        stop()
        try {
            val resId = context.resources.getIdentifier(resourceName, "raw", context.packageName)
            if (resId != 0) {
                val player = MediaPlayer.create(context, resId)
                currentPlayer = player
                player?.start()
                player?.setOnCompletionListener { mp ->
                    if (mp == currentPlayer) {
                        mp.release()
                        currentPlayer = null
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play sound: $resourceName", e)
        }
    }

    @Synchronized
    fun stop() {
        val player = currentPlayer
        if (player != null) {
            player.setOnCompletionListener(null)
            player.stop()
            player.release()
            currentPlayer = null
        }
    }
}
