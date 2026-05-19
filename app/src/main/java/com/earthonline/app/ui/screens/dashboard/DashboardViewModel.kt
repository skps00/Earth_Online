package com.earthonline.app.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.earthonline.app.data.local.entity.UserAchievementProgressEntity
import com.earthonline.app.data.repository.UnlockedAchievementEvent
import com.earthonline.app.domain.model.TriggerType
import com.earthonline.app.domain.service.AchievementService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val achievementService: AchievementService
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _unlockEvent = MutableSharedFlow<UnlockedAchievementEvent>(replay = 0)
    val unlockEvent: SharedFlow<UnlockedAchievementEvent> = _unlockEvent.asSharedFlow()

    init {
        initialize()
    }

    private fun initialize() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                achievementService.initialize()
                achievementService.refreshAll()
                loadAchievementDisplay()
            } catch (_: Exception) {
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.PhotoTaken -> {
                if (event.success) {
                    _uiState.update { it.copy(showFoodConfirmDialog = true) }
                }
            }
            DashboardEvent.FoodConfirmed -> {
                _uiState.update { it.copy(showFoodConfirmDialog = false) }
                viewModelScope.launch {
                    val events = achievementService.recordPhoto()
                    handleUnlockEvents(events)
                    achievementService.refreshAll()
                    loadAchievementDisplay()
                }
            }
            DashboardEvent.FoodRejected -> {
                _uiState.update { it.copy(showFoodConfirmDialog = false) }
            }
        }
    }

    private suspend fun handleUnlockEvents(events: List<UnlockedAchievementEvent>) {
        for (event in events) {
            _unlockEvent.emit(event)
        }
    }

    private suspend fun loadAchievementDisplay() {
        val definitions = achievementService.getAllDefinitions()
        val allProgress = achievementService.getAllAchievementProgress()

        val displayItems = definitions.map { def ->
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
        }.sortedWith(
            compareByDescending<AchievementDisplayItem> { it.progress.isUnlocked }
                .thenByDescending { it.progress.currentProgress }
        )

        val totalPhotos = allProgress
            .filter { it.triggerType == TriggerType.PHOTO_UPLOAD_COUNT.value }
            .maxOfOrNull { it.currentProgress } ?: 0L

        _uiState.update {
            it.copy(
                totalPhotos = totalPhotos,
                achievements = displayItems
            )
        }
    }

    fun onUnlockEventHandled() {
    }
}
