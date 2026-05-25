package com.earthonline.app.ui.screens.dashboard

import com.earthonline.app.data.local.entity.AchievementDefinitionEntity
import com.earthonline.app.data.local.entity.UserAchievementProgressEntity

data class AchievementDisplayItem(
    val definition: AchievementDefinitionEntity,
    val progress: UserAchievementProgressEntity
)

data class PetUiState(
    val name: String = "地球精靈",
    val emoji: String = "🐉",
    val level: Int = 1,
    val strength: Int = 0,
    val agility: Int = 0,
    val intelligence: Int = 0,
    val charisma: Int = 0,
    val vitality: Int = 0
)

data class DashboardUiState(
    val totalCheckins: Long = 0L,
    val totalPoints: Long = 0L,
    val unlockedCount: Int = 0,
    val totalAchievements: Int = 129,
    val playerLevel: Int = 1,
    val levelProgress: Float = 0f,
    val xpToNext: Long = 100L,
    val achievements: List<AchievementDisplayItem> = emptyList(),
    val pet: PetUiState = PetUiState(),
    val isLoading: Boolean = true,
    val showCheckinConfirmDialog: Boolean = false,
    val pendingLocation: Pair<Double, Double>? = null,
    val pendingAddress: String = "",
    val pendingCountry: String = "",
    val pendingContinent: String = "",
    val pendingEvidenceAchievementId: String? = null,
    val pendingEvidencePhotoPath: String? = null,
    val analyzedLabels: List<String> = emptyList(),
    val errorMessage: String? = null
)

sealed class DashboardEvent {
    data object CheckInConfirmed : DashboardEvent()
    data object CheckInRejected : DashboardEvent()
    data class ManualConfirm(val achievementId: String) : DashboardEvent()
    data class EvidencePhotoTaken(val achievementId: String, val success: Boolean) : DashboardEvent()
    data object EvidenceConfirmed : DashboardEvent()
    data object EvidenceRejected : DashboardEvent()
    data class RenamePet(val newName: String) : DashboardEvent()
    data class ChangePetEmoji(val emoji: String) : DashboardEvent()
}
