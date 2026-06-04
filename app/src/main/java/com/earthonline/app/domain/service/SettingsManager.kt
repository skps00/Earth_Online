package com.earthonline.app.domain.service

import android.content.Context
import com.earthonline.app.AppConstants
import com.earthonline.app.ui.theme.ThemeConfig
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

    @Deprecated("Use currentThemeId instead", ReplaceWith("currentThemeId"))
    var darkModeEnabled: Boolean
        get() = currentThemeId == ThemeConfig.rpgDark.id
        set(value) {
            currentThemeId = if (value) ThemeConfig.rpgDark.id else ThemeConfig.rpgLight.id
        }

    var currentThemeId: String
        get() = prefs.getString(AppConstants.KEY_THEME_ID, ThemeConfig.default.id) ?: ThemeConfig.default.id
        set(value) = prefs.edit().putString(AppConstants.KEY_THEME_ID, value).apply()

    var activityTrackingEnabled: Boolean
        get() = !prefs.getBoolean(AppConstants.KEY_ACTIVITY_TRACKING_DISABLED, false)
        set(value) = prefs.edit().putBoolean(AppConstants.KEY_ACTIVITY_TRACKING_DISABLED, !value).apply()

    var permissionRemindersEnabled: Boolean
        get() = !prefs.getBoolean("permission_reminders_disabled", false)
        set(value) = prefs.edit().putBoolean("permission_reminders_disabled", !value).apply()

    fun clearAllData() {
        prefs.edit().clear().apply()
        context.getSharedPreferences(AppConstants.ACTIVITY_STATS_PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply()
        val dbDir = context.getDatabasePath(AppConstants.DATABASE_NAME).parentFile
        if (dbDir?.exists() == true) dbDir.deleteRecursively()
        val photosDir = java.io.File(context.filesDir, AppConstants.PHOTOS_DIR)
        if (photosDir.exists()) photosDir.deleteRecursively()
    }
}
