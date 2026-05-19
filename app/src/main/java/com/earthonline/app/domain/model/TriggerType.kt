package com.earthonline.app.domain.model

enum class TriggerType(val value: String) {
    STEPS_ACCUMULATED("STEPS_ACCUMULATED"),
    PHOTO_UPLOAD_COUNT("PHOTO_UPLOAD_COUNT");

    companion object {
        fun fromValue(value: String): TriggerType {
            return entries.first { it.value == value }
        }
    }
}
