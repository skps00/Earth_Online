package com.earthonline.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.earthonline.app.data.local.entity.PetEntity

@Dao
interface PetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(pet: PetEntity)

    @Query("SELECT * FROM pet WHERE id = 1")
    suspend fun get(): PetEntity?
}
