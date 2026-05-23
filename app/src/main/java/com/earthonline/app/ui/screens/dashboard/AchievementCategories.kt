package com.earthonline.app.ui.screens.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.earthonline.app.R
import com.earthonline.app.domain.model.TriggerType

data class AchievementCategory(
    val title: String,
    val predicate: (AchievementDisplayItem) -> Boolean
)

object AchievementCategories {
    @Composable
    fun getAll(items: List<AchievementDisplayItem>): List<Pair<String, List<AchievementDisplayItem>>> {
        val categories = listOf(
            stringResource(R.string.category_checkin) to { i: AchievementDisplayItem -> i.definition.triggerType == TriggerType.LOCATION_CHECKIN_COUNT.value },
            stringResource(R.string.category_explore) to { i: AchievementDisplayItem -> (i.definition.triggerType == TriggerType.MANUAL_CONFIRM.value || i.definition.triggerType == TriggerType.AUTO_TRACK.value) && i.definition.achievementId.startsWith("explore_") },
            stringResource(R.string.category_career) to { i: AchievementDisplayItem -> i.definition.triggerType == TriggerType.MANUAL_CONFIRM.value && i.definition.achievementId.startsWith("career_") },
            stringResource(R.string.category_daily) to { i: AchievementDisplayItem -> i.definition.triggerType == TriggerType.MANUAL_CONFIRM.value && i.definition.achievementId.startsWith("daily_") },
            stringResource(R.string.category_epic) to { i: AchievementDisplayItem -> (i.definition.triggerType == TriggerType.MANUAL_CONFIRM.value || i.definition.triggerType == TriggerType.AUTO_TRACK.value) && i.definition.achievementId.startsWith("epic_") },
            stringResource(R.string.category_health) to { i: AchievementDisplayItem -> i.definition.triggerType == TriggerType.MANUAL_CONFIRM.value && i.definition.achievementId.startsWith("health_") },
            stringResource(R.string.category_transport) to { i: AchievementDisplayItem -> i.definition.triggerType == TriggerType.MANUAL_CONFIRM.value && i.definition.achievementId.startsWith("transport_") },
            stringResource(R.string.category_ocean) to { i: AchievementDisplayItem -> i.definition.triggerType == TriggerType.MANUAL_CONFIRM.value && i.definition.achievementId.startsWith("ocean_") }
        )
        return categories.map { (title, pred) -> title to items.filter(pred) }.filter { it.second.isNotEmpty() }
    }
}
