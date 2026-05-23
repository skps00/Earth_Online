package com.earthonline.app.data.media

import android.content.Context
import android.media.MediaPlayer

object SoundPlayer {
    private var currentPlayer: MediaPlayer? = null

    fun play(context: Context, resourceName: String) {
        stop()
        try {
            val resId = context.resources.getIdentifier(resourceName, "raw", context.packageName)
            if (resId != 0) {
                val player = MediaPlayer.create(context, resId)
                currentPlayer = player
                player?.start()
                player?.setOnCompletionListener {
                    it.release()
                    currentPlayer = null
                }
            }
        } catch (_: Exception) { }
    }

    fun stop() {
        currentPlayer?.stop()
        currentPlayer?.release()
        currentPlayer = null
    }
}
