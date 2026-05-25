package com.earthonline.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievement_definition")
data class AchievementDefinitionEntity(
    @PrimaryKey
    @ColumnInfo(name = "achievement_id")
    val achievementId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "icon_asset")
    val iconAsset: String,

    @ColumnInfo(name = "trigger_type")
    val triggerType: String,

    @ColumnInfo(name = "trigger_goal")
    val triggerGoal: Long,

    @ColumnInfo(name = "is_hidden")
    val isHidden: Boolean = false,

    @ColumnInfo(name = "reward_points")
    val rewardPoints: Int = 0,

    @ColumnInfo(name = "hint")
    val hint: String = "",

    @ColumnInfo(name = "strength_weight")
    val strengthWeight: Float = 0f,

    @ColumnInfo(name = "agility_weight")
    val agilityWeight: Float = 0f,

    @ColumnInfo(name = "intelligence_weight")
    val intelligenceWeight: Float = 0f,

    @ColumnInfo(name = "charisma_weight")
    val charismaWeight: Float = 0f,

    @ColumnInfo(name = "vitality_weight")
    val vitalityWeight: Float = 0f
)
