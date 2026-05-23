package com.earthonline.app

import android.app.Application
import android.media.SoundPool
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EarthOnlineApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            val sp = SoundPool.Builder().setMaxStreams(1).build()
            val id = sp.load(this, R.raw.achievement_unlock, 1)
            sp.setOnLoadCompleteListener { _, _, status ->
                if (status == 0) sp.play(id, 1f, 1f, 1, 0, 1f)
            }
        } catch (_: Exception) { }
    }
}
