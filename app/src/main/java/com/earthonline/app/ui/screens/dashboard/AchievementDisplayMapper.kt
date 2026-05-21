package com.earthonline.app.ui.screens.dashboard

import com.earthonline.app.data.local.entity.AchievementDefinitionEntity
import com.earthonline.app.data.local.entity.UserAchievementProgressEntity
import com.earthonline.app.domain.model.TriggerType

object AchievementDisplayMapper {

    private val allowedTypes = setOf(
        TriggerType.LOCATION_CHECKIN_COUNT.value,
        TriggerType.MANUAL_CONFIRM.value,
        TriggerType.AUTO_TRACK.value
    )

    fun map(
        definitions: List<AchievementDefinitionEntity>,
        allProgress: List<UserAchievementProgressEntity>
    ): List<AchievementDisplayItem> {
        return definitions
            .filter { it.triggerType in allowedTypes }
            .map { def ->
                val progress = allProgress.find { it.achievementId == def.achievementId }
                    ?: UserAchievementProgressEntity(
                        userId = "local_user",
                        achievementId = def.achievementId,
                        currentProgress = 0L,
                        isUnlocked = false,
                        unlockedDate = null,
                        triggerType = def.triggerType
                    )
                AchievementDisplayItem(definition = def, progress = progress)
            }
            .sortedWith(
                compareByDescending<AchievementDisplayItem> { it.progress.isUnlocked }
                    .thenByDescending { it.progress.currentProgress }
            )
    }
}
