package com.earthonline.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checkin_record")
data class CheckInRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "continent") val continent: String,
    @ColumnInfo(name = "address") val address: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)
