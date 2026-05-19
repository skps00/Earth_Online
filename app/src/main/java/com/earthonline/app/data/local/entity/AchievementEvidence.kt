package com.earthonline.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievement_evidence")
data class AchievementEvidence(
    @PrimaryKey @ColumnInfo(name = "achievement_id") val achievementId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "photo_path") val photoPath: String,
    @ColumnInfo(name = "detected_labels") val detectedLabels: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)
