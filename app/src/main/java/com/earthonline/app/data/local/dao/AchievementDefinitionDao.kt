package com.earthonline.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.earthonline.app.data.local.entity.AchievementDefinitionEntity

// 成就定義 DAO：提供成就規則資料的批次寫入、依 ID/觸發類型查詢與計數
@Dao
interface AchievementDefinitionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) // 插入多筆，衝突時覆蓋
    suspend fun insertAll(definitions: List<AchievementDefinitionEntity>)

    @Query("SELECT * FROM achievement_definition") // 取得所有成就定義
    suspend fun getAll(): List<AchievementDefinitionEntity>

    @Query("SELECT * FROM achievement_definition WHERE achievement_id = :achievementId") // 依 ID 查詢單一成就
    suspend fun getById(achievementId: String): AchievementDefinitionEntity?

    @Query("SELECT * FROM achievement_definition WHERE trigger_type = :triggerType") // 依觸發類型查詢
    suspend fun getByTriggerType(triggerType: String): List<AchievementDefinitionEntity>

    @Query("SELECT COUNT(*) FROM achievement_definition") // 計算成就總數
    suspend fun count(): Int
}
