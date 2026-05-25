package com.earthonline.app.data.repository

import com.earthonline.app.AppConstants
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

data class UnlockedAchievementEvent(
    val achievement: AchievementDefinitionEntity,
    val unlockedDate: Long
)

@Singleton
class AchievementRepository @Inject constructor(
    private val definitionDao: AchievementDefinitionDao,
    private val progressDao: UserAchievementProgressDao,
    private val checkInRecordDao: CheckInRecordDao,
    private val evidenceDao: AchievementEvidenceDao,
    private val petDao: PetDao
) {
    private val _unlockEvents = MutableSharedFlow<UnlockedAchievementEvent>(replay = 0)
    val unlockEvents: SharedFlow<UnlockedAchievementEvent> = _unlockEvents

    private val _totalCheckins = MutableSharedFlow<Long>(replay = 1)
    val totalCheckins: SharedFlow<Long> = _totalCheckins

    suspend fun initializeAchievements() {
        val definitions = AchievementSeedData.create()
        definitionDao.insertAll(definitions)
        progressDao.insertAll(AchievementSeedData.createProgress(definitions, AppConstants.LOCAL_USER_ID))
    }

    suspend fun recordCheckin(latitude: Double, longitude: Double, country: String, continent: String = "", address: String = ""): List<UnlockedAchievementEvent> {
        val userId = AppConstants.LOCAL_USER_ID
        val triggerType = TriggerType.LOCATION_CHECKIN_COUNT.value

        checkInRecordDao.insert(
            CheckInRecord(userId = userId, latitude = latitude, longitude = longitude, country = country, continent = continent, address = address, timestamp = System.currentTimeMillis())
        )

        val uniqueCount = checkInRecordDao.countUniqueLocations(userId).toLong()
        progressDao.setProgressByType(userId, triggerType, uniqueCount)
        _totalCheckins.emit(uniqueCount)

        val events = mutableListOf<UnlockedAchievementEvent>()
        events.addAll(checkAndUnlock(userId, triggerType))
        events.addAll(evaluateAutoTrackAchievements(country, continent))
        return events
    }

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

    suspend fun getEvidence(achievementId: String): AchievementEvidence? {
        return evidenceDao.getByAchievement(achievementId, AppConstants.LOCAL_USER_ID)
    }

    suspend fun getAllAchievementProgress(): List<UserAchievementProgressEntity> {
        return progressDao.getAllByUser(AppConstants.LOCAL_USER_ID)
    }

    suspend fun getAllDefinitions(): List<AchievementDefinitionEntity> {
        return definitionDao.getAll()
    }

    suspend fun getTotalPoints(): Long {
        val allProgress = progressDao.getAllByUser(AppConstants.LOCAL_USER_ID)
        return allProgress.filter { it.isUnlocked }.sumOf { progress ->
            definitionDao.getById(progress.achievementId)?.rewardPoints?.toLong() ?: 0L
        }
    }

    suspend fun getUnlockedCount(): Int {
        return progressDao.getAllByUser(AppConstants.LOCAL_USER_ID).count { it.isUnlocked }
    }

    suspend fun getCheckinCount(): Int {
        return checkInRecordDao.countByUser(AppConstants.LOCAL_USER_ID)
    }

    suspend fun getAllCheckinRecords(): List<CheckInRecord> {
        return checkInRecordDao.getAllByUser(AppConstants.LOCAL_USER_ID)
    }

    suspend fun getUniqueLocationCount(): Int {
        return checkInRecordDao.countUniqueLocations(AppConstants.LOCAL_USER_ID)
    }

    private suspend fun checkAndUnlock(
        userId: String,
        triggerType: String
    ): List<UnlockedAchievementEvent> {
        val unlockedProgress = progressDao.getUnlockedByUserAndType(userId, triggerType)
        val events = mutableListOf<UnlockedAchievementEvent>()

        for (progress in unlockedProgress) {
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

    suspend fun refreshTotalCheckins() {
        val count = checkInRecordDao.countUniqueLocations(AppConstants.LOCAL_USER_ID).toLong()
        _totalCheckins.emit(count)
    }

    suspend fun refreshAll() {
        refreshTotalCheckins()
    }

    private val LEVEL_SCALE = 100.0

    fun computePlayerLevel(totalPoints: Long): Int {
        return (kotlin.math.sqrt(totalPoints.toDouble() / LEVEL_SCALE) + 1).toInt()
    }

    fun computeXpToNext(totalPoints: Long): Long {
        val currentLevel = computePlayerLevel(totalPoints)
        val nextLevelXp = (currentLevel.toLong() * currentLevel.toLong()) * LEVEL_SCALE.toLong()
        return (nextLevelXp - totalPoints).coerceAtLeast(0)
    }

    fun computeLevelProgress(totalPoints: Long): Float {
        val currentLevel = computePlayerLevel(totalPoints)
        val currentLevelXp = ((currentLevel - 1).toLong() * (currentLevel - 1).toLong()) * LEVEL_SCALE.toLong()
        val nextLevelXp = (currentLevel.toLong() * currentLevel.toLong()) * LEVEL_SCALE.toLong()
        val totalNeeded = nextLevelXp - currentLevelXp
        if (totalNeeded <= 0) return 1f
        val earned = totalPoints - currentLevelXp
        return (earned.toFloat() / totalNeeded.toFloat()).coerceIn(0f, 1f)
    }

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
        } catch (_: Exception) { }
    }

    suspend fun getPet(): PetEntity {
        val existing = petDao.get()
        return existing ?: PetEntity().also { petDao.save(it) }
    }

    suspend fun renamePet(name: String) {
        val pet = getPet().copy(name = name)
        petDao.save(pet)
    }

    suspend fun changePetEmoji(emoji: String) {
        val pet = getPet().copy(emoji = emoji)
        petDao.save(pet)
    }

    suspend fun computeAndSavePetStats() {
        val pet = getPet()
        val allProgress = progressDao.getAllByUser(AppConstants.LOCAL_USER_ID)
            .filter { it.isUnlocked }

        var strengthRaw = 0f
        var agilityRaw = 0f
        var intelligenceRaw = 0f
        var charismaRaw = 0f
        var vitalityRaw = 0f

        for (prog in allProgress) {
            val def = definitionDao.getById(prog.achievementId) ?: continue
            val points = def.rewardPoints.toFloat()
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

        val divisor = 10f
        val totalPoints = getTotalPoints()
        val level = computePlayerLevel(totalPoints)

        petDao.save(
            pet.copy(
                level = level,
                xp = totalPoints,
                strength = roundToInt(strengthRaw / divisor),
                agility = roundToInt(agilityRaw / divisor),
                intelligence = roundToInt(intelligenceRaw / divisor),
                charisma = roundToInt(charismaRaw / divisor),
                vitality = roundToInt(vitalityRaw / divisor)
            )
        )
    }

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
