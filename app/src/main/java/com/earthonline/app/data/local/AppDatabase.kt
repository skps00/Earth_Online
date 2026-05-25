package com.earthonline.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.earthonline.app.data.local.dao.AchievementDefinitionDao
import com.earthonline.app.data.local.dao.AchievementEvidenceDao
import com.earthonline.app.data.local.dao.CheckInRecordDao
import com.earthonline.app.data.local.dao.PetDao
import com.earthonline.app.data.local.dao.UserAchievementProgressDao
import com.earthonline.app.data.local.entity.AchievementDefinitionEntity
import com.earthonline.app.data.local.entity.AchievementEvidence
import com.earthonline.app.data.local.entity.CheckInRecord
import com.earthonline.app.data.local.entity.PetEntity
import com.earthonline.app.data.local.entity.UserAchievementProgressEntity

@Database(
    entities = [
        AchievementDefinitionEntity::class,
        UserAchievementProgressEntity::class,
        CheckInRecord::class,
        AchievementEvidence::class,
        PetEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun achievementDefinitionDao(): AchievementDefinitionDao
    abstract fun userAchievementProgressDao(): UserAchievementProgressDao
    abstract fun checkInRecordDao(): CheckInRecordDao
    abstract fun achievementEvidenceDao(): AchievementEvidenceDao
    abstract fun petDao(): PetDao
}
