package com.earthonline.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.earthonline.app.data.local.entity.PetEntity

// 寵物 DAO：提供單一寵物資料的儲存與讀取（固定 id = 1）
@Dao
interface PetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) // 儲存寵物資料，衝突時覆蓋
    suspend fun save(pet: PetEntity)

    @Query("SELECT * FROM pet WHERE id = 1") // 讀取唯一寵物資料
    suspend fun get(): PetEntity?
}
