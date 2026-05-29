package com.earthonline.app.ui.screens.dashboard

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.earthonline.app.R
import com.earthonline.app.data.backup.BackupManager
import com.earthonline.app.data.local.entity.CheckInRecord
import com.earthonline.app.data.photo.PhotoManager
import com.earthonline.app.data.repository.UnlockedAchievementEvent
import com.earthonline.app.data.repository.AchievementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val repository: AchievementRepository,
    private val backupManager: BackupManager,
    private val photoManager: PhotoManager,
    @ApplicationContext private val context: Context
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
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: context.getString(R.string.init_failed)) }
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
                val country = _uiState.value.pendingCountry
                val continent = _uiState.value.pendingContinent
                val address = _uiState.value.pendingAddress
                _uiState.update {
                    it.copy(
                        showCheckinConfirmDialog = false,
                        pendingLocation = null,
                        pendingAddress = ""
                    )
                }
                viewModelScope.launch {
                    val events = repository.recordCheckin(location.first, location.second, country, continent, address)
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
                    val result = repository.confirmManualAchievement(
                        achievementId = event.achievementId
                    )
                    if (result != null) {
                        handleUnlockEvents(listOf(result))
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
                val photoPath = _uiState.value.pendingEvidencePhotoPath
                _uiState.update {
                    it.copy(
                        pendingEvidenceAchievementId = null,
                        pendingEvidencePhotoPath = null,
                        analyzedLabels = emptyList()
                    )
                }
                if (photoPath != null) {
                    viewModelScope.launch { photoManager.deletePhoto(photoPath) }
                }
            }
            is DashboardEvent.RenamePet -> {
                viewModelScope.launch {
                    repository.renamePet(event.newName)
                    loadAchievementDisplay()
                }
            }
            is DashboardEvent.ChangePetEmoji -> {
                viewModelScope.launch {
                    repository.changePetEmoji(event.emoji)
                    loadAchievementDisplay()
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
        val (activity, activityEvents) = repository.evaluateActivityAchievements()
        handleUnlockEvents(activityEvents)
        val definitions = repository.getAllDefinitions()
        val allProgress = repository.getAllAchievementProgress()
        val displayItems = AchievementDisplayMapper.map(definitions, allProgress)
        val totalCheckins = repository.getCheckinCount().toLong()
        val totalPoints = repository.getTotalPoints()
        val unlockedCount = repository.getUnlockedCount()
        val level = repository.computePlayerLevel(totalPoints)
        val progress = repository.computeLevelProgress(totalPoints)
        val xpNext = repository.computeXpToNext(totalPoints)
        repository.computeAndSavePetStats()
        val petEntity = repository.getPet()
        val pet = repository.petToUiState(petEntity)

        _uiState.update {
            it.copy(
                totalCheckins = totalCheckins,
                totalPoints = totalPoints,
                unlockedCount = unlockedCount,
                playerLevel = level,
                levelProgress = progress,
                xpToNext = xpNext,
                achievements = displayItems,
                pet = pet,
                isLoading = false,
                walkingMinutes = activity.first,
                bikingMinutes = activity.second,
                bikingKm = activity.third
            )
        }
    }

    fun onUnlockEventHandled() {
    }

    fun injectTestActivityData() {
        android.widget.Toast.makeText(context, "Injecting test activity data...", android.widget.Toast.LENGTH_SHORT).show()
        viewModelScope.launch {
            repository.injectTestActivityData()
            loadAchievementDisplay()
            android.widget.Toast.makeText(context, "Done! Walking: ${_uiState.value.walkingMinutes}min Biking: ${_uiState.value.bikingMinutes}min", android.widget.Toast.LENGTH_LONG).show()
        }
    }

    fun retryLoad() {
        viewModelScope.launch {
            loadAchievementDisplay()
        }
    }

    suspend fun getEvidencePhoto(achievementId: String): String? {
        return repository.getEvidence(achievementId)?.photoPath
    }

    suspend fun getAllEvidencePhotos(achievementId: String): List<String> {
        return repository.getAllEvidenceForAchievement(achievementId).map { it.photoPath }
    }

    suspend fun getAllCheckinRecords(): List<CheckInRecord> {
        return repository.getAllCheckinRecords()
    }

    suspend fun exportBackup(uri: Uri) {
        backupManager.exportToUri(uri)
    }

    suspend fun importBackup(uri: Uri) {
        backupManager.importFromUri(uri)
        repository.syncAutoTrackFromHistory()
        repository.refreshAll()
        loadAchievementDisplay()
    }
}
