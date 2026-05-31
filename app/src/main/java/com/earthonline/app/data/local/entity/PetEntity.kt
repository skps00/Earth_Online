package com.earthonline.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import com.earthonline.app.AppConstants

// 寵物實體：儲存唯一寵物的名稱、經驗值、等級與五大屬性數值
@Entity(tableName = "pet")
data class PetEntity(
    @PrimaryKey val id: Int = 1, // 固定為 1，單一寵物模式
    @ColumnInfo(name = "name") val name: String = AppConstants.DEFAULT_PET_NAME, // 寵物名稱
    @ColumnInfo(name = "emoji") val emoji: String = AppConstants.DEFAULT_PET_EMOJI, // 寵物表情符號
    @ColumnInfo(name = "level") val level: Int = 1, // 寵物等級
    @ColumnInfo(name = "xp") val xp: Long = 0, // 累積經驗值
    @ColumnInfo(name = "strength") val strength: Int = 0, // 力量屬性
    @ColumnInfo(name = "agility") val agility: Int = 0, // 敏捷屬性
    @ColumnInfo(name = "intelligence") val intelligence: Int = 0, // 智力屬性
    @ColumnInfo(name = "charisma") val charisma: Int = 0, // 魅力屬性
    @ColumnInfo(name = "vitality") val vitality: Int = 0 // 活力屬性
)
