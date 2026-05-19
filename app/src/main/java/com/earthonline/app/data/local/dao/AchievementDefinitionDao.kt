package com.earthonline.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.earthonline.app.data.local.entity.AchievementDefinitionEntity

@Dao
interface AchievementDefinitionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(definitions: List<AchievementDefinitionEntity>)

    @Query("SELECT * FROM achievement_definition")
    suspend fun getAll(): List<AchievementDefinitionEntity>

    @Query("SELECT * FROM achievement_definition WHERE achievement_id = :achievementId")
    suspend fun getById(achievementId: String): AchievementDefinitionEntity?

    @Query("SELECT * FROM achievement_definition WHERE trigger_type = :triggerType")
    suspend fun getByTriggerType(triggerType: String): List<AchievementDefinitionEntity>

    @Query("SELECT COUNT(*) FROM achievement_definition")
    suspend fun count(): Int
}
