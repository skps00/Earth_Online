package com.earthonline.app.ui.screens.dashboard

// 儀表板 ViewModel，處理打卡、成就解鎖、寵物自訂與活動追蹤等核心邏輯

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

// 儀表板核心 ViewModel，管理 UI 狀態、打卡流程與成就解鎖事件
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: AchievementRepository,
    private val backupManager: BackupManager,
    private val photoManager: PhotoManager,
    private val settingsManager: SettingsManager,
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

    // 設定待確認的簽到位置資訊並顯示確認對話框
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

    // 設定照片分析標籤結果
    fun setAnalyzedLabels(labels: List<String>) {
        _uiState.update { it.copy(analyzedLabels = labels) }
    }

    // 設定待確認的證據照片路徑
    fun setEvidencePhotoPath(achievementId: String, path: String) {
        _uiState.update {
            it.copy(
                pendingEvidenceAchievementId = achievementId,
                pendingEvidencePhotoPath = path
            )
        }
    }

    // 分發處理所有儀表板事件：簽到、成就確認、證據、寵物操作
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

    // 處理打卡確認：記錄打卡、觸發成就解鎖、重新載入儀表板
    private fun handleCheckInConfirmed() {
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

    // 處理打卡拒絕：關閉確認對話框、清除暫存位置與地址
    private fun handleCheckInRejected() {
        _uiState.update {
            it.copy(
                showCheckinConfirmDialog = false,
                pendingLocation = null,
                pendingAddress = ""
            )
        }
    }

    // 處理手動確認成就：呼叫 Repository 確認、觸發解鎖事件、重新載入
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

    // 處理證據照片拍攝完成：記錄待確認的成就 ID 至 UI 狀態
    private fun handleEvidencePhotoTaken(event: DashboardEvent.EvidencePhotoTaken) {
        _uiState.update {
            it.copy(pendingEvidenceAchievementId = event.achievementId)
        }
    }

    // 處理證據確認：使用照片與標籤確認成就、觸發解鎖、重新載入
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

    // 處理證據拒絕：清除暫存狀態、刪除已拍攝的照片檔案
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

    // 處理寵物重新命名：呼叫 Repository 更新名稱並重新載入顯示
    private fun handleRenamePet(event: DashboardEvent.RenamePet) {
        viewModelScope.launch {
            repository.renamePet(event.newName)
            loadAchievementDisplay()
        }
    }

    // 處理寵物 Emoji 變更：呼叫 Repository 更新圖標並重新載入顯示
    private fun handleChangePetEmoji(event: DashboardEvent.ChangePetEmoji) {
        viewModelScope.launch {
            repository.changePetEmoji(event.emoji)
            loadAchievementDisplay()
        }
    }

    // 將解鎖事件逐一發射到 SharedFlow 供 UI 顯示動畫
    private suspend fun handleUnlockEvents(events: List<UnlockedAchievementEvent>) {
        for (e in events) _unlockEvent.emit(e)
    }

    // 重新載入所有成就資料、計算玩家等級與寵物狀態並更新 UI
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
                bikingKm = activity.third,
                activityPermissionGranted = androidx.core.content.ContextCompat.checkSelfPermission(
                    context, "com.google.android.gms.permission.ACTIVITY_RECOGNITION"
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED,
                showActivityPermissionDialog = !settingsManager.activityTrackingEnabled
                    || (!isActivityPermissionGranted() && !isActivityPermissionRequested())
            )
        }
    }

    // 解鎖動畫處理完畢的回呼（目前為空實作，預留擴充）
    fun onUnlockEventHandled() {
    }

    // 關閉活動權限對話框並標記已請求
    fun dismissActivityPermissionDialog() {
        markActivityPermissionRequested()
        _uiState.update { it.copy(showActivityPermissionDialog = false) }
    }

    // 授予活動權限並關閉對話框
    fun grantActivityPermission() {
        markActivityPermissionRequested()
        _uiState.update { it.copy(showActivityPermissionDialog = false) }
    }

    // 請求活動辨識權限（觸發系統權限對話框）
    fun requestActivityPermission() {
        _uiState.update { it.copy(showActivityPermissionDialog = false) }
    }

    private fun isActivityPermissionGranted(): Boolean {
        return androidx.core.content.ContextCompat.checkSelfPermission(
            context, "com.google.android.gms.permission.ACTIVITY_RECOGNITION"
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    private fun isActivityPermissionRequested(): Boolean {
        return context.getSharedPreferences("earth_online_settings", android.content.Context.MODE_PRIVATE)
            .getBoolean("activity_permission_requested", false)
    }

    private fun markActivityPermissionRequested() {
        context.getSharedPreferences("earth_online_settings", android.content.Context.MODE_PRIVATE)
            .edit().putBoolean("activity_permission_requested", true).apply()
    }

    // 重新載入儀表板資料（用於錯誤恢復）
    fun retryLoad() {
        viewModelScope.launch {
            loadAchievementDisplay()
        }
    }

    // 取得指定成就的證據照片路徑
    suspend fun getEvidencePhoto(achievementId: String): String? {
        return repository.getEvidence(achievementId)?.photoPath
    }

    // 取得指定成就的所有證據照片路徑
    suspend fun getAllEvidencePhotos(achievementId: String): List<String> {
        return repository.getAllEvidenceForAchievement(achievementId).map { it.photoPath }
    }

    // 取得所有簽到記錄
    suspend fun getAllCheckinRecords(): List<CheckInRecord> {
        return repository.getAllCheckinRecords()
    }

    // 匯出資料備份至指定 URI
    suspend fun exportBackup(uri: Uri) {
        backupManager.exportToUri(uri)
    }

    // 從指定 URI 匯入資料備份並重新載入
    suspend fun importBackup(uri: Uri) {
        backupManager.importFromUri(uri)
        repository.syncAutoTrackFromHistory()
        repository.refreshAll()
        loadAchievementDisplay()
    }
}
