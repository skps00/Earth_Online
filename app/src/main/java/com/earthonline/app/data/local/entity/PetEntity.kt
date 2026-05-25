package com.earthonline.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pet")
data class PetEntity(
    @PrimaryKey val id: Int = 1,
    @ColumnInfo(name = "name") val name: String = "地球精靈"
)
