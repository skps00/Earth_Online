package com.earthonline.app.ui.screens.dashboard

import com.earthonline.app.data.local.entity.AchievementDefinitionEntity
import com.earthonline.app.data.local.entity.UserAchievementProgressEntity

data class AchievementDisplayItem(
    val definition: AchievementDefinitionEntity,
    val progress: UserAchievementProgressEntity
)

data class DashboardUiState(
    val totalPhotos: Long = 0L,
    val achievements: List<AchievementDisplayItem> = emptyList(),
    val isLoading: Boolean = true,
    val showFoodConfirmDialog: Boolean = false
)

sealed class DashboardEvent {
    data class PhotoTaken(val success: Boolean) : DashboardEvent()
    data object FoodConfirmed : DashboardEvent()
    data object FoodRejected : DashboardEvent()
}
