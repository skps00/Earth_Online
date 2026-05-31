package com.earthonline.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// 成就證據實體：儲存成就解鎖時的照片路徑與 AI 標籤辨識結果
@Entity(tableName = "achievement_evidence")
data class AchievementEvidence(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // 自動遞增主鍵
    @ColumnInfo(name = "achievement_id") val achievementId: String, // 對應的成就 ID
    @ColumnInfo(name = "user_id") val userId: String, // 使用者識別碼
    @ColumnInfo(name = "photo_path") val photoPath: String, // 照片檔案路徑
    @ColumnInfo(name = "detected_labels") val detectedLabels: String, // AI 辨識到的標籤（逗號分隔）
    @ColumnInfo(name = "timestamp") val timestamp: Long // 證據建立時間戳
)
