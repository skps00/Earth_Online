package com.earthonline.app.domain.service

// 設定管理器 — 集中管理音效、深色主題、活動追蹤等應用設定

import android.content.Context
import com.earthonline.app.AppConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

// 設定管理器 — 使用 SharedPreferences 持久化應用偏好設定
@Singleton
class SettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)

    // 音效開關 — 內部儲存為靜音模式（取反），對外暴露為啟用狀態
    var soundEnabled: Boolean
        get() = !prefs.getBoolean(AppConstants.KEY_SOUND_MUTED, false)
        set(value) = prefs.edit().putBoolean(AppConstants.KEY_SOUND_MUTED, !value).apply()

    // 深色主題開關 — 預設為啟用
    var darkModeEnabled: Boolean
        get() = prefs.getBoolean(AppConstants.KEY_DARK_MODE, true)
        set(value) = prefs.edit().putBoolean(AppConstants.KEY_DARK_MODE, value).apply()

    // 活動追蹤開關 — 內部儲存為停用狀態（取反）
    var activityTrackingEnabled: Boolean
        get() = !prefs.getBoolean(AppConstants.KEY_ACTIVITY_TRACKING_DISABLED, false)
        set(value) = prefs.edit().putBoolean(AppConstants.KEY_ACTIVITY_TRACKING_DISABLED, !value).apply()

    // 清除所有資料 — 設定、活動統計、資料庫、照片目錄
    fun clearAllData() {
        prefs.edit().clear().apply()
        context.getSharedPreferences("activity_stats", Context.MODE_PRIVATE).edit().clear().apply()
        val dbDir = context.getDatabasePath(AppConstants.DATABASE_NAME).parentFile
        if (dbDir?.exists() == true) dbDir.deleteRecursively()
        val photosDir = java.io.File(context.filesDir, "photos")
        if (photosDir.exists()) photosDir.deleteRecursively()
    }
}
