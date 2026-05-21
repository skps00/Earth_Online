package com.earthonline.app.ui.screens.dashboard

import com.earthonline.app.data.local.entity.AchievementDefinitionEntity
import com.earthonline.app.data.local.entity.UserAchievementProgressEntity

data class AchievementDisplayItem(
    val definition: AchievementDefinitionEntity,
    val progress: UserAchievementProgressEntity
)

data class DashboardUiState(
    val totalCheckins: Long = 0L,
    val achievements: List<AchievementDisplayItem> = emptyList(),
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
}
