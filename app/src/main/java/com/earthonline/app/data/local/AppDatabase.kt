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

// Room 本地資料庫：定義所有實體、版本號，並暴露各 DAO 的抽象方法
@Database(
    entities = [
        AchievementDefinitionEntity::class, // 成就定義表
        UserAchievementProgressEntity::class, // 使用者成就進度表
        CheckInRecord::class, // 打卡記錄表
        AchievementEvidence::class, // 成就證據表
        PetEntity::class // 寵物表
    ],
    version = 12,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun achievementDefinitionDao(): AchievementDefinitionDao // 成就定義 DAO
    abstract fun userAchievementProgressDao(): UserAchievementProgressDao // 使用者成就進度 DAO
    abstract fun checkInRecordDao(): CheckInRecordDao // 打卡記錄 DAO
    abstract fun achievementEvidenceDao(): AchievementEvidenceDao // 成就證據 DAO
    abstract fun petDao(): PetDao // 寵物 DAO
}
