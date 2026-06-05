package com.earthonline.app.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.earthonline.app.AppConstants
import com.earthonline.app.data.local.AppDatabase
import com.earthonline.app.data.local.dao.AchievementDefinitionDao
import com.earthonline.app.data.local.dao.AchievementEvidenceDao
import com.earthonline.app.data.local.dao.CheckInRecordDao
import com.earthonline.app.data.local.dao.PetDao
import com.earthonline.app.data.local.dao.UserAchievementProgressDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Hilt 依賴注入模組，提供 Room 資料庫實例及所有 DAO 的單例供給
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // No schema change — only seed data added (daily_puzzle achievement)
            }
        }
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
                        AppConstants.DATABASE_NAME
        ).addMigrations(MIGRATION_12_13)
         .fallbackToDestructiveMigration()
         .build()
    }

    @Provides
    fun provideAchievementDefinitionDao(database: AppDatabase): AchievementDefinitionDao {
        return database.achievementDefinitionDao()
    }

    @Provides
    fun provideUserAchievementProgressDao(database: AppDatabase): UserAchievementProgressDao {
        return database.userAchievementProgressDao()
    }

    @Provides
    fun provideCheckInRecordDao(database: AppDatabase): CheckInRecordDao {
        return database.checkInRecordDao()
    }

    @Provides
    fun provideAchievementEvidenceDao(database: AppDatabase): AchievementEvidenceDao {
        return database.achievementEvidenceDao()
    }

    @Provides
    fun providePetDao(database: AppDatabase): PetDao {
        return database.petDao()
    }
}
