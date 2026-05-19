package com.earthonline.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.earthonline.app.data.local.entity.AchievementEvidence

@Dao
interface AchievementEvidenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(evidence: AchievementEvidence)

    @Query("SELECT * FROM achievement_evidence WHERE achievement_id = :achievementId AND user_id = :userId")
    suspend fun getByAchievement(achievementId: String, userId: String): AchievementEvidence?
}
