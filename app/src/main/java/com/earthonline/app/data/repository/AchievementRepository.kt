package com.earthonline.app.data.repository

// 成就倉儲 — 管理成就解鎖、打卡記錄、寵物屬性計算的核心資料層

import android.util.Log
import com.earthonline.app.AppConstants
import com.earthonline.app.data.activity.ActivityRecognitionManager
import com.earthonline.app.data.screentime.ScreenTimeManager
import com.earthonline.app.data.local.AchievementSeedData
import com.earthonline.app.data.local.dao.AchievementDefinitionDao
import com.earthonline.app.data.local.dao.AchievementEvidenceDao
import com.earthonline.app.data.local.dao.CheckInRecordDao
import com.earthonline.app.data.local.dao.PetDao
import com.earthonline.app.data.local.dao.UserAchievementProgressDao
import com.earthonline.app.data.local.entity.AchievementDefinitionEntity
import com.earthonline.app.data.local.entity.AchievementEvidence
import com.earthonline.app.data.local.entity.CheckInRecord
import com.earthonline.app.data.local.entity.PetEntity
import com.earthonline.app.data.local.entity.UserAchievementProgressEntity
import com.earthonline.app.domain.model.AchievementTriggers
import com.earthonline.app.domain.model.TriggerType
import com.earthonline.app.domain.service.PetStatContributions
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import kotlin.math.roundToInt
import javax.inject.Singleton

// 成就解鎖事件 — 包含成就定義與解鎖時間戳記
private const val TAG = "AchievementRepository"

data class UnlockedAchievementEvent(
    val achievement: AchievementDefinitionEntity,
    val unlockedDate: Long
)

// 成就倉儲實作 — 注入所有底層 DAO 與活動識別管理器
@Singleton
class AchievementRepository @Inject constructor(
    private val definitionDao: AchievementDefinitionDao,
    private val progressDao: UserAchievementProgressDao,
    private val checkInRecordDao: CheckInRecordDao,
    private val evidenceDao: AchievementEvidenceDao,
    private val petDao: PetDao,
    private val activityRecognitionManager: ActivityRecognitionManager,
    private val screenTimeManager: ScreenTimeManager
) {
    // 成就解鎖事件流 — 供 ViewModel 訂閱顯示解鎖動畫
    private val _unlockEvents = MutableSharedFlow<UnlockedAchievementEvent>(replay = 0)
    val unlockEvents: SharedFlow<UnlockedAchievementEvent> = _unlockEvents

    // 總打卡數事件流 — replay=1 確保新訂閱者能取得最新值
    private val _totalCheckins = MutableSharedFlow<Long>(replay = 1)
    val totalCheckins: SharedFlow<Long> = _totalCheckins

    // 初始化種子成就定義與進度 — 僅在首次啟動時呼叫
    suspend fun initializeAchievements() {
        val definitions = AchievementSeedData.create()
        definitionDao.insertAll(definitions)
        progressDao.insertAll(AchievementSeedData.createProgress(definitions, AppConstants.LOCAL_USER_ID))
    }

    // 記錄打卡並檢查成就解鎖 — 回傳本次打卡觸發的所有解鎖事件
    suspend fun recordCheckin(latitude: Double, longitude: Double, country: String, continent: String = "", address: String = "", altitude: Double? = null): List<UnlockedAchievementEvent> {
        val userId = AppConstants.LOCAL_USER_ID
        val triggerType = TriggerType.LOCATION_CHECKIN_COUNT.value

        val uniqueCount = checkInRecordDao.insertCheckinAndCount(
            CheckInRecord(userId = userId, latitude = latitude, longitude = longitude, country = country, continent = continent, address = address, timestamp = System.currentTimeMillis()),
            userId
        )

        progressDao.setProgressByType(userId, triggerType, uniqueCount)
        _totalCheckins.emit(uniqueCount)

        val events = mutableListOf<UnlockedAchievementEvent>()
        // 先檢查打卡計數型成就，再檢查自動追蹤型成就
        events.addAll(checkAndUnlock(userId, triggerType))
        events.addAll(evaluateAutoTrackAchievements(country, continent))
        // 檢查高山成就：海拔 ≥ 2500m → 解鎖 explore_mountain
        if (altitude != null && altitude in AppConstants.MOUNTAIN_ALTITUDE_MIN..AppConstants.MOUNTAIN_ALTITUDE_MAX) {
            tryAutoUnlock(userId, "explore_mountain", System.currentTimeMillis())?.let { events.add(it) }
        }
        return events
    }

    // 評估自動追蹤成就 — 依國家和洲別檢查探索成就
    private suspend fun evaluateAutoTrackAchievements(country: String, continent: String): List<UnlockedAchievementEvent> {
        val userId = AppConstants.LOCAL_USER_ID
        val events = mutableListOf<UnlockedAchievementEvent>()

        if (country.isNotBlank()) {
            val countryCount = checkInRecordDao.countUniqueCountries(userId).toLong()
            events.addAll(autoTrackExploreCountry(countryCount))
            events.addAll(autoTrackSpecificCountry(country))
        }

        if (continent.isNotBlank()) {
            autoTrackSpecificContinent(continent).let { events.addAll(it) }
        }

        return events
    }

    // 自動追蹤國家數量及洲數量成就 — 檢查 explore_5countries 等進度型成就
    private suspend fun autoTrackExploreCountry(countryCount: Long): List<UnlockedAchievementEvent> {
        val exploreIds = AchievementTriggers.countryCountAchievements
        val events = mutableListOf<UnlockedAchievementEvent>()

        for (id in exploreIds) {
            progressDao.setProgressById(AppConstants.LOCAL_USER_ID, id, countryCount)
            val updated = progressDao.getByUserAndAchievement(AppConstants.LOCAL_USER_ID, id) ?: continue
            val def = definitionDao.getById(id) ?: continue
            if (!updated.isUnlocked && updated.currentProgress >= def.triggerGoal) {
                progressDao.unlockAchievement(AppConstants.LOCAL_USER_ID, id, System.currentTimeMillis())
                val event = UnlockedAchievementEvent(def, System.currentTimeMillis())
                _unlockEvents.emit(event)
                events.add(event)
            }
        }

        val continentCount = checkInRecordDao.countUniqueContinents(AppConstants.LOCAL_USER_ID)
        val continentIds = AchievementTriggers.continentCountAchievements
        for (id in continentIds) {
            progressDao.setProgressById(AppConstants.LOCAL_USER_ID, id, continentCount.toLong())
            val updated = progressDao.getByUserAndAchievement(AppConstants.LOCAL_USER_ID, id) ?: continue
            val def = definitionDao.getById(id) ?: continue
            if (!updated.isUnlocked && updated.currentProgress >= def.triggerGoal) {
                progressDao.unlockAchievement(AppConstants.LOCAL_USER_ID, id, System.currentTimeMillis())
                val event = UnlockedAchievementEvent(def, System.currentTimeMillis())
                _unlockEvents.emit(event)
                events.add(event)
            }
        }

        return events
    }

    // 自動解鎖特定國家成就 — 如到達日本即解鎖 explore_japan
    private suspend fun autoTrackSpecificCountry(country: String): List<UnlockedAchievementEvent> {
        val achievementId = AchievementTriggers.countryTriggers[country] ?: return emptyList()
        val userId = AppConstants.LOCAL_USER_ID
        val progress = progressDao.getByUserAndAchievement(userId, achievementId) ?: return emptyList()
        if (progress.isUnlocked) return emptyList()

        progressDao.unlockAchievement(userId, achievementId, System.currentTimeMillis())
        val def = definitionDao.getById(achievementId) ?: return emptyList()
        val event = UnlockedAchievementEvent(def, System.currentTimeMillis())
        _unlockEvents.emit(event)
        return listOf(event)
    }

    // 自動解鎖特定洲成就 — 如到達亞洲即解鎖 explore_asia
    private suspend fun autoTrackSpecificContinent(continent: String): List<UnlockedAchievementEvent> {
        val achievementId = AchievementTriggers.continentTriggers[continent] ?: return emptyList()
        val userId = AppConstants.LOCAL_USER_ID
        val progress = progressDao.getByUserAndAchievement(userId, achievementId) ?: return emptyList()
        if (progress.isUnlocked) return emptyList()

        progressDao.unlockAchievement(userId, achievementId, System.currentTimeMillis())
        val def = definitionDao.getById(achievementId) ?: return emptyList()
        val event = UnlockedAchievementEvent(def, System.currentTimeMillis())
        _unlockEvents.emit(event)
        return listOf(event)
    }

    // 手動確認成就 — 遞增進度，可附照片與 AI 標籤作為證據
    suspend fun confirmManualAchievement(
        achievementId: String,
        photoPath: String? = null,
        labels: List<String>? = null
    ): UnlockedAchievementEvent? {
        val userId = AppConstants.LOCAL_USER_ID

        progressDao.incrementProgressById(userId, achievementId)

        if (photoPath != null) {
            evidenceDao.insert(
                AchievementEvidence(
                    achievementId = achievementId,
                    userId = userId,
                    photoPath = photoPath,
                    detectedLabels = labels?.joinToString(", ") ?: "",
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        val progress = progressDao.getByUserAndAchievement(userId, achievementId)
        val definition = definitionDao.getById(achievementId)

        if (progress != null && definition != null && progress.currentProgress >= definition.triggerGoal) {
            val now = System.currentTimeMillis()
            progressDao.unlockAchievement(userId, achievementId, now)
            val event = UnlockedAchievementEvent(
                achievement = definition,
                unlockedDate = now
            )
            _unlockEvents.emit(event)
            return event
        }

        return null
    }

    // 取得指定成就的最新證據記錄
    suspend fun getEvidence(achievementId: String): AchievementEvidence? {
        return evidenceDao.getLatestByAchievement(achievementId, AppConstants.LOCAL_USER_ID)
    }

    // 取得指定成就的所有證據記錄（支援多次進度拍照）
    suspend fun getAllEvidenceForAchievement(achievementId: String): List<AchievementEvidence> {
        return evidenceDao.getByAchievement(achievementId, AppConstants.LOCAL_USER_ID)
    }

    // 取得當前使用者的所有成就進度
    suspend fun getAllAchievementProgress(): List<UserAchievementProgressEntity> {
        return progressDao.getAllByUser(AppConstants.LOCAL_USER_ID)
    }

    // 取得所有成就定義
    suspend fun getAllDefinitions(): List<AchievementDefinitionEntity> {
        return definitionDao.getAll()
    }

    // 計算所有已解鎖成就的總獎勵分數
    suspend fun getTotalPoints(): Long {
        val allProgress = progressDao.getAllByUser(AppConstants.LOCAL_USER_ID)
        val definitions = definitionDao.getAll().associateBy { it.achievementId }
        return allProgress.filter { it.isUnlocked }.sumOf { progress ->
            definitions[progress.achievementId]?.rewardPoints?.toLong() ?: 0L
        }
    }

    // 取得已解鎖成就的總數量
    suspend fun getUnlockedCount(): Int {
        return progressDao.getAllByUser(AppConstants.LOCAL_USER_ID).count { it.isUnlocked }
    }

    // 取得使用者打卡總次數
    suspend fun getCheckinCount(): Int {
        return checkInRecordDao.countByUser(AppConstants.LOCAL_USER_ID)
    }

    // 取得使用者的所有打卡記錄
    suspend fun getAllCheckinRecords(): List<CheckInRecord> {
        return checkInRecordDao.getAllByUser(AppConstants.LOCAL_USER_ID)
    }

    // 取得不重複打卡地點數量（經緯度去重）
    suspend fun getUniqueLocationCount(): Int {
        return checkInRecordDao.countUniqueLocations(AppConstants.LOCAL_USER_ID)
    }

    // 檢查並解鎖達到目標值的成就 — 遍歷未鎖定成就，進度達標則解鎖
    private suspend fun checkAndUnlock(
        userId: String,
        triggerType: String
    ): List<UnlockedAchievementEvent> {
        val lockedProgress = progressDao.getLockedByUserAndType(userId, triggerType)
        val events = mutableListOf<UnlockedAchievementEvent>()

        for (progress in lockedProgress) {
            val definition = definitionDao.getById(progress.achievementId) ?: continue
            if (progress.currentProgress >= definition.triggerGoal) {
                val now = System.currentTimeMillis()
                progressDao.unlockAchievement(userId, progress.achievementId, now)

                val event = UnlockedAchievementEvent(
                    achievement = definition,
                    unlockedDate = now
                )
                events.add(event)
                _unlockEvents.emit(event)
            }
        }

        return events
    }

    // 重新發送總打卡數事件 — 供外部強制刷新 UI
    suspend fun refreshTotalCheckins() {
        val count = checkInRecordDao.countUniqueLocations(AppConstants.LOCAL_USER_ID).toLong()
        _totalCheckins.emit(count)
    }

    // 刷新所有資料 — 供導入備份後重整狀態
    suspend fun refreshAll() {
        refreshTotalCheckins()
    }

    // 等級縮放係數 — 用於平方根等級公式，值越大升級越慢
    private val LEVEL_SCALE = 100.0

    // 根據總分計算玩家等級 — 使用平方根公式 sqrt(points / 100) + 1
    fun computePlayerLevel(totalPoints: Long): Int {
        return (kotlin.math.sqrt(totalPoints.toDouble() / LEVEL_SCALE) + 1).roundToInt()
    }

    // 計算升到下一級所需經驗值
    fun computeXpToNext(totalPoints: Long): Long {
        val currentLevel = computePlayerLevel(totalPoints)
        val nextLevelXp = (currentLevel.toLong() * currentLevel.toLong()) * LEVEL_SCALE.toLong()
        return (nextLevelXp - totalPoints).coerceAtLeast(0)
    }

    // 計算當前等級進度百分比 — 回傳 0f~1f 供進度條使用
    fun computeLevelProgress(totalPoints: Long): Float {
        val currentLevel = computePlayerLevel(totalPoints)
        val currentLevelXp = ((currentLevel - 1).toLong() * (currentLevel - 1).toLong()) * LEVEL_SCALE.toLong()
        val nextLevelXp = (currentLevel.toLong() * currentLevel.toLong()) * LEVEL_SCALE.toLong()
        val totalNeeded = nextLevelXp - currentLevelXp
        if (totalNeeded <= 0) return 1f
        val earned = totalPoints - currentLevelXp
        return (earned.toFloat() / totalNeeded.toFloat()).coerceIn(0f, 1f)
    }

    // 從歷史記錄同步自動追蹤成就 — 用於備份匯入後重新評估
    suspend fun syncAutoTrackFromHistory() {
        try {
            val userId = AppConstants.LOCAL_USER_ID
            val countryCount = checkInRecordDao.countUniqueCountries(userId).toLong()
            val continentCount = checkInRecordDao.countUniqueContinents(userId).toLong()

            val countryIds = listOf("explore_5countries", "explore_10countries", "explore_50countries")
            val continentIds = AchievementTriggers.continentCountAchievements

            for (id in countryIds) {
                val def = definitionDao.getById(id) ?: continue
                progressDao.setProgressById(userId, id, countryCount)
                val updated = progressDao.getByUserAndAchievement(userId, id) ?: continue
                if (!updated.isUnlocked && updated.currentProgress >= def.triggerGoal) {
                    progressDao.unlockAchievement(userId, id, System.currentTimeMillis())
                    _unlockEvents.emit(UnlockedAchievementEvent(def, System.currentTimeMillis()))
                }
            }

            for (id in continentIds) {
                val def = definitionDao.getById(id) ?: continue
                progressDao.setProgressById(userId, id, continentCount)
                val updated = progressDao.getByUserAndAchievement(userId, id) ?: continue
                if (!updated.isUnlocked && updated.currentProgress >= def.triggerGoal) {
                    progressDao.unlockAchievement(userId, id, System.currentTimeMillis())
                    _unlockEvents.emit(UnlockedAchievementEvent(def, System.currentTimeMillis()))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync auto-track achievements from history", e)
        }
    }

    // 評估活動成就 — 回傳活動數據 (走路/騎行/騎行公里) 與解鎖事件
    suspend fun evaluateActivityAchievements(): Pair<Triple<Int, Int, Int>, List<UnlockedAchievementEvent>> {
        val walkMin = activityRecognitionManager.getWalkingMinutes()
        val bikeMin = activityRecognitionManager.getBikingMinutes()
        val bikeKm = activityRecognitionManager.getBikingKm()
        val userId = AppConstants.LOCAL_USER_ID
        val now = System.currentTimeMillis()
        val events = mutableListOf<UnlockedAchievementEvent>()

        if (bikeMin > 0) tryAutoUnlock(userId, "transport_bike", now)?.let { events.add(it) }
        if (bikeKm >= 100) tryAutoUnlock(userId, "transport_bike_100", now)?.let { events.add(it) }

        return Triple(walkMin, bikeMin, bikeKm) to events
    }

    suspend fun evaluateScreenTimeAchievements(): List<UnlockedAchievementEvent> {
        val achievementIds = screenTimeManager.evaluateAchievements()
        if (achievementIds.isEmpty()) return emptyList()

        val userId = AppConstants.LOCAL_USER_ID
        val now = System.currentTimeMillis()
        val events = mutableListOf<UnlockedAchievementEvent>()

        for (id in achievementIds) {
            tryAutoUnlock(userId, id, now)?.let { events.add(it) }
        }
        return events
    }

    // 嘗試自動解鎖單一成就 — 若未解鎖則設為滿進度並解鎖
    private suspend fun tryAutoUnlock(userId: String, achievementId: String, now: Long): UnlockedAchievementEvent? {
        val def = definitionDao.getById(achievementId) ?: return null
        val progress = progressDao.getByUserAndAchievement(userId, achievementId) ?: return null
        if (!progress.isUnlocked) {
            progressDao.setProgressById(userId, achievementId, def.triggerGoal)
            progressDao.unlockAchievement(userId, achievementId, now)
            return UnlockedAchievementEvent(def, now)
        }
        return null
    }

    suspend fun unlockByIds(achievementIds: List<String>) {
        val userId = AppConstants.LOCAL_USER_ID
        val now = System.currentTimeMillis()
        for (id in achievementIds) {
            tryAutoUnlock(userId, id, now)
        }
    }

    // 取得寵物實體 — 尚無則新建預設寵物
    suspend fun getPet(): PetEntity {
        val existing = petDao.get()
        return existing ?: PetEntity().also { petDao.save(it) }
    }

    // 重新命名寵物
    suspend fun renamePet(name: String) {
        val pet = getPet().copy(name = name)
        petDao.save(pet)
    }

    // 更換寵物表情符號
    suspend fun changePetEmoji(emoji: String) {
        val pet = getPet().copy(emoji = emoji)
        petDao.save(pet)
    }

    // 根據已解鎖成就計算並保存寵物五維屬性（力量/敏捷/智力/魅力/體力）
    suspend fun computeAndSavePetStats() {
        val pet = getPet()
        val allProgress = progressDao.getAllByUser(AppConstants.LOCAL_USER_ID)
            .filter { it.isUnlocked }
        val definitions = definitionDao.getAll().associateBy { it.achievementId }

        var strengthRaw = 0f
        var agilityRaw = 0f
        var intelligenceRaw = 0f
        var charismaRaw = 0f
        var vitalityRaw = 0f

        for (prog in allProgress) {
            val def = definitions[prog.achievementId] ?: continue
            val points = def.rewardPoints.toFloat()
            // 若成就定義有自訂屬性權重則優先使用，否則使用分類預設權重
            val hasCustomWeights = def.strengthWeight + def.agilityWeight + def.intelligenceWeight + def.charismaWeight + def.vitalityWeight > 0f
            if (hasCustomWeights) {
                strengthRaw += points * def.strengthWeight
                agilityRaw += points * def.agilityWeight
                intelligenceRaw += points * def.intelligenceWeight
                charismaRaw += points * def.charismaWeight
                vitalityRaw += points * def.vitalityWeight
            } else {
                val w = PetStatContributions.getWeights(def.achievementId)
                strengthRaw += points * w.strength
                agilityRaw += points * w.agility
                intelligenceRaw += points * w.intelligence
                charismaRaw += points * w.charisma
                vitalityRaw += points * w.vitality
            }
        }

        val divisor = AppConstants.STAT_DIVISOR
        val totalPoints = getTotalPoints()
        val level = computePlayerLevel(totalPoints)

        petDao.save(
            pet.copy(
                level = level,
                xp = totalPoints,
                strength = (strengthRaw / divisor).roundToInt(),
                agility = (agilityRaw / divisor).roundToInt(),
                intelligence = (intelligenceRaw / divisor).roundToInt(),
                charisma = (charismaRaw / divisor).roundToInt(),
                vitality = (vitalityRaw / divisor).roundToInt()
            )
        )
    }

    // 將 PetEntity 轉換為 UI 層 PetUiState
    fun petToUiState(pet: PetEntity): com.earthonline.app.ui.screens.dashboard.PetUiState {
        return com.earthonline.app.ui.screens.dashboard.PetUiState(
            name = pet.name,
            emoji = pet.emoji,
            level = pet.level,
            strength = pet.strength,
            agility = pet.agility,
            intelligence = pet.intelligence,
            charisma = pet.charisma,
            vitality = pet.vitality
        )
    }
}
