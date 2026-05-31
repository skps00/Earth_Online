package com.earthonline.app.ui.screens.dashboard

// 成就顯示映射器，將資料庫實體轉換為 UI 顯示項目並排序

import com.earthonline.app.AppConstants
import com.earthonline.app.data.local.entity.AchievementDefinitionEntity
import com.earthonline.app.data.local.entity.UserAchievementProgressEntity
import com.earthonline.app.domain.model.TriggerType

// 成就顯示映射器，將資料庫實體轉換為 UI 顯示項目並排序
object AchievementDisplayMapper {

    private val allowedTypes = setOf(
        TriggerType.LOCATION_CHECKIN_COUNT.value,
        TriggerType.MANUAL_CONFIRM.value,
        TriggerType.AUTO_TRACK.value
    )

    // 將成就定義與進度配對，僅保留允許類型並依解鎖狀態排序
    fun map(
        definitions: List<AchievementDefinitionEntity>,
        allProgress: List<UserAchievementProgressEntity>
    ): List<AchievementDisplayItem> {
        return definitions
            .filter { it.triggerType in allowedTypes }
            .map { def ->
                val progress = allProgress.find { it.achievementId == def.achievementId }
                    ?: UserAchievementProgressEntity(
                        userId = AppConstants.LOCAL_USER_ID,
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
