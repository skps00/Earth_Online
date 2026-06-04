package com.earthonline.app.data.backup

import android.content.Context
import android.net.Uri
import android.util.Log
import com.earthonline.app.AppConstants
import com.earthonline.app.data.local.dao.AchievementDefinitionDao
import com.earthonline.app.data.local.dao.AchievementEvidenceDao
import com.earthonline.app.data.local.dao.CheckInRecordDao
import com.earthonline.app.data.local.dao.PetDao
import com.earthonline.app.data.local.dao.UserAchievementProgressDao
import com.earthonline.app.data.local.entity.AchievementEvidence
import com.earthonline.app.data.local.entity.CheckInRecord
import com.earthonline.app.data.local.entity.UserAchievementProgressEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "BackupManager"

sealed class BackupResult {
    data object Success : BackupResult()
    data class Error(val message: String) : BackupResult()
}

@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val progressDao: UserAchievementProgressDao,
    private val checkInRecordDao: CheckInRecordDao,
    private val evidenceDao: AchievementEvidenceDao,
    private val definitionDao: AchievementDefinitionDao,
    private val petDao: PetDao
) {
    companion object {
        private const val KEY_VERSION = "version"
        private const val KEY_EXPORT_DATE = "exportDate"
        private const val KEY_ACHIEVEMENT_PROGRESS = "achievementProgress"
        private const val KEY_ACHIEVEMENT_ID = "achievementId"
        private const val KEY_CURRENT_PROGRESS = "currentProgress"
        private const val KEY_IS_UNLOCKED = "isUnlocked"
        private const val KEY_UNLOCKED_DATE = "unlockedDate"
        private const val KEY_TRIGGER_TYPE = "triggerType"
        private const val KEY_CHECKIN_RECORDS = "checkinRecords"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
        private const val KEY_COUNTRY = "country"
        private const val KEY_CONTINENT = "continent"
        private const val KEY_ADDRESS = "address"
        private const val KEY_TIMESTAMP = "timestamp"
        private const val KEY_EVIDENCE = "evidence"
        private const val KEY_PHOTO_PATH = "photoPath"
        private const val KEY_DETECTED_LABELS = "detectedLabels"
        private const val KEY_PET = "pet"
        private const val KEY_NAME = "name"
        private const val KEY_EMOJI = "emoji"
        private const val KEY_LEVEL = "level"
        private const val KEY_XP = "xp"
        private const val KEY_STRENGTH = "strength"
        private const val KEY_AGILITY = "agility"
        private const val KEY_INTELLIGENCE = "intelligence"
        private const val KEY_CHARISMA = "charisma"
        private const val KEY_VITALITY = "vitality"
    }

    private val userId = AppConstants.LOCAL_USER_ID

    suspend fun exportToUri(uri: Uri): BackupResult {
        return try {
            val json = JSONObject().apply {
                put(KEY_VERSION, 1)
                put(KEY_EXPORT_DATE, System.currentTimeMillis())

                val progressArray = JSONArray()
                val allProgress = progressDao.getAllByUser(userId)
                allProgress.forEach { p ->
                    progressArray.put(JSONObject().apply {
                        put(KEY_ACHIEVEMENT_ID, p.achievementId)
                        put(KEY_CURRENT_PROGRESS, p.currentProgress)
                        put(KEY_IS_UNLOCKED, p.isUnlocked)
                        put(KEY_UNLOCKED_DATE, p.unlockedDate?.toString() ?: "")
                        put(KEY_TRIGGER_TYPE, p.triggerType)
                    })
                }
                put(KEY_ACHIEVEMENT_PROGRESS, progressArray)

                val checkinArray = JSONArray()
                val allCheckins = checkInRecordDao.getAllByUser(userId)
                allCheckins.forEach { c ->
                    checkinArray.put(JSONObject().apply {
                        put(KEY_LATITUDE, c.latitude)
                        put(KEY_LONGITUDE, c.longitude)
                        put(KEY_COUNTRY, c.country)
                        put(KEY_CONTINENT, c.continent)
                        put(KEY_ADDRESS, c.address)
                        put(KEY_TIMESTAMP, c.timestamp)
                    })
                }
                put(KEY_CHECKIN_RECORDS, checkinArray)

                val evidenceArray = JSONArray()
                val allEvidence = evidenceDao.getAllByUser(userId)
                allEvidence.forEach { e ->
                    evidenceArray.put(JSONObject().apply {
                        put(KEY_ACHIEVEMENT_ID, e.achievementId)
                        put(KEY_PHOTO_PATH, e.photoPath)
                        put(KEY_DETECTED_LABELS, e.detectedLabels)
                        put(KEY_TIMESTAMP, e.timestamp)
                    })
                }
                put(KEY_EVIDENCE, evidenceArray)

                val pet = petDao.get()
                if (pet != null) {
                    put(KEY_PET, JSONObject().apply {
                        put(KEY_NAME, pet.name)
                        put(KEY_EMOJI, pet.emoji)
                        put(KEY_LEVEL, pet.level)
                        put(KEY_XP, pet.xp)
                        put(KEY_STRENGTH, pet.strength)
                        put(KEY_AGILITY, pet.agility)
                        put(KEY_INTELLIGENCE, pet.intelligence)
                        put(KEY_CHARISMA, pet.charisma)
                        put(KEY_VITALITY, pet.vitality)
                    })
                }
            }

            context.contentResolver.openOutputStream(uri)?.use { out ->
                out.write(json.toString(2).toByteArray())
            } ?: return BackupResult.Error("Cannot open output stream")

            BackupResult.Success
        } catch (e: Exception) {
            Log.e(TAG, "Export failed", e)
            BackupResult.Error(e.message ?: "Export failed")
        }
    }

    suspend fun importFromUri(uri: Uri): BackupResult {
        return try {
            val jsonString = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                ?: return BackupResult.Error("Cannot read backup file")
            val json = JSONObject(jsonString)

            val progressArray = json.getJSONArray(KEY_ACHIEVEMENT_PROGRESS)
            for (i in 0 until progressArray.length()) {
                val obj = progressArray.getJSONObject(i)
                val progress = UserAchievementProgressEntity(
                    userId = userId,
                    achievementId = obj.getString(KEY_ACHIEVEMENT_ID),
                    currentProgress = obj.getLong(KEY_CURRENT_PROGRESS),
                    isUnlocked = obj.getBoolean(KEY_IS_UNLOCKED),
                    unlockedDate = obj.optString(KEY_UNLOCKED_DATE).toLongOrNull(),
                    triggerType = obj.getString(KEY_TRIGGER_TYPE)
                )
                progressDao.insertReplace(progress)
            }

            val checkinArray = json.optJSONArray(KEY_CHECKIN_RECORDS)
            if (checkinArray != null) {
                for (i in 0 until checkinArray.length()) {
                    val obj = checkinArray.getJSONObject(i)
                    checkInRecordDao.insertReplace(CheckInRecord(
                        userId = userId,
                        latitude = obj.getDouble(KEY_LATITUDE),
                        longitude = obj.getDouble(KEY_LONGITUDE),
                        country = obj.optString(KEY_COUNTRY, ""),
                        continent = obj.optString(KEY_CONTINENT, ""),
                        address = obj.optString(KEY_ADDRESS, ""),
                        timestamp = obj.getLong(KEY_TIMESTAMP)
                    ))
                }
            }

            val evidenceArray = json.optJSONArray(KEY_EVIDENCE)
            if (evidenceArray != null) {
                for (i in 0 until evidenceArray.length()) {
                    val obj = evidenceArray.getJSONObject(i)
                    evidenceDao.insert(AchievementEvidence(
                        achievementId = obj.getString(KEY_ACHIEVEMENT_ID),
                        userId = userId,
                        photoPath = obj.getString(KEY_PHOTO_PATH),
                        detectedLabels = obj.getString(KEY_DETECTED_LABELS),
                        timestamp = obj.getLong(KEY_TIMESTAMP)
                    ))
                }
            }

            val petObj = json.optJSONObject(KEY_PET)
            if (petObj != null) {
                petDao.save(com.earthonline.app.data.local.entity.PetEntity(
                    name = petObj.optString(KEY_NAME, AppConstants.DEFAULT_PET_NAME),
                    emoji = petObj.optString(KEY_EMOJI, AppConstants.DEFAULT_PET_EMOJI),
                    level = petObj.optInt(KEY_LEVEL, AppConstants.DEFAULT_PET_LEVEL),
                    xp = petObj.optLong(KEY_XP, AppConstants.DEFAULT_PET_XP),
                    strength = petObj.optInt(KEY_STRENGTH, 0),
                    agility = petObj.optInt(KEY_AGILITY, 0),
                    intelligence = petObj.optInt(KEY_INTELLIGENCE, 0),
                    charisma = petObj.optInt(KEY_CHARISMA, 0),
                    vitality = petObj.optInt(KEY_VITALITY, 0)
                ))
            }

            BackupResult.Success
        } catch (e: Exception) {
            Log.e(TAG, "Import failed", e)
            BackupResult.Error(e.message ?: "Import failed")
        }
    }
}
