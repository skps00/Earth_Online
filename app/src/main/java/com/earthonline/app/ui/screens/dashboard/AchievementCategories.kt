package com.earthonline.app.ui.screens.dashboard

import com.earthonline.app.domain.model.TriggerType

data class AchievementCategory(
    val title: String,
    val predicate: (AchievementDisplayItem) -> Boolean
)

object AchievementCategories {
    fun getAll(items: List<AchievementDisplayItem>): List<Pair<String, List<AchievementDisplayItem>>> {
        val categories = listOf(
            "📍 打卡" to { i: AchievementDisplayItem -> i.definition.triggerType == TriggerType.LOCATION_CHECKIN_COUNT.value },
            "🗺️ 探索" to { i: AchievementDisplayItem -> (i.definition.triggerType == TriggerType.MANUAL_CONFIRM.value || i.definition.triggerType == TriggerType.AUTO_TRACK.value) && i.definition.achievementId.startsWith("explore_") },
            "🎓 職涯" to { i: AchievementDisplayItem -> i.definition.triggerType == TriggerType.MANUAL_CONFIRM.value && i.definition.achievementId.startsWith("career_") },
            "🎭 日常" to { i: AchievementDisplayItem -> i.definition.triggerType == TriggerType.MANUAL_CONFIRM.value && i.definition.achievementId.startsWith("daily_") },
            "🏆 史詩" to { i: AchievementDisplayItem -> (i.definition.triggerType == TriggerType.MANUAL_CONFIRM.value || i.definition.triggerType == TriggerType.AUTO_TRACK.value) && i.definition.achievementId.startsWith("epic_") },
            "🩺 健康" to { i: AchievementDisplayItem -> i.definition.triggerType == TriggerType.MANUAL_CONFIRM.value && i.definition.achievementId.startsWith("health_") },
            "🚗 交通" to { i: AchievementDisplayItem -> i.definition.triggerType == TriggerType.MANUAL_CONFIRM.value && i.definition.achievementId.startsWith("transport_") },
            "🌊 大海" to { i: AchievementDisplayItem -> i.definition.triggerType == TriggerType.MANUAL_CONFIRM.value && i.definition.achievementId.startsWith("ocean_") }
        )
        return categories.map { (title, pred) -> title to items.filter(pred) }.filter { it.second.isNotEmpty() }
    }
}
