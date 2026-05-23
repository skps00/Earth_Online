package com.earthonline.app.domain.service

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("earth_online_settings", Context.MODE_PRIVATE)

    var soundEnabled: Boolean
        get() = !prefs.getBoolean("sound_muted", false)
        set(value) = prefs.edit().putBoolean("sound_muted", !value).apply()

    fun clearAllData() {
        prefs.edit().clear().apply()
        context.deleteDatabase("earth_online.db")
    }
}
