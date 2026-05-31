package com.earthonline.app.domain.model

// 觸發類型枚舉 — 定義成就的三種觸發方式

// 位置打卡計數觸發、手動確認觸發、自動追蹤觸發
enum class TriggerType(val value: String) {
    LOCATION_CHECKIN_COUNT("LOCATION_CHECKIN_COUNT"),
    MANUAL_CONFIRM("MANUAL_CONFIRM"),
    AUTO_TRACK("AUTO_TRACK");

    companion object {
        // 從字串值反查對應的 TriggerType 枚舉
        fun fromValue(value: String): TriggerType? {
            return entries.firstOrNull { it.value == value }
        }
    }
}
