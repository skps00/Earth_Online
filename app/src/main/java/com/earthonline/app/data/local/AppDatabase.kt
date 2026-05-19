package com.earthonline.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.earthonline.app.data.local.dao.AchievementDefinitionDao
import com.earthonline.app.data.local.dao.UserAchievementProgressDao
import com.earthonline.app.data.local.entity.AchievementDefinitionEntity
import com.earthonline.app.data.local.entity.UserAchievementProgressEntity

@Database(
    entities = [
        AchievementDefinitionEntity::class,
        UserAchievementProgressEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun achievementDefinitionDao(): AchievementDefinitionDao
    abstract fun userAchievementProgressDao(): UserAchievementProgressDao
}
