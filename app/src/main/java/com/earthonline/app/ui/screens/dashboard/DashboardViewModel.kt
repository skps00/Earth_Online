package com.earthonline.app.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.earthonline.app.data.repository.UnlockedAchievementEvent
import com.earthonline.app.data.repository.AchievementRepository
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
    private val repository: AchievementRepository
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
                repository.initializeAchievements()
                loadAchievementDisplay()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "初始化失敗") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun setPendingLocation(latitude: Double, longitude: Double, address: String, country: String, continent: String) {
        _uiState.update {
            it.copy(
                showCheckinConfirmDialog = true,
                pendingLocation = Pair(latitude, longitude),
                pendingAddress = address,
                pendingCountry = country,
                pendingContinent = continent
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
                    val events = repository.recordCheckin(location.first, location.second, _uiState.value.pendingCountry, _uiState.value.pendingContinent)
                    handleUnlockEvents(events)
                    repository.refreshAll()
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
                    val event = repository.confirmManualAchievement(
                        achievementId = event.achievementId
                    )
                    if (event != null) {
                        handleUnlockEvents(listOf(event))
                    }
                    repository.refreshAll()
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
                    val unlockEvent = repository.confirmManualAchievement(
                        achievementId = achievementId,
                        photoPath = photoPath,
                        labels = labels
                    )
                    if (unlockEvent != null) {
                        handleUnlockEvents(listOf(unlockEvent))
                    }
                    repository.refreshAll()
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
        for (e in events) _unlockEvent.emit(e)
    }

    private suspend fun loadAchievementDisplay() {
        repository.syncAutoTrackFromHistory()
        repository.refreshAll()
        val definitions = repository.getAllDefinitions()
        val allProgress = repository.getAllAchievementProgress()
        val displayItems = AchievementDisplayMapper.map(definitions, allProgress)
        val totalCheckins = repository.getCheckinCount().toLong()

        _uiState.update {
            it.copy(
                totalCheckins = totalCheckins,
                achievements = displayItems
            )
        }
    }

    fun onUnlockEventHandled() {
    }

    fun retryLoad() {
        viewModelScope.launch {
            loadAchievementDisplay()
        }
    }

    suspend fun getEvidencePhoto(achievementId: String): String? {
        return repository.getEvidence(achievementId)?.photoPath
    }
}
