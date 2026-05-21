package com.earthonline.app.data.repository

import com.earthonline.app.data.local.dao.AchievementDefinitionDao
import com.earthonline.app.data.local.dao.AchievementEvidenceDao
import com.earthonline.app.data.local.dao.CheckInRecordDao
import com.earthonline.app.data.local.dao.UserAchievementProgressDao
import com.earthonline.app.data.local.entity.AchievementDefinitionEntity
import com.earthonline.app.data.local.entity.AchievementEvidence
import com.earthonline.app.data.local.entity.CheckInRecord
import com.earthonline.app.data.local.entity.UserAchievementProgressEntity
import com.earthonline.app.domain.model.TriggerType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton

data class UnlockedAchievementEvent(
    val achievement: AchievementDefinitionEntity,
    val unlockedDate: Long
)

@Singleton
class AchievementRepository @Inject constructor(
    private val definitionDao: AchievementDefinitionDao,
    private val progressDao: UserAchievementProgressDao,
    private val checkInRecordDao: CheckInRecordDao,
    private val evidenceDao: AchievementEvidenceDao
) {
    private val _unlockEvents = MutableSharedFlow<UnlockedAchievementEvent>(replay = 0)
    val unlockEvents: SharedFlow<UnlockedAchievementEvent> = _unlockEvents

    private val _totalCheckins = MutableSharedFlow<Long>(replay = 1)
    val totalCheckins: SharedFlow<Long> = _totalCheckins

    suspend fun initializeAchievements() {
        val definitions = listOf(
            AchievementDefinitionEntity("checkin_1", "初次打卡", "在 1 個地點打卡", "ic_achievement_photo_1", TriggerType.LOCATION_CHECKIN_COUNT.value, 1L, false, 10),
            AchievementDefinitionEntity("checkin_3", "三度探訪", "在 3 個不同地點打卡", "ic_achievement_photo_2", TriggerType.LOCATION_CHECKIN_COUNT.value, 3L, false, 15),
            AchievementDefinitionEntity("checkin_5", "五方雲遊", "在 5 個不同地點打卡", "ic_achievement_photo_3", TriggerType.LOCATION_CHECKIN_COUNT.value, 5L, false, 25),
            AchievementDefinitionEntity("checkin_10", "十全十美", "在 10 個不同地點打卡", "ic_achievement_photo_1", TriggerType.LOCATION_CHECKIN_COUNT.value, 10L, false, 50),
            AchievementDefinitionEntity("checkin_25", "足跡遍布", "在 25 個不同地點打卡", "ic_achievement_photo_2", TriggerType.LOCATION_CHECKIN_COUNT.value, 25L, false, 100),
            AchievementDefinitionEntity("checkin_50", "環球旅者", "在 50 個不同地點打卡", "ic_achievement_photo_3", TriggerType.LOCATION_CHECKIN_COUNT.value, 50L, false, 200),

            AchievementDefinitionEntity("explore_7continents", "征服七大洲", "踏上 7 大洲", "ic_achievement_photo_1", TriggerType.AUTO_TRACK.value, 7L, false, 1000),
            AchievementDefinitionEntity("explore_50countries", "環遊世界", "造訪 50 個國家", "ic_achievement_photo_2", TriggerType.AUTO_TRACK.value, 50L, false, 2000),
            AchievementDefinitionEntity("explore_10countries", "旅遊達人", "造訪 10 個國家", "ic_achievement_photo_3", TriggerType.AUTO_TRACK.value, 10L, false, 200),
            AchievementDefinitionEntity("explore_5countries", "國際旅人", "造訪 5 個國家", "ic_achievement_photo_1", TriggerType.AUTO_TRACK.value, 5L, false, 100),
            AchievementDefinitionEntity("explore_3continents", "跨洲冒險", "踏上 3 大洲", "ic_achievement_photo_2", TriggerType.AUTO_TRACK.value, 3L, false, 500),
            AchievementDefinitionEntity("explore_dateline", "穿越換日線", "跨越國際換日線", "ic_achievement_photo_3", TriggerType.MANUAL_CONFIRM.value, 1L, false, 100),
            AchievementDefinitionEntity("explore_missed_flight", "錯過班機", "錯過一次航班", "ic_achievement_photo_1", TriggerType.MANUAL_CONFIRM.value, 1L, false, 50),
            AchievementDefinitionEntity("explore_island", "島嶼探險", "造訪一座島嶼", "ic_achievement_photo_2", TriggerType.MANUAL_CONFIRM.value, 1L, false, 50),
            AchievementDefinitionEntity("explore_mountain", "登峰造極", "攀登一座高山", "ic_achievement_photo_3", TriggerType.MANUAL_CONFIRM.value, 1L, false, 75),
            AchievementDefinitionEntity("explore_solo", "獨自旅行", "完成一次獨自旅行", "ic_achievement_photo_1", TriggerType.MANUAL_CONFIRM.value, 1L, false, 100),
            AchievementDefinitionEntity("explore_ocean", "橫渡大洋", "橫渡一片大洋", "ic_achievement_photo_2", TriggerType.MANUAL_CONFIRM.value, 1L, false, 150),
            AchievementDefinitionEntity("explore_first_abroad", "首度出國", "第一次出國旅行", "ic_achievement_photo_3", TriggerType.MANUAL_CONFIRM.value, 1L, false, 50),
            AchievementDefinitionEntity("explore_japan", "日本漫遊", "造訪日本", "ic_achievement_photo_1", TriggerType.MANUAL_CONFIRM.value, 1L, false, 50),
            AchievementDefinitionEntity("explore_europe", "歐洲巡禮", "造訪歐洲", "ic_achievement_photo_2", TriggerType.MANUAL_CONFIRM.value, 1L, false, 75),
            AchievementDefinitionEntity("explore_africa", "非洲探險", "造訪非洲", "ic_achievement_photo_3", TriggerType.MANUAL_CONFIRM.value, 1L, false, 100),
            AchievementDefinitionEntity("explore_south_america", "南美之旅", "造訪南美洲", "ic_achievement_photo_1", TriggerType.MANUAL_CONFIRM.value, 1L, false, 100),
            AchievementDefinitionEntity("explore_antarctica", "南極遠征", "造訪南極洲", "ic_achievement_photo_2", TriggerType.MANUAL_CONFIRM.value, 1L, false, 500),
            AchievementDefinitionEntity("explore_australia", "澳洲歷險", "造訪澳洲", "ic_achievement_photo_3", TriggerType.MANUAL_CONFIRM.value, 1L, false, 100),
            AchievementDefinitionEntity("explore_capital", "首都巡禮", "造訪一個首都城市", "ic_achievement_photo_1", TriggerType.MANUAL_CONFIRM.value, 1L, false, 30),
            AchievementDefinitionEntity("explore_unesco", "世界遺產", "造訪一個 UNESCO 世界遺產", "ic_achievement_photo_2", TriggerType.MANUAL_CONFIRM.value, 1L, false, 50),
            AchievementDefinitionEntity("explore_temple", "古剎參拜", "參訪一座寺廟", "ic_achievement_photo_3", TriggerType.MANUAL_CONFIRM.value, 1L, false, 20),
            AchievementDefinitionEntity("explore_night_market", "夜市饗宴", "逛一個夜市", "ic_achievement_photo_1", TriggerType.MANUAL_CONFIRM.value, 1L, false, 20),
            AchievementDefinitionEntity("explore_hot_spring", "溫泉之旅", "泡一次溫泉", "ic_achievement_photo_2", TriggerType.MANUAL_CONFIRM.value, 1L, false, 30),
            AchievementDefinitionEntity("explore_beach", "海灘時光", "享受一片海灘", "ic_achievement_photo_3", TriggerType.MANUAL_CONFIRM.value, 1L, false, 20),
            AchievementDefinitionEntity("explore_museum", "博物館日", "參觀一個博物館", "ic_achievement_photo_1", TriggerType.MANUAL_CONFIRM.value, 1L, false, 30),
            AchievementDefinitionEntity("explore_airport", "機場漫遊", "在機場待超過 3 小時", "ic_achievement_photo_2", TriggerType.MANUAL_CONFIRM.value, 1L, false, 30),
            AchievementDefinitionEntity("explore_cruise", "遊輪之旅", "搭乘一次遊輪", "ic_achievement_photo_3", TriggerType.MANUAL_CONFIRM.value, 1L, false, 75),
            AchievementDefinitionEntity("explore_border", "邊境穿梭", "跨越一次國境", "ic_achievement_photo_1", TriggerType.MANUAL_CONFIRM.value, 1L, false, 50),
            AchievementDefinitionEntity("explore_canyon", "峽谷探險", "造訪一座峽谷", "ic_achievement_photo_2", TriggerType.MANUAL_CONFIRM.value, 1L, false, 50),
            AchievementDefinitionEntity("explore_volcano", "火山探秘", "造訪一座火山", "ic_achievement_photo_3", TriggerType.MANUAL_CONFIRM.value, 1L, false, 50),
            AchievementDefinitionEntity("explore_lake", "湖畔靜謐", "造訪一座湖泊", "ic_achievement_photo_1", TriggerType.MANUAL_CONFIRM.value, 1L, false, 30),

            AchievementDefinitionEntity("career_phd", "博士學位", "獲得博士學位", "ic_achievement_photo_2", TriggerType.MANUAL_CONFIRM.value, 1L, false, 200),
            AchievementDefinitionEntity("career_graduate", "學業有成", "大學畢業", "ic_achievement_photo_3", TriggerType.MANUAL_CONFIRM.value, 1L, false, 100),
            AchievementDefinitionEntity("career_house", "置產置業", "購買第一間房子", "ic_achievement_photo_1", TriggerType.MANUAL_CONFIRM.value, 1L, false, 300),
            AchievementDefinitionEntity("career_first_job", "踏入職場", "獲得第一份工作", "ic_achievement_photo_2", TriggerType.MANUAL_CONFIRM.value, 1L, false, 50),
            AchievementDefinitionEntity("career_365_ontime", "全勤達人", "連續一年準時上班", "ic_achievement_photo_3", TriggerType.MANUAL_CONFIRM.value, 1L, false, 150),
            AchievementDefinitionEntity("career_masters", "碩士學位", "獲得碩士學位", "ic_achievement_photo_1", TriggerType.MANUAL_CONFIRM.value, 1L, false, 100),
            AchievementDefinitionEntity("career_bachelor", "學士學位", "獲得學士學位", "ic_achievement_photo_2", TriggerType.MANUAL_CONFIRM.value, 1L, false, 50),
            AchievementDefinitionEntity("career_fired", "被開除了", "被公司解僱", "ic_achievement_photo_3", TriggerType.MANUAL_CONFIRM.value, 1L, false, 75),
            AchievementDefinitionEntity("career_promotion", "升職加薪", "獲得一次升職", "ic_achievement_photo_1", TriggerType.MANUAL_CONFIRM.value, 1L, false, 100),
            AchievementDefinitionEntity("career_startup", "創業先鋒", "創辦一家公司", "ic_achievement_photo_2", TriggerType.MANUAL_CONFIRM.value, 1L, false, 200),

            AchievementDefinitionEntity("daily_lottery", "發票中獎", "中一次發票", "ic_achievement_photo_3", TriggerType.MANUAL_CONFIRM.value, 1L, false, 50),
            AchievementDefinitionEntity("daily_social", "社交達人", "參加一場社交活動", "ic_achievement_photo_1", TriggerType.MANUAL_CONFIRM.value, 1L, false, 20),
            AchievementDefinitionEntity("daily_pets", "毛孩夥伴", "養 2 隻寵物", "ic_achievement_photo_2", TriggerType.MANUAL_CONFIRM.value, 2L, false, 50),
            AchievementDefinitionEntity("daily_earlybird", "早起的鳥兒", "清晨 5 點前起床", "ic_achievement_photo_3", TriggerType.MANUAL_CONFIRM.value, 1L, false, 30),
            AchievementDefinitionEntity("daily_cook", "自煮生活", "自己煮一餐", "ic_achievement_photo_1", TriggerType.MANUAL_CONFIRM.value, 1L, false, 10),
            AchievementDefinitionEntity("daily_binge", "追劇馬拉松", "一次看 10 集以上", "ic_achievement_photo_2", TriggerType.MANUAL_CONFIRM.value, 1L, false, 30),
            AchievementDefinitionEntity("daily_allnighter", "徹夜未眠", "通宵一次", "ic_achievement_photo_3", TriggerType.MANUAL_CONFIRM.value, 1L, false, 30),
            AchievementDefinitionEntity("daily_exercise_30", "運動習慣", "連續運動 30 分鐘", "ic_achievement_photo_1", TriggerType.MANUAL_CONFIRM.value, 1L, false, 30),
            AchievementDefinitionEntity("daily_read_10", "閱讀時光", "讀完 10 本書", "ic_achievement_photo_2", TriggerType.MANUAL_CONFIRM.value, 10L, false, 100),
            AchievementDefinitionEntity("daily_no_phone", "數位排毒", "一整天不用手機", "ic_achievement_photo_3", TriggerType.MANUAL_CONFIRM.value, 1L, false, 50),
            AchievementDefinitionEntity("daily_stranger", "陌生善意", "幫助一位陌生人", "ic_achievement_photo_1", TriggerType.MANUAL_CONFIRM.value, 1L, false, 30),

            AchievementDefinitionEntity("epic_eclipse", "日月蝕", "親眼目睹日蝕或月蝕", "ic_achievement_photo_2", TriggerType.MANUAL_CONFIRM.value, 1L, true, 200),
            AchievementDefinitionEntity("epic_survive", "死裡逃生", "經歷一次生死關頭", "ic_achievement_photo_3", TriggerType.MANUAL_CONFIRM.value, 1L, true, 500),
            AchievementDefinitionEntity("epic_newborn", "新生命", "迎接新生兒", "ic_achievement_photo_2", TriggerType.MANUAL_CONFIRM.value, 1L, true, 500),
            AchievementDefinitionEntity("epic_northernlights", "極光奇景", "親眼目睹極光", "ic_achievement_photo_3", TriggerType.MANUAL_CONFIRM.value, 1L, true, 500),
            AchievementDefinitionEntity("epic_milkyway", "銀河之旅", "親眼看見銀河", "ic_achievement_photo_1", TriggerType.MANUAL_CONFIRM.value, 1L, true, 300),
            AchievementDefinitionEntity("epic_earthquake", "地牛翻身", "經歷一次地震", "ic_achievement_photo_2", TriggerType.MANUAL_CONFIRM.value, 1L, false, 100),
            AchievementDefinitionEntity("epic_double_rainbow", "雙彩虹", "親眼目睹雙彩虹", "ic_achievement_photo_3", TriggerType.MANUAL_CONFIRM.value, 1L, false, 200),
            AchievementDefinitionEntity("epic_meteor", "流星雨", "親眼目睹流星雨", "ic_achievement_photo_1", TriggerType.MANUAL_CONFIRM.value, 1L, false, 200),

            AchievementDefinitionEntity("health_marathon", "馬拉松跑者", "完成一場馬拉松", "ic_achievement_photo_2", TriggerType.MANUAL_CONFIRM.value, 1L, false, 200),
            AchievementDefinitionEntity("health_blood", "熱血助人", "捐血一次", "ic_achievement_photo_3", TriggerType.MANUAL_CONFIRM.value, 1L, false, 50),
            AchievementDefinitionEntity("health_10k", "十公里挑戰", "跑完 10 公里", "ic_achievement_photo_1", TriggerType.MANUAL_CONFIRM.value, 1L, false, 100),
            AchievementDefinitionEntity("health_no_sugar", "戒糖挑戰", "連續一週不吃糖", "ic_achievement_photo_2", TriggerType.MANUAL_CONFIRM.value, 1L, false, 75),
            AchievementDefinitionEntity("health_meditate", "靜心冥想", "連續 30 天冥想", "ic_achievement_photo_3", TriggerType.MANUAL_CONFIRM.value, 1L, false, 100),
            AchievementDefinitionEntity("health_gym", "健身新手", "開始去健身房", "ic_achievement_photo_1", TriggerType.MANUAL_CONFIRM.value, 1L, false, 30),

            AchievementDefinitionEntity("transport_license", "駕照到手", "考取駕照", "ic_achievement_photo_2", TriggerType.MANUAL_CONFIRM.value, 1L, false, 100),
            AchievementDefinitionEntity("transport_first_car", "第一台車", "購買第一輛車", "ic_achievement_photo_3", TriggerType.MANUAL_CONFIRM.value, 1L, false, 100),
            AchievementDefinitionEntity("transport_bike", "單車旅人", "騎單車通勤", "ic_achievement_photo_1", TriggerType.MANUAL_CONFIRM.value, 1L, false, 50),
            AchievementDefinitionEntity("transport_roadtrip", "公路旅行", "完成一次公路旅行", "ic_achievement_photo_2", TriggerType.MANUAL_CONFIRM.value, 1L, false, 100),
            AchievementDefinitionEntity("transport_no_accident", "安全駕駛", "一年無事故", "ic_achievement_photo_3", TriggerType.MANUAL_CONFIRM.value, 1L, false, 100)
        )
        definitionDao.insertAll(definitions)

        val userId = "local_user"
        val progressList = definitions.map { def ->
            UserAchievementProgressEntity(
                userId = userId,
                achievementId = def.achievementId,
                currentProgress = 0L,
                isUnlocked = false,
                unlockedDate = null,
                triggerType = def.triggerType
            )
        }
        progressDao.insertAll(progressList)
    }

    suspend fun recordCheckin(latitude: Double, longitude: Double, country: String, continent: String = ""): List<UnlockedAchievementEvent> {
        val userId = "local_user"
        val triggerType = TriggerType.LOCATION_CHECKIN_COUNT.value

        checkInRecordDao.insert(
            CheckInRecord(
                userId = userId,
                latitude = latitude,
                longitude = longitude,
                country = country,
                continent = continent,
                timestamp = System.currentTimeMillis()
            )
        )

        val uniqueCount = checkInRecordDao.countUniqueLocations(userId).toLong()
        progressDao.setProgressByType(userId, triggerType, uniqueCount)

        _totalCheckins.emit(uniqueCount)

        val events = mutableListOf<UnlockedAchievementEvent>()
        events.addAll(checkAndUnlock(userId, triggerType))

        if (country.isNotBlank()) {
            val countryCount = checkInRecordDao.countUniqueCountries(userId).toLong()
            events.addAll(autoTrackExploreCountry(countryCount))
            events.addAll(autoTrackSpecificCountry(country))
        }

        if (continent.isNotBlank()) {
            autoTrackSpecificContinent(continent).let { events.addAll(it) }
        }

        return events
    }

    private suspend fun autoTrackExploreCountry(countryCount: Long): List<UnlockedAchievementEvent> {
        val exploreIds = listOf("explore_5countries", "explore_10countries", "explore_50countries")
        val events = mutableListOf<UnlockedAchievementEvent>()

        for (id in exploreIds) {
            progressDao.setProgressById("local_user", id, countryCount)
            val updated = progressDao.getByUserAndAchievement("local_user", id) ?: continue
            val def = definitionDao.getById(id) ?: continue
            if (!updated.isUnlocked && updated.currentProgress >= def.triggerGoal) {
                progressDao.unlockAchievement("local_user", id, System.currentTimeMillis())
                val event = UnlockedAchievementEvent(def, System.currentTimeMillis())
                _unlockEvents.emit(event)
                events.add(event)
            }
        }

        val continentCount = checkInRecordDao.countUniqueContinents("local_user")
        val continentIds = listOf("explore_3continents", "explore_7continents")
        for (id in continentIds) {
            progressDao.setProgressById("local_user", id, continentCount.toLong())
            val updated = progressDao.getByUserAndAchievement("local_user", id) ?: continue
            val def = definitionDao.getById(id) ?: continue
            if (!updated.isUnlocked && updated.currentProgress >= def.triggerGoal) {
                progressDao.unlockAchievement("local_user", id, System.currentTimeMillis())
                val event = UnlockedAchievementEvent(def, System.currentTimeMillis())
                _unlockEvents.emit(event)
                events.add(event)
            }
        }

        return events
    }

    private suspend fun autoTrackSpecificCountry(country: String): List<UnlockedAchievementEvent> {
        val countryMap = mapOf(
            "Japan" to "explore_japan",
            "Australia" to "explore_australia"
        )
        val achievementId = countryMap[country] ?: return emptyList()
        val userId = "local_user"
        val progress = progressDao.getByUserAndAchievement(userId, achievementId) ?: return emptyList()
        if (progress.isUnlocked) return emptyList()

        progressDao.unlockAchievement(userId, achievementId, System.currentTimeMillis())
        val def = definitionDao.getById(achievementId) ?: return emptyList()
        val event = UnlockedAchievementEvent(def, System.currentTimeMillis())
        _unlockEvents.emit(event)
        return listOf(event)
    }

    private suspend fun autoTrackSpecificContinent(continent: String): List<UnlockedAchievementEvent> {
        val continentMap = mapOf(
            "Asia" to "explore_japan",
            "Europe" to "explore_europe",
            "Africa" to "explore_africa",
            "South America" to "explore_south_america",
            "Antarctica" to "explore_antarctica"
        )
        val achievementId = continentMap[continent] ?: return emptyList()
        val userId = "local_user"
        val progress = progressDao.getByUserAndAchievement(userId, achievementId) ?: return emptyList()
        if (progress.isUnlocked) return emptyList()

        progressDao.unlockAchievement(userId, achievementId, System.currentTimeMillis())
        val def = definitionDao.getById(achievementId) ?: return emptyList()
        val event = UnlockedAchievementEvent(def, System.currentTimeMillis())
        _unlockEvents.emit(event)
        return listOf(event)
    }

    suspend fun confirmManualAchievement(
        achievementId: String,
        photoPath: String? = null,
        labels: List<String>? = null
    ): UnlockedAchievementEvent? {
        val userId = "local_user"

        progressDao.incrementProgressById(userId, achievementId)

        if (photoPath != null) {
            evidenceDao.insert(
                AchievementEvidence(
                    achievementId = achievementId,
                    userId = userId,
                    photoPath = photoPath,
                    detectedLabels = labels?.joinToString(", ") ?: "",
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        val progress = progressDao.getByUserAndAchievement(userId, achievementId)
        val definition = definitionDao.getById(achievementId)

        if (progress != null && definition != null && progress.currentProgress >= definition.triggerGoal) {
            val now = System.currentTimeMillis()
            progressDao.unlockAchievement(userId, achievementId, now)
            val event = UnlockedAchievementEvent(
                achievement = definition,
                unlockedDate = now
            )
            _unlockEvents.emit(event)
            return event
        }

        return null
    }

    suspend fun getEvidence(achievementId: String): AchievementEvidence? {
        return evidenceDao.getByAchievement(achievementId, "local_user")
    }

    suspend fun getAllAchievementProgress(): List<UserAchievementProgressEntity> {
        return progressDao.getAllByUser("local_user")
    }

    suspend fun getAllDefinitions(): List<AchievementDefinitionEntity> {
        return definitionDao.getAll()
    }

    suspend fun getCheckinCount(): Int {
        return checkInRecordDao.countByUser("local_user")
    }

    suspend fun getUniqueLocationCount(): Int {
        return checkInRecordDao.countUniqueLocations("local_user")
    }

    private suspend fun checkAndUnlock(
        userId: String,
        triggerType: String
    ): List<UnlockedAchievementEvent> {
        val unlockedProgress = progressDao.getUnlockedByUserAndType(userId, triggerType)
        val events = mutableListOf<UnlockedAchievementEvent>()

        for (progress in unlockedProgress) {
            val definition = definitionDao.getById(progress.achievementId) ?: continue
            if (progress.currentProgress >= definition.triggerGoal) {
                val now = System.currentTimeMillis()
                progressDao.unlockAchievement(userId, progress.achievementId, now)

                val event = UnlockedAchievementEvent(
                    achievement = definition,
                    unlockedDate = now
                )
                events.add(event)
                _unlockEvents.emit(event)
            }
        }

        return events
    }

    suspend fun refreshTotalCheckins() {
        val count = checkInRecordDao.countUniqueLocations("local_user").toLong()
        _totalCheckins.emit(count)
    }

    suspend fun syncAutoTrackFromHistory() {
        try {
            val userId = "local_user"
            val countryCount = checkInRecordDao.countUniqueCountries(userId).toLong()
            val continentCount = checkInRecordDao.countUniqueContinents(userId).toLong()

            val countryIds = listOf("explore_5countries", "explore_10countries", "explore_50countries")
            val continentIds = listOf("explore_3continents", "explore_7continents")

            for (id in countryIds) {
                val def = definitionDao.getById(id) ?: continue
                progressDao.setProgressById(userId, id, countryCount)
                val updated = progressDao.getByUserAndAchievement(userId, id) ?: continue
                if (!updated.isUnlocked && updated.currentProgress >= def.triggerGoal) {
                    progressDao.unlockAchievement(userId, id, System.currentTimeMillis())
                    _unlockEvents.emit(UnlockedAchievementEvent(def, System.currentTimeMillis()))
                }
            }

            for (id in continentIds) {
                val def = definitionDao.getById(id) ?: continue
                progressDao.setProgressById(userId, id, continentCount)
                val updated = progressDao.getByUserAndAchievement(userId, id) ?: continue
                if (!updated.isUnlocked && updated.currentProgress >= def.triggerGoal) {
                    progressDao.unlockAchievement(userId, id, System.currentTimeMillis())
                    _unlockEvents.emit(UnlockedAchievementEvent(def, System.currentTimeMillis()))
                }
            }
        } catch (_: Exception) { }
    }
}
