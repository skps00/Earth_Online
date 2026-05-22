package com.earthonline.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.earthonline.app.data.local.entity.UserAchievementProgressEntity

@Dao
interface UserAchievementProgressDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(progressList: List<UserAchievementProgressEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(progress: UserAchievementProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplace(progress: UserAchievementProgressEntity)

    @Update
    suspend fun update(progress: UserAchievementProgressEntity)

    @Query("SELECT * FROM user_achievement_progress WHERE user_id = :userId")
    suspend fun getAllByUser(userId: String): List<UserAchievementProgressEntity>

    @Query(
        """
        SELECT * FROM user_achievement_progress 
        WHERE user_id = :userId 
        AND trigger_type = :triggerType 
        AND is_unlocked = 0
        """
    )
    suspend fun getUnlockedByUserAndType(
        userId: String,
        triggerType: String
    ): List<UserAchievementProgressEntity>

    @Query(
        """
        SELECT * FROM user_achievement_progress 
        WHERE user_id = :userId 
        AND achievement_id = :achievementId
        """
    )
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
    )
    suspend fun incrementProgress(userId: String, triggerType: String, increment: Long)

    @Query(
        """
        SELECT SUM(current_progress) FROM user_achievement_progress 
        WHERE user_id = :userId AND trigger_type = :triggerType
        """
    )
    suspend fun getTotalProgressByType(userId: String, triggerType: String): Long?

    @Transaction
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

    @Query("SELECT COUNT(*) FROM user_achievement_progress WHERE user_id = :userId AND trigger_type = :triggerType")
    suspend fun countByUserAndType(userId: String, triggerType: String): Int

    @Query("UPDATE user_achievement_progress SET current_progress = :value WHERE user_id = :userId AND trigger_type = :triggerType AND is_unlocked = 0")
    suspend fun setProgressByType(userId: String, triggerType: String, value: Long)

    @Query("UPDATE user_achievement_progress SET current_progress = current_progress + 1 WHERE user_id = :userId AND achievement_id = :achievementId AND is_unlocked = 0")
    suspend fun incrementProgressById(userId: String, achievementId: String)

    @Query(
        """
        UPDATE user_achievement_progress 
        SET current_progress = :value 
        WHERE user_id = :userId 
        AND achievement_id = :achievementId 
        AND is_unlocked = 0
        """
    )
    suspend fun setProgressById(userId: String, achievementId: String, value: Long)
}
