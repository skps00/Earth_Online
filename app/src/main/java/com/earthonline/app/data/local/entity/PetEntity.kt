package com.earthonline.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import com.earthonline.app.AppConstants

@Entity(tableName = "pet")
data class PetEntity(
    @PrimaryKey val id: Int = 1,
    @ColumnInfo(name = "name") val name: String = AppConstants.DEFAULT_PET_NAME,
    @ColumnInfo(name = "emoji") val emoji: String = AppConstants.DEFAULT_PET_EMOJI,
    @ColumnInfo(name = "level") val level: Int = 1,
    @ColumnInfo(name = "xp") val xp: Long = 0,
    @ColumnInfo(name = "strength") val strength: Int = 0,
    @ColumnInfo(name = "agility") val agility: Int = 0,
    @ColumnInfo(name = "intelligence") val intelligence: Int = 0,
    @ColumnInfo(name = "charisma") val charisma: Int = 0,
    @ColumnInfo(name = "vitality") val vitality: Int = 0
)
