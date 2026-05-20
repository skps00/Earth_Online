package com.earthonline.app.domain.service

import com.earthonline.app.data.local.entity.AchievementDefinitionEntity
import com.earthonline.app.data.local.entity.AchievementEvidence
import com.earthonline.app.data.local.entity.UserAchievementProgressEntity
import com.earthonline.app.data.repository.AchievementRepository
import com.earthonline.app.data.repository.UnlockedAchievementEvent
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementService @Inject constructor(
    private val repository: AchievementRepository
) {
    val unlockEvents: SharedFlow<UnlockedAchievementEvent> = repository.unlockEvents
    val totalCheckins: SharedFlow<Long> = repository.totalCheckins

    suspend fun initialize() {
        repository.initializeAchievements()
    }

    suspend fun recordCheckin(latitude: Double, longitude: Double, country: String, continent: String): List<UnlockedAchievementEvent> {
        return repository.recordCheckin(latitude, longitude, country, continent)
    }

    suspend fun confirmManualAchievement(
        achievementId: String,
        photoPath: String? = null,
        labels: List<String>? = null
    ): UnlockedAchievementEvent? {
        return repository.confirmManualAchievement(achievementId, photoPath, labels)
    }

    suspend fun getEvidence(achievementId: String): AchievementEvidence? {
        return repository.getEvidence(achievementId)
    }

    suspend fun getAllDefinitions(): List<AchievementDefinitionEntity> {
        return repository.getAllDefinitions()
    }

    suspend fun getAllAchievementProgress(): List<UserAchievementProgressEntity> {
        return repository.getAllAchievementProgress()
    }

    suspend fun getCheckinCount(): Int {
        return repository.getCheckinCount()
    }

    suspend fun getUniqueLocationCount(): Int {
        return repository.getUniqueLocationCount()
    }

    suspend fun refreshAll() {
        repository.refreshTotalCheckins()
    }
}
