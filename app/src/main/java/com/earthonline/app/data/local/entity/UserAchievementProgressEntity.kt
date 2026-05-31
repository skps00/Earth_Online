package com.earthonline.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

// 使用者成就進度實體：記錄每位使用者對每個成就的當前進度與解鎖狀態
@Entity(
    tableName = "user_achievement_progress",
    primaryKeys = ["user_id", "achievement_id"], // 複合主鍵：使用者 + 成就
    foreignKeys = [
        ForeignKey(
            entity = AchievementDefinitionEntity::class,
            parentColumns = ["achievement_id"],
            childColumns = ["achievement_id"]
        ) // 外鍵關聯成就定義表
    ],
    indices = [
        Index(value = ["user_id"]), // 加速依使用者查詢
        Index(value = ["achievement_id"]), // 加速依成就查詢
        Index(value = ["trigger_type"]) // 加速依觸發類型查詢
    ]
)
data class UserAchievementProgressEntity(
    @ColumnInfo(name = "user_id")
    val userId: String, // 使用者識別碼

    @ColumnInfo(name = "achievement_id")
    val achievementId: String, // 對應的成就 ID

    @ColumnInfo(name = "current_progress")
    val currentProgress: Long = 0L, // 當前進度值

    @ColumnInfo(name = "is_unlocked")
    val isUnlocked: Boolean = false, // 是否已解鎖

    @ColumnInfo(name = "unlocked_date")
    val unlockedDate: Long? = null, // 解鎖時間戳（未解鎖則為 null）

    @ColumnInfo(name = "trigger_type")
    val triggerType: String // 觸發類型，方便批次更新
)
