package com.earthonline.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.earthonline.app.data.local.entity.AchievementEvidence

// 成就證據 DAO：提供證據照片的插入、依成就查詢最新一筆及依使用者查詢全部
@Dao
interface AchievementEvidenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // 插入一筆證據，衝突時覆蓋
    suspend fun insert(evidence: AchievementEvidence)

    @Query("SELECT * FROM achievement_evidence WHERE achievement_id = :achievementId AND user_id = :userId ORDER BY timestamp DESC") // 查詢特定成就的所有證據（依時間倒序）
    suspend fun getByAchievement(achievementId: String, userId: String): List<AchievementEvidence>

    @Query("SELECT * FROM achievement_evidence WHERE achievement_id = :achievementId AND user_id = :userId ORDER BY timestamp DESC LIMIT 1") // 查詢特定成就的最新一筆證據
    suspend fun getLatestByAchievement(achievementId: String, userId: String): AchievementEvidence?

    @Query("SELECT * FROM achievement_evidence WHERE user_id = :userId ORDER BY timestamp DESC") // 查詢使用者的所有證據
    suspend fun getAllByUser(userId: String): List<AchievementEvidence>
}
