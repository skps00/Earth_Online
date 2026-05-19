package com.earthonline.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "user_achievement_progress",
    primaryKeys = ["user_id", "achievement_id"],
    foreignKeys = [
        ForeignKey(
            entity = AchievementDefinitionEntity::class,
            parentColumns = ["achievement_id"],
            childColumns = ["achievement_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["achievement_id"]),
        Index(value = ["trigger_type"])
    ]
)
data class UserAchievementProgressEntity(
    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "achievement_id")
    val achievementId: String,

    @ColumnInfo(name = "current_progress")
    val currentProgress: Long = 0L,

    @ColumnInfo(name = "is_unlocked")
    val isUnlocked: Boolean = false,

    @ColumnInfo(name = "unlocked_date")
    val unlockedDate: Long? = null,

    @ColumnInfo(name = "trigger_type")
    val triggerType: String
)
