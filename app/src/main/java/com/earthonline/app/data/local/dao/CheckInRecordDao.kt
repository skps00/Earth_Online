package com.earthonline.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.earthonline.app.data.local.entity.CheckInRecord

@Dao
interface CheckInRecordDao {
    @Insert
    suspend fun insert(record: CheckInRecord)

    @Query("SELECT COUNT(*) FROM checkin_record WHERE user_id = :userId")
    suspend fun countByUser(userId: String): Int

    @Query("SELECT COUNT(DISTINCT ROUND(latitude, 3) || ',' || ROUND(longitude, 3)) FROM checkin_record WHERE user_id = :userId")
    suspend fun countUniqueLocations(userId: String): Int
}
