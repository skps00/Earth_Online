package com.earthonline.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.earthonline.app.data.local.entity.UserAchievementProgressEntity

// 使用者成就進度 DAO：提供成就進度的插入、更新、批次增量與解鎖操作
@Dao
interface UserAchievementProgressDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE) // 批次插入，已存在則忽略
    suspend fun insertAll(progressList: List<UserAchievementProgressEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE) // 插入單筆，已存在則忽略
    suspend fun insert(progress: UserAchievementProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE) // 插入單筆，衝突時覆蓋
    suspend fun insertReplace(progress: UserAchievementProgressEntity)

    @Update // 更新進度實體
    suspend fun update(progress: UserAchievementProgressEntity)

    @Query("SELECT * FROM user_achievement_progress WHERE user_id = :userId") // 查詢使用者所有成就進度
    suspend fun getAllByUser(userId: String): List<UserAchievementProgressEntity>

    @Query(
        """
        SELECT * FROM user_achievement_progress 
        WHERE user_id = :userId 
        AND trigger_type = :triggerType 
        AND is_unlocked = 0
        """
    ) // 查詢使用者某類型中尚未解鎖的成就進度
    suspend fun getLockedByUserAndType(
        userId: String,
        triggerType: String
    ): List<UserAchievementProgressEntity>

    @Query(
        """
        SELECT * FROM user_achievement_progress 
        WHERE user_id = :userId 
        AND achievement_id = :achievementId
        """
    ) // 查詢使用者對特定成就的進度
    suspend fun getByUserAndAchievement(
        userId: String,
        achievementId: String
    ): UserAchievementProgressEntity?

    @Query(
        """
        UPDATE user_achievement_progress 
        SET current_progress = current_progress + :increment 
        WHERE user_id = :userId 
        AND trigger_type = :triggerType 
        AND is_unlocked = 0
        """
    ) // 對某類型所有未解鎖成就增加指定進度值
    suspend fun incrementProgress(userId: String, triggerType: String, increment: Long)

    @Query(
        """
        SELECT SUM(current_progress) FROM user_achievement_progress 
        WHERE user_id = :userId AND trigger_type = :triggerType
        """
    ) // 加總使用者在某類型的進度總和
    suspend fun getTotalProgressByType(userId: String, triggerType: String): Long?

    @Transaction // 解鎖成就的複合操作：查詢 → 設定解鎖狀態與日期 → 更新
    suspend fun unlockAchievement(
        userId: String,
        achievementId: String,
        unlockedDate: Long
    ) {
        val queryResult = getByUserAndAchievement(userId, achievementId)
        if (queryResult != null && !queryResult.isUnlocked) {
            update(
                queryResult.copy(
                    isUnlocked = true,
                    unlockedDate = unlockedDate
                )
            )
        }
    }

    @Query("SELECT COUNT(*) FROM user_achievement_progress WHERE user_id = :userId AND trigger_type = :triggerType") // 計算使用者某類型的成就記錄數
    suspend fun countByUserAndType(userId: String, triggerType: String): Int

    @Query("UPDATE user_achievement_progress SET current_progress = :value WHERE user_id = :userId AND trigger_type = :triggerType AND is_unlocked = 0") // 直接設定某類型未解鎖成就的進度值
    suspend fun setProgressByType(userId: String, triggerType: String, value: Long)

    @Query("UPDATE user_achievement_progress SET current_progress = current_progress + 1 WHERE user_id = :userId AND achievement_id = :achievementId AND is_unlocked = 0") // 對特定成就進度加 1
    suspend fun incrementProgressById(userId: String, achievementId: String)

    @Query(
        """
        UPDATE user_achievement_progress 
        SET current_progress = :value 
        WHERE user_id = :userId 
        AND achievement_id = :achievementId 
        AND is_unlocked = 0
        """
    ) // 直接設定特定成就的進度值
    suspend fun setProgressById(userId: String, achievementId: String, value: Long)
}
