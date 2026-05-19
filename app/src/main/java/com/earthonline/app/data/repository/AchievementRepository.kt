package com.earthonline.app.data.repository

import com.earthonline.app.data.local.dao.AchievementDefinitionDao
import com.earthonline.app.data.local.dao.UserAchievementProgressDao
import com.earthonline.app.data.local.entity.AchievementDefinitionEntity
import com.earthonline.app.data.local.entity.UserAchievementProgressEntity
import com.earthonline.app.domain.model.TriggerType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton

data class UnlockedAchievementEvent(
    val achievement: AchievementDefinitionEntity,
    val unlockedDate: Long
)

@Singleton
class AchievementRepository @Inject constructor(
    private val definitionDao: AchievementDefinitionDao,
    private val progressDao: UserAchievementProgressDao
) {
    private val _unlockEvents = MutableSharedFlow<UnlockedAchievementEvent>(replay = 0)
    val unlockEvents: SharedFlow<UnlockedAchievementEvent> = _unlockEvents

    private val _totalPhotos = MutableSharedFlow<Long>(replay = 1)
    val totalPhotos: SharedFlow<Long> = _totalPhotos

    suspend fun initializeAchievements() {
            val definitions = listOf(
                AchievementDefinitionEntity("photo_1", "初嚐記錄", "拍攝 1 次餐點", "ic_achievement_photo_1", TriggerType.PHOTO_UPLOAD_COUNT.value, 1L, false, 10),
                AchievementDefinitionEntity("photo_3", "三菜一湯", "拍攝 3 次餐點", "ic_achievement_photo_1", TriggerType.PHOTO_UPLOAD_COUNT.value, 3L, false, 5),
                AchievementDefinitionEntity("photo_5", "五感俱全", "拍攝 5 次餐點", "ic_achievement_photo_1", TriggerType.PHOTO_UPLOAD_COUNT.value, 5L, false, 10),
                AchievementDefinitionEntity("photo_7", "一週菜單", "拍攝 7 次餐點", "ic_achievement_photo_1", TriggerType.PHOTO_UPLOAD_COUNT.value, 7L, false, 15),
                AchievementDefinitionEntity("photo_10", "美食獵人", "拍攝 10 次餐點", "ic_achievement_photo_2", TriggerType.PHOTO_UPLOAD_COUNT.value, 10L, false, 50),
                AchievementDefinitionEntity("photo_25", "美食探險家", "拍攝 25 次餐點", "ic_achievement_photo_2", TriggerType.PHOTO_UPLOAD_COUNT.value, 25L, false, 100),
                AchievementDefinitionEntity("photo_50", "美食圖書館", "拍攝 50 次餐點", "ic_achievement_photo_2", TriggerType.PHOTO_UPLOAD_COUNT.value, 50L, false, 200),
                AchievementDefinitionEntity("photo_75", "舌尖上的旅途", "拍攝 75 次餐點", "ic_achievement_photo_2", TriggerType.PHOTO_UPLOAD_COUNT.value, 75L, false, 300),
                AchievementDefinitionEntity("photo_100", "百年食客", "拍攝 100 次餐點", "ic_achievement_photo_3", TriggerType.PHOTO_UPLOAD_COUNT.value, 100L, false, 500),
                AchievementDefinitionEntity("photo_150", "米其林之眼", "拍攝 150 次餐點", "ic_achievement_photo_3", TriggerType.PHOTO_UPLOAD_COUNT.value, 150L, false, 750),
                AchievementDefinitionEntity("photo_200", "美食評論家", "拍攝 200 次餐點", "ic_achievement_photo_3", TriggerType.PHOTO_UPLOAD_COUNT.value, 200L, false, 1000),
                AchievementDefinitionEntity("photo_250", "饕客名人堂", "拍攝 250 次餐點", "ic_achievement_photo_3", TriggerType.PHOTO_UPLOAD_COUNT.value, 250L, false, 1200),
                AchievementDefinitionEntity("photo_365", "全年無休", "拍攝 365 次餐點", "ic_achievement_photo_1", TriggerType.PHOTO_UPLOAD_COUNT.value, 365L, false, 2000),
                AchievementDefinitionEntity("photo_400", "食之史官", "拍攝 400 次餐點", "ic_achievement_photo_1", TriggerType.PHOTO_UPLOAD_COUNT.value, 400L, false, 2500),
                AchievementDefinitionEntity("photo_500", "人間食譜", "拍攝 500 次餐點", "ic_achievement_photo_2", TriggerType.PHOTO_UPLOAD_COUNT.value, 500L, false, 5000),
                AchievementDefinitionEntity("photo_666", "吃貨魔王", "拍攝 666 次餐點", "ic_achievement_photo_2", TriggerType.PHOTO_UPLOAD_COUNT.value, 666L, false, 3333),
                AchievementDefinitionEntity("photo_888", "食神發發發", "拍攝 888 次餐點", "ic_achievement_photo_3", TriggerType.PHOTO_UPLOAD_COUNT.value, 888L, false, 5000),
                AchievementDefinitionEntity("photo_999", "九九至尊食", "拍攝 999 次餐點", "ic_achievement_photo_3", TriggerType.PHOTO_UPLOAD_COUNT.value, 999L, false, 6000),
                AchievementDefinitionEntity("photo_1000", "食神降臨", "拍攝 1,000 次餐點", "ic_achievement_photo_1", TriggerType.PHOTO_UPLOAD_COUNT.value, 1000L, false, 10000),
                AchievementDefinitionEntity("photo_1500", "萬食之王", "拍攝 1,500 次餐點", "ic_achievement_photo_2", TriggerType.PHOTO_UPLOAD_COUNT.value, 1500L, false, 8000),
                AchievementDefinitionEntity("photo_2000", "美食之神", "拍攝 2,000 次餐點", "ic_achievement_photo_3", TriggerType.PHOTO_UPLOAD_COUNT.value, 2000L, false, 12000),
                AchievementDefinitionEntity("photo_5000", "永恆食典", "拍攝 5,000 次餐點", "ic_achievement_photo_1", TriggerType.PHOTO_UPLOAD_COUNT.value, 5000L, false, 25000),
                AchievementDefinitionEntity("photo_10000", "地球美食征服者", "拍攝 10,000 次餐點", "ic_achievement_photo_2", TriggerType.PHOTO_UPLOAD_COUNT.value, 10000L, false, 50000)
            )
            definitionDao.insertAll(definitions)

            val userId = "local_user"
            val progressList = definitions.map { def ->
                UserAchievementProgressEntity(
                    userId = userId,
                    achievementId = def.achievementId,
                    currentProgress = 0L,
                    isUnlocked = false,
                    unlockedDate = null,
                    triggerType = def.triggerType
                )
            }
            progressDao.insertAll(progressList)
    }

    suspend fun recordPhoto(): List<UnlockedAchievementEvent> {
        val userId = "local_user"
        val triggerType = TriggerType.PHOTO_UPLOAD_COUNT.value

        val photoCount = (progressDao.countByUserAndType(userId, triggerType) + 1).toLong()
        progressDao.incrementProgress(userId, triggerType, 1)

        _totalPhotos.emit(photoCount)

        return checkAndUnlock(userId, triggerType)
    }

    suspend fun getAllAchievementProgress(): List<UserAchievementProgressEntity> {
        return progressDao.getAllByUser("local_user")
    }

    suspend fun getAllDefinitions(): List<AchievementDefinitionEntity> {
        return definitionDao.getAll()
    }

    private suspend fun checkAndUnlock(
        userId: String,
        triggerType: String
    ): List<UnlockedAchievementEvent> {
        val unlockedProgress = progressDao.getUnlockedByUserAndType(userId, triggerType)
        val events = mutableListOf<UnlockedAchievementEvent>()

        for (progress in unlockedProgress) {
            val definition = definitionDao.getById(progress.achievementId) ?: continue
            if (progress.currentProgress >= definition.triggerGoal) {
                val now = System.currentTimeMillis()
                progressDao.unlockAchievement(userId, progress.achievementId, now)

                val event = UnlockedAchievementEvent(
                    achievement = definition,
                    unlockedDate = now
                )
                events.add(event)
                _unlockEvents.emit(event)
            }
        }

        return events
    }

    suspend fun refreshTotalPhotos() {
        val userId = "local_user"
        val total = progressDao.getTotalProgressByType(userId, TriggerType.PHOTO_UPLOAD_COUNT.value) ?: 0L
        _totalPhotos.emit(total)
    }
}
