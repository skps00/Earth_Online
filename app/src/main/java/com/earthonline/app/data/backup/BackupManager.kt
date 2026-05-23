package com.earthonline.app.data.backup

import android.content.Context
import android.net.Uri
import com.earthonline.app.AppConstants
import com.earthonline.app.data.local.dao.AchievementDefinitionDao
import com.earthonline.app.data.local.dao.AchievementEvidenceDao
import com.earthonline.app.data.local.dao.CheckInRecordDao
import com.earthonline.app.data.local.dao.UserAchievementProgressDao
import com.earthonline.app.data.local.entity.AchievementEvidence
import com.earthonline.app.data.local.entity.CheckInRecord
import com.earthonline.app.data.local.entity.UserAchievementProgressEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val progressDao: UserAchievementProgressDao,
    private val checkInRecordDao: CheckInRecordDao,
    private val evidenceDao: AchievementEvidenceDao,
    private val definitionDao: AchievementDefinitionDao
) {
    private val userId = AppConstants.LOCAL_USER_ID

    suspend fun exportToUri(uri: Uri) {
        val json = JSONObject().apply {
            put("version", 1)
            put("exportDate", System.currentTimeMillis())

            val progressArray = JSONArray()
            val allProgress = progressDao.getAllByUser(userId)
            allProgress.forEach { p ->
                progressArray.put(JSONObject().apply {
                    put("achievementId", p.achievementId)
                    put("currentProgress", p.currentProgress)
                    put("isUnlocked", p.isUnlocked)
                    put("unlockedDate", p.unlockedDate?.toString() ?: "")
                    put("triggerType", p.triggerType)
                })
            }
            put("achievementProgress", progressArray)

            val checkinArray = JSONArray()
            val allCheckins = checkInRecordDao.getAllByUser(userId)
            allCheckins.forEach { c ->
                checkinArray.put(JSONObject().apply {
                    put("latitude", c.latitude)
                    put("longitude", c.longitude)
                    put("country", c.country)
                    put("continent", c.continent)
                    put("address", c.address)
                    put("timestamp", c.timestamp)
                })
            }
            put("checkinRecords", checkinArray)

            val evidenceArray = JSONArray()
            val allEvidence = progressDao.getAllByUser(userId).mapNotNull {
                evidenceDao.getByAchievement(it.achievementId, userId)
            }
            allEvidence.forEach { e ->
                evidenceArray.put(JSONObject().apply {
                    put("achievementId", e.achievementId)
                    put("photoPath", e.photoPath)
                    put("detectedLabels", e.detectedLabels)
                    put("timestamp", e.timestamp)
                })
            }
            put("evidence", evidenceArray)
        }

        context.contentResolver.openOutputStream(uri)?.use { out ->
            out.write(json.toString(2).toByteArray())
        }
    }

    suspend fun importFromUri(uri: Uri) {
        val jsonString = context.contentResolver.openInputStream(uri)?.bufferedReader()?.readText() ?: return
        val json = JSONObject(jsonString)

        val progressArray = json.getJSONArray("achievementProgress")
        for (i in 0 until progressArray.length()) {
            val obj = progressArray.getJSONObject(i)
            val progress = UserAchievementProgressEntity(
                userId = userId,
                achievementId = obj.getString("achievementId"),
                currentProgress = obj.getLong("currentProgress"),
                isUnlocked = obj.getBoolean("isUnlocked"),
                unlockedDate = obj.optString("unlockedDate").toLongOrNull(),
                triggerType = obj.getString("triggerType")
            )
            progressDao.insertReplace(progress)
        }

        val checkinArray = json.optJSONArray("checkinRecords")
        if (checkinArray != null) {
            for (i in 0 until checkinArray.length()) {
                val obj = checkinArray.getJSONObject(i)
                checkInRecordDao.insertReplace(CheckInRecord(
                    userId = userId,
                    latitude = obj.getDouble("latitude"),
                    longitude = obj.getDouble("longitude"),
                    country = obj.optString("country", ""),
                    continent = obj.optString("continent", ""),
                    address = obj.optString("address", ""),
                    timestamp = obj.getLong("timestamp")
                ))
            }
        }

        val evidenceArray = json.optJSONArray("evidence")
        if (evidenceArray != null) {
            for (i in 0 until evidenceArray.length()) {
                val obj = evidenceArray.getJSONObject(i)
                evidenceDao.insert(AchievementEvidence(
                    achievementId = obj.getString("achievementId"),
                    userId = userId,
                    photoPath = obj.getString("photoPath"),
                    detectedLabels = obj.getString("detectedLabels"),
                    timestamp = obj.getLong("timestamp")
                ))
            }
        }
    }
}
