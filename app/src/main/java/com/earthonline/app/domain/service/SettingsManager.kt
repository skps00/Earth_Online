package com.earthonline.app.domain.service

import android.content.Context
import com.earthonline.app.AppConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)

    var soundEnabled: Boolean
        get() = !prefs.getBoolean(AppConstants.KEY_SOUND_MUTED, false)
        set(value) = prefs.edit().putBoolean(AppConstants.KEY_SOUND_MUTED, !value).apply()

    fun clearAllData() {
        prefs.edit().clear().apply()
        context.deleteDatabase(AppConstants.DATABASE_NAME)
    }
}
