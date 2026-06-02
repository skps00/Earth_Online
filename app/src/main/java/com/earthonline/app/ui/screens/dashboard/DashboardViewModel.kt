package com.earthonline.app.ui.screens.dashboard

// 儀表板 ViewModel，處理打卡、成就解鎖、寵物自訂與活動追蹤等核心邏輯

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.earthonline.app.R
import com.earthonline.app.AppConstants
import com.earthonline.app.data.backup.BackupManager
import com.earthonline.app.data.backup.BackupResult
import com.earthonline.app.data.local.entity.CheckInRecord
import com.earthonline.app.data.photo.PhotoManager
import com.earthonline.app.data.screentime.ScreenTimeManager
import com.earthonline.app.data.repository.UnlockedAchievementEvent
import com.earthonline.app.data.repository.AchievementRepository
import com.earthonline.app.domain.service.SettingsManager
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
    private val settingsManager: SettingsManager,
    private val screenTimeManager: ScreenTimeManager,
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

    fun setPendingLocation(latitude: Double, longitude: Double, address: String, country: String, continent: String, altitude: Double? = null) {
        _uiState.update {
            it.copy(
                showCheckinConfirmDialog = true,
                pendingLocation = Pair(latitude, longitude),
                pendingAddress = address,
                pendingCountry = country,
                pendingContinent = continent,
                pendingAltitude = altitude
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
            DashboardEvent.CheckInConfirmed -> handleCheckInConfirmed()
            DashboardEvent.CheckInRejected -> handleCheckInRejected()
            is DashboardEvent.ManualConfirm -> handleManualConfirm(event)
            is DashboardEvent.EvidencePhotoTaken -> handleEvidencePhotoTaken(event)
            DashboardEvent.EvidenceConfirmed -> handleEvidenceConfirmed()
            DashboardEvent.EvidenceRejected -> handleEvidenceRejected()
            is DashboardEvent.RenamePet -> handleRenamePet(event)
            is DashboardEvent.ChangePetEmoji -> handleChangePetEmoji(event)
        }
    }

    private fun handleCheckInConfirmed() {
        val location = _uiState.value.pendingLocation ?: return
        val country = _uiState.value.pendingCountry
        val continent = _uiState.value.pendingContinent
        val address = _uiState.value.pendingAddress
        val altitude = _uiState.value.pendingAltitude
        _uiState.update {
            it.copy(
                showCheckinConfirmDialog = false,
                pendingLocation = null,
                pendingAddress = ""
            )
        }
        viewModelScope.launch {
            val events = repository.recordCheckin(location.first, location.second, country, continent, address, altitude)
            handleUnlockEvents(events)
            repository.refreshAll()
            loadAchievementDisplay()
        }
    }

    private fun handleCheckInRejected() {
        _uiState.update {
            it.copy(
                showCheckinConfirmDialog = false,
                pendingLocation = null,
                pendingAddress = ""
            )
        }
    }

    private fun handleManualConfirm(event: DashboardEvent.ManualConfirm) {
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

    private fun handleEvidencePhotoTaken(event: DashboardEvent.EvidencePhotoTaken) {
        _uiState.update {
            it.copy(pendingEvidenceAchievementId = event.achievementId)
        }
    }

    private fun handleEvidenceConfirmed() {
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

    private fun handleEvidenceRejected() {
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

    private fun handleRenamePet(event: DashboardEvent.RenamePet) {
        viewModelScope.launch {
            repository.renamePet(event.newName)
            loadAchievementDisplay()
        }
    }

    private fun handleChangePetEmoji(event: DashboardEvent.ChangePetEmoji) {
        viewModelScope.launch {
            repository.changePetEmoji(event.emoji)
            loadAchievementDisplay()
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
        val screenTimeEvents = repository.evaluateScreenTimeAchievements()
        handleUnlockEvents(screenTimeEvents)
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
        val screenMinutes = screenTimeManager.getTodayTotalScreenTimeMinutes()
        val usageStatsGranted = screenTimeManager.isUsageStatsPermissionGranted()

        val locationGranted = androidx.core.content.ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        val activityGranted = androidx.core.content.ContextCompat.checkSelfPermission(
            context, AppConstants.ACTIVITY_RECOGNITION_PERMISSION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        val cameraGranted = androidx.core.content.ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        val anyInAppMissing = !locationGranted || !activityGranted || !cameraGranted
        val remindersEnabled = settingsManager.permissionRemindersEnabled

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
                bikingKm = activity.third,
                screenTimeMinutes = screenMinutes,
                locationPermissionGranted = locationGranted,
                activityPermissionGranted = activityGranted,
                cameraPermissionGranted = cameraGranted,
                showUnifiedPermissionDialog = anyInAppMissing && remindersEnabled,
                showScreenTimePermissionDialog = !usageStatsGranted && remindersEnabled
            )
        }
    }

    fun onUnlockEventHandled() {
    }

    fun dismissUnifiedPermissionDialog() {
        _uiState.update { it.copy(showUnifiedPermissionDialog = false) }
    }

    fun setPermissionGranted(permission: String, granted: Boolean) {
        _uiState.update {
            when (permission) {
                Manifest.permission.ACCESS_FINE_LOCATION -> it.copy(locationPermissionGranted = granted)
                AppConstants.ACTIVITY_RECOGNITION_PERMISSION -> it.copy(activityPermissionGranted = granted)
                Manifest.permission.CAMERA -> it.copy(cameraPermissionGranted = granted)
                else -> it
            }
        }
    }

    fun openScreenTimeSettings() {
        screenTimeManager.openUsageAccessSettings()
    }

    fun dismissScreenTimePermissionDialog() {
        _uiState.update { it.copy(showScreenTimePermissionDialog = false) }
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

    suspend fun exportBackup(uri: Uri): BackupResult {
        return backupManager.exportToUri(uri)
    }

    suspend fun importBackup(uri: Uri): BackupResult {
        val result = backupManager.importFromUri(uri)
        if (result is BackupResult.Success) {
            repository.syncAutoTrackFromHistory()
            repository.refreshAll()
            loadAchievementDisplay()
        }
        return result
    }
}
