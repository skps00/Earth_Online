package com.earthonline.app

// 應用程式集中常數定義，包含資料庫名稱、設定鍵值、寵物預設值、照片壓縮參數、動畫參數等
object AppConstants {
    // 本地用戶 ID、資料庫與設定檔名稱
    const val LOCAL_USER_ID = "local_user"
    const val DATABASE_NAME = "earth_online.db"
    const val PREFS_NAME = "earth_online_settings"

    // SharedPreferences 設定鍵值
    const val KEY_ONBOARDING_SHOWN = "onboarding_shown"
    const val KEY_SOUND_MUTED = "sound_muted"
    const val KEY_DARK_MODE = "dark_mode"
    const val KEY_ACTIVITY_PERMISSION_REQUESTED = "activity_permission_requested"

    // 寵物預設值
    const val DEFAULT_PET_NAME = "地球精靈"
    const val DEFAULT_PET_EMOJI = "🐉"
    const val DEFAULT_PET_LEVEL = 1
    const val DEFAULT_PET_XP = 0L

    // 備份檔案名稱與 MIME 類型
    const val DEFAULT_BACKUP_FILENAME = "earth_online_backup.json"
    const val MIME_JSON = "application/json"

    // 照片壓縮參數
    const val MAX_PHOTO_DIM = 1080
    const val MAX_COMPRESSED_SIZE_BYTES = 200 * 1024
    const val INITIAL_WEBP_QUALITY = 80
    const val MIN_WEBP_QUALITY = 10
    const val QUALITY_STEP = 5
    const val PHOTOS_DIR = "photos"
    const val WEBP_EXTENSION = ".webp"
    const val PHOTO_TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss"

    // 地理編碼網路請求參數（Nominatim API）
    const val NOMINATIM_CONNECT_TIMEOUT_MS = 5000
    const val NOMINATIM_READ_TIMEOUT_MS = 5000
    const val MAX_GEOCODER_RESULTS = 5

    // 寵物屬性計算除數與成就總數
    const val STAT_DIVISOR = 10f
    const val TOTAL_ACHIEVEMENT_COUNT = 129

    // UI 動畫參數
    const val BOUNCE_DAMPING_RATIO = 0.4f
    const val BOUNCE_STIFFNESS = 400f
    const val SPEECH_BUBBLE_MIN_INTERVAL_MS = 5000L
    const val SPEECH_BUBBLE_MAX_EXTRA_MS = 5000L
    const val SPEECH_BUBBLE_DISPLAY_MS = 4000L
    const val CARD_ANIMATION_DURATION_MS = 600
    const val CROSSFADE_DURATION_MS = 300
    const val INDICATOR_ANIMATION_DURATION_MS = 300
}
