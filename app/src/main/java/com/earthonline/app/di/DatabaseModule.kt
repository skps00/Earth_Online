package com.earthonline.app.di

import android.content.Context
import androidx.room.Room
import com.earthonline.app.data.local.AppDatabase
import com.earthonline.app.data.local.dao.AchievementDefinitionDao
import com.earthonline.app.data.local.dao.UserAchievementProgressDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "earth_online.db"
        ).build()
    }

    @Provides
    fun provideAchievementDefinitionDao(database: AppDatabase): AchievementDefinitionDao {
        return database.achievementDefinitionDao()
    }

    @Provides
    fun provideUserAchievementProgressDao(database: AppDatabase): UserAchievementProgressDao {
        return database.userAchievementProgressDao()
    }
}
