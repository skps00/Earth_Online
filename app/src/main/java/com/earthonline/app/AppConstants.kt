package com.earthonline.app

object AppConstants {
    const val LOCAL_USER_ID = "local_user"
    const val DATABASE_NAME = "earth_online.db"
    const val PREFS_NAME = "earth_online_settings"

    const val KEY_ONBOARDING_SHOWN = "onboarding_shown"
    const val KEY_SOUND_MUTED = "sound_muted"

    const val DEFAULT_PET_NAME = "地球精靈"
    const val DEFAULT_PET_EMOJI = "🐉"
    const val DEFAULT_PET_LEVEL = 1
    const val DEFAULT_PET_XP = 0L

    const val DEFAULT_BACKUP_FILENAME = "earth_online_backup.json"
    const val MIME_JSON = "application/json"

    const val MAX_PHOTO_DIM = 1080
    const val MAX_COMPRESSED_SIZE_BYTES = 200 * 1024
    const val INITIAL_WEBP_QUALITY = 80
    const val MIN_WEBP_QUALITY = 10
    const val QUALITY_STEP = 5
    const val PHOTOS_DIR = "photos"
    const val WEBP_EXTENSION = ".webp"
    const val PHOTO_TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss"

    const val NOMINATIM_CONNECT_TIMEOUT_MS = 5000
    const val NOMINATIM_READ_TIMEOUT_MS = 5000
    const val MAX_GEOCODER_RESULTS = 5

    const val STAT_DIVISOR = 10f
    const val TOTAL_ACHIEVEMENT_COUNT = 129
}
