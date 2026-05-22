package com.earthonline.app.data.repository

import com.earthonline.app.data.local.AchievementSeedData
import com.earthonline.app.data.local.dao.AchievementDefinitionDao
import com.earthonline.app.data.local.dao.AchievementEvidenceDao
import com.earthonline.app.data.local.dao.CheckInRecordDao
import com.earthonline.app.data.local.dao.UserAchievementProgressDao
import com.earthonline.app.data.local.entity.AchievementDefinitionEntity
import com.earthonline.app.data.local.entity.AchievementEvidence
import com.earthonline.app.data.local.entity.CheckInRecord
import com.earthonline.app.data.local.entity.UserAchievementProgressEntity
import com.earthonline.app.domain.model.AchievementTriggers
import com.earthonline.app.domain.model.TriggerType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
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
    private val evidenceDao: AchievementEvidenceDao
) {
    private val _unlockEvents = MutableSharedFlow<UnlockedAchievementEvent>(replay = 0)
    val unlockEvents: SharedFlow<UnlockedAchievementEvent> = _unlockEvents

    private val _totalCheckins = MutableSharedFlow<Long>(replay = 1)
    val totalCheckins: SharedFlow<Long> = _totalCheckins

    suspend fun initializeAchievements() {
        val definitions = AchievementSeedData.create()
        definitionDao.insertAll(definitions)
        progressDao.insertAll(AchievementSeedData.createProgress(definitions, "local_user"))
    }

    suspend fun recordCheckin(latitude: Double, longitude: Double, country: String, continent: String = "", address: String = ""): List<UnlockedAchievementEvent> {
        val userId = "local_user"
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
        val userId = "local_user"
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
            progressDao.setProgressById("local_user", id, countryCount)
            val updated = progressDao.getByUserAndAchievement("local_user", id) ?: continue
            val def = definitionDao.getById(id) ?: continue
            if (!updated.isUnlocked && updated.currentProgress >= def.triggerGoal) {
                progressDao.unlockAchievement("local_user", id, System.currentTimeMillis())
                val event = UnlockedAchievementEvent(def, System.currentTimeMillis())
                _unlockEvents.emit(event)
                events.add(event)
            }
        }

        val continentCount = checkInRecordDao.countUniqueContinents("local_user")
        val continentIds = AchievementTriggers.continentCountAchievements
        for (id in continentIds) {
            progressDao.setProgressById("local_user", id, continentCount.toLong())
            val updated = progressDao.getByUserAndAchievement("local_user", id) ?: continue
            val def = definitionDao.getById(id) ?: continue
            if (!updated.isUnlocked && updated.currentProgress >= def.triggerGoal) {
                progressDao.unlockAchievement("local_user", id, System.currentTimeMillis())
                val event = UnlockedAchievementEvent(def, System.currentTimeMillis())
                _unlockEvents.emit(event)
                events.add(event)
            }
        }

        return events
    }

    private suspend fun autoTrackSpecificCountry(country: String): List<UnlockedAchievementEvent> {
        val achievementId = AchievementTriggers.countryTriggers[country] ?: return emptyList()
        val userId = "local_user"
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
        val userId = "local_user"
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
        val userId = "local_user"

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
        return evidenceDao.getByAchievement(achievementId, "local_user")
    }

    suspend fun getAllAchievementProgress(): List<UserAchievementProgressEntity> {
        return progressDao.getAllByUser("local_user")
    }

    suspend fun getAllDefinitions(): List<AchievementDefinitionEntity> {
        return definitionDao.getAll()
    }

    suspend fun getCheckinCount(): Int {
        return checkInRecordDao.countByUser("local_user")
    }

    suspend fun getAllCheckinRecords(): List<CheckInRecord> {
        return checkInRecordDao.getAllByUser("local_user")
    }

    suspend fun getUniqueLocationCount(): Int {
        return checkInRecordDao.countUniqueLocations("local_user")
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
        val count = checkInRecordDao.countUniqueLocations("local_user").toLong()
        _totalCheckins.emit(count)
    }

    suspend fun refreshAll() {
        refreshTotalCheckins()
    }

    suspend fun syncAutoTrackFromHistory() {
        try {
            val userId = "local_user"
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
}
