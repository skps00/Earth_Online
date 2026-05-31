package com.earthonline.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.earthonline.app.data.local.entity.CheckInRecord

// 打卡記錄 DAO：提供打卡資料的插入、去重統計（位置/國家/洲）與歷史查詢
@Dao
interface CheckInRecordDao {

    @Insert // 插入一筆打卡記錄
    suspend fun insert(record: CheckInRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE) // 插入（衝突時覆蓋）
    suspend fun insertReplace(record: CheckInRecord)

    @Query("SELECT COUNT(*) FROM checkin_record WHERE user_id = :userId") // 計算使用者總打卡次數
    suspend fun countByUser(userId: String): Int

    @Query("SELECT COUNT(DISTINCT ROUND(latitude, 3) || ',' || ROUND(longitude, 3)) FROM checkin_record WHERE user_id = :userId") // 計算不重複打卡位置數（經緯度小數三位內視為相同）
    suspend fun countUniqueLocations(userId: String): Int

    @Query("SELECT COUNT(DISTINCT country) FROM checkin_record WHERE user_id = :userId AND country != ''") // 計算不重複國家數
    suspend fun countUniqueCountries(userId: String): Int

    @Query("SELECT COUNT(DISTINCT continent) FROM checkin_record WHERE user_id = :userId AND continent != ''") // 計算不重複洲別數
    suspend fun countUniqueContinents(userId: String): Int

    @Query("SELECT * FROM checkin_record WHERE user_id = :userId ORDER BY timestamp DESC") // 依時間倒序查詢使用者所有打卡記錄
    suspend fun getAllByUser(userId: String): List<CheckInRecord>
}
