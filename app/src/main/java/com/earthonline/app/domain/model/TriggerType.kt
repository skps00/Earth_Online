package com.earthonline.app.domain.model

enum class TriggerType(val value: String) {
    LOCATION_CHECKIN_COUNT("LOCATION_CHECKIN_COUNT"),
    MANUAL_CONFIRM("MANUAL_CONFIRM");

    companion object {
        fun fromValue(value: String): TriggerType {
            return entries.first { it.value == value }
        }
    }
}
