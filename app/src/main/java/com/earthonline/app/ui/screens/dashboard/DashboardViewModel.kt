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

    fun setPendingLocation(latitude: Double, longitude: Double, address: String) {
        _uiState.update {
            it.copy(
                showCheckinConfirmDialog = true,
                pendingLocation = Pair(latitude, longitude),
                pendingAddress = address
            )
        }
    }

    fun setAnalyzedLabels(labels: List<String>) {
        _uiState.update { it.copy(analyzedLabels = labels) }
    }

    fun setEvidencePhotoPath(achievementId: String, path: String) {
        _uiState.update {
            it.copy(
                pendingEvidenceAchievementId = achievementId,
                pendingEvidencePhotoPath = path
            )
        }
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            DashboardEvent.CheckInConfirmed -> {
                val location = _uiState.value.pendingLocation ?: return
                _uiState.update {
                    it.copy(
                        showCheckinConfirmDialog = false,
                        pendingLocation = null,
                        pendingAddress = ""
                    )
                }
                viewModelScope.launch {
                    val events = achievementService.recordCheckin(location.first, location.second)
                    handleUnlockEvents(events)
                    achievementService.refreshAll()
                    loadAchievementDisplay()
                }
            }
            DashboardEvent.CheckInRejected -> {
                _uiState.update {
                    it.copy(
                        showCheckinConfirmDialog = false,
                        pendingLocation = null,
                        pendingAddress = ""
                    )
                }
            }
            is DashboardEvent.ManualConfirm -> {
                viewModelScope.launch {
                    val event = achievementService.confirmManualAchievement(
                        achievementId = event.achievementId
                    )
                    if (event != null) {
                        handleUnlockEvents(listOf(event))
                    }
                    achievementService.refreshAll()
                    loadAchievementDisplay()
                }
            }
            is DashboardEvent.EvidencePhotoTaken -> {
                _uiState.update {
                    it.copy(pendingEvidenceAchievementId = event.achievementId)
                }
            }
            DashboardEvent.EvidenceConfirmed -> {
                val achievementId = _uiState.value.pendingEvidenceAchievementId ?: return
                val photoPath = _uiState.value.pendingEvidencePhotoPath ?: return
                val labels = _uiState.value.analyzedLabels
                _uiState.update {
                    it.copy(
                        pendingEvidenceAchievementId = null,
                        pendingEvidencePhotoPath = null,
                        analyzedLabels = emptyList()
                    )
                }
                viewModelScope.launch {
                    val unlockEvent = achievementService.confirmManualAchievement(
                        achievementId = achievementId,
                        photoPath = photoPath,
                        labels = labels
                    )
                    if (unlockEvent != null) {
                        handleUnlockEvents(listOf(unlockEvent))
                    }
                    achievementService.refreshAll()
                    loadAchievementDisplay()
                }
            }
            DashboardEvent.EvidenceRejected -> {
                _uiState.update {
                    it.copy(
                        pendingEvidenceAchievementId = null,
                        pendingEvidencePhotoPath = null,
                        analyzedLabels = emptyList()
                    )
                }
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

        val allowedTypes = setOf(
            TriggerType.LOCATION_CHECKIN_COUNT.value,
            TriggerType.MANUAL_CONFIRM.value
        )

        val displayItems = definitions
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

        val totalCheckins = achievementService.getCheckinCount().toLong()

        _uiState.update {
            it.copy(
                totalCheckins = totalCheckins,
                achievements = displayItems
            )
        }
    }

    fun onUnlockEventHandled() {
    }
}
