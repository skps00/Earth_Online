package com.earthonline.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// 成就定義實體：儲存每個成就的觸發規則、獎勵數值與屬性加成權重
@Entity(tableName = "achievement_definition")
data class AchievementDefinitionEntity(
    @PrimaryKey
    @ColumnInfo(name = "achievement_id")
    val achievementId: String, // 成就唯一識別碼

    @ColumnInfo(name = "title")
    val title: String, // 成就名稱

    @ColumnInfo(name = "description")
    val description: String, // 成就描述

    @ColumnInfo(name = "icon_asset")
    val iconAsset: String, // 成就圖示資源路徑

    @ColumnInfo(name = "trigger_type")
    val triggerType: String, // 觸發類型（如打卡次數、國家數等）

    @ColumnInfo(name = "trigger_goal")
    val triggerGoal: Long, // 觸發門檻值

    @ColumnInfo(name = "is_hidden")
    val isHidden: Boolean = false, // 是否為隱藏成就

    @ColumnInfo(name = "reward_points")
    val rewardPoints: Int = 0, // 解鎖獎勵點數

    @ColumnInfo(name = "hint")
    val hint: String = "", // 隱藏成就的提示文字

    @ColumnInfo(name = "strength_weight")
    val strengthWeight: Float = 0f, // 力量屬性加成權重

    @ColumnInfo(name = "agility_weight")
    val agilityWeight: Float = 0f, // 敏捷屬性加成權重

    @ColumnInfo(name = "intelligence_weight")
    val intelligenceWeight: Float = 0f, // 智力屬性加成權重

    @ColumnInfo(name = "charisma_weight")
    val charismaWeight: Float = 0f, // 魅力屬性加成權重

    @ColumnInfo(name = "vitality_weight")
    val vitalityWeight: Float = 0f // 活力屬性加成權重
)
