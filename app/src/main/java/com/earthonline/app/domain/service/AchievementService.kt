package com.earthonline.app.domain.service

import com.earthonline.app.data.local.entity.AchievementDefinitionEntity
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
    val totalPhotos: SharedFlow<Long> = repository.totalPhotos

    suspend fun initialize() {
        repository.initializeAchievements()
    }

    suspend fun recordPhoto(): List<UnlockedAchievementEvent> {
        return repository.recordPhoto()
    }

    suspend fun getAllDefinitions(): List<AchievementDefinitionEntity> {
        return repository.getAllDefinitions()
    }

    suspend fun getAllAchievementProgress(): List<UserAchievementProgressEntity> {
        return repository.getAllAchievementProgress()
    }

    suspend fun refreshAll() {
        repository.refreshTotalPhotos()
    }
}
