package com.earthonline.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// 打卡記錄實體：儲存每次地理位置簽到的經緯度、國家與地址資訊
@Entity(tableName = "checkin_record")
data class CheckInRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // 自動遞增主鍵
    @ColumnInfo(name = "user_id") val userId: String, // 使用者識別碼
    @ColumnInfo(name = "latitude") val latitude: Double, // 緯度
    @ColumnInfo(name = "longitude") val longitude: Double, // 經度
    @ColumnInfo(name = "country") val country: String, // 國家名稱
    @ColumnInfo(name = "continent") val continent: String, // 洲別
    @ColumnInfo(name = "address") val address: String, // 反向地理編碼地址
    @ColumnInfo(name = "timestamp") val timestamp: Long // 簽到時間戳
)
