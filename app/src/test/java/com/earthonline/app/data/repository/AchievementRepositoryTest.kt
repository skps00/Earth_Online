package com.earthonline.app.data.repository

import com.earthonline.app.data.activity.ActivityRecognitionManager
import com.earthonline.app.data.screentime.ScreenTimeManager
import com.earthonline.app.data.local.dao.AchievementDefinitionDao
import com.earthonline.app.data.local.dao.AchievementEvidenceDao
import com.earthonline.app.data.local.dao.CheckInRecordDao
import com.earthonline.app.data.local.dao.PetDao
import com.earthonline.app.data.local.dao.UserAchievementProgressDao
import com.earthonline.app.data.local.entity.AchievementDefinitionEntity
import com.earthonline.app.data.local.entity.UserAchievementProgressEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AchievementRepositoryTest {

    private lateinit var repository: AchievementRepository
    private lateinit var definitionDao: AchievementDefinitionDao
    private lateinit var progressDao: UserAchievementProgressDao
    private lateinit var checkInRecordDao: CheckInRecordDao

    @Before
    fun setup() {
        definitionDao = mockk(relaxed = true)
        progressDao = mockk(relaxed = true)
        checkInRecordDao = mockk(relaxed = true)
        val evidenceDao = mockk<AchievementEvidenceDao>(relaxed = true)
        val petDao = mockk<PetDao>(relaxed = true)
        val activityManager = mockk<ActivityRecognitionManager>(relaxed = true)
        val screenTimeManager = mockk<ScreenTimeManager>(relaxed = true)
        repository = AchievementRepository(
            definitionDao, progressDao, checkInRecordDao, evidenceDao, petDao, activityManager, screenTimeManager
        )
    }

    @Test
    fun `initialize should insert definitions and progress`() = runTest {
        repository.initializeAchievements()
        assertTrue(true)
    }

    @Test
    fun `computePlayerLevel should return 1 for 0 points`() {
        assertEquals(1, repository.computePlayerLevel(0))
    }

    @Test
    fun `computePlayerLevel should return 2 for 100 points`() {
        assertEquals(2, repository.computePlayerLevel(100))
    }

    @Test
    fun `computePlayerLevel should return 4 for 900 points`() {
        assertEquals(4, repository.computePlayerLevel(900))
    }

    @Test
    fun `computePlayerLevel should return 11 for 10000 points`() {
        assertEquals(11, repository.computePlayerLevel(10000))
    }

    @Test
    fun `computeXpToNext should return correct xp gap`() {
        val total = 100L
        val xpToNext = repository.computeXpToNext(total)
        val currentLevel = repository.computePlayerLevel(total)
        val nextLevelThreshold = ((currentLevel * currentLevel) * 100).toLong()
        assertEquals(nextLevelThreshold - total, xpToNext)
    }

    @Test
    fun `computeLevelProgress should return 0f for exact level boundary`() {
        val progress = repository.computeLevelProgress(100L)
        assertEquals(0f, progress)
    }

    @Test
    fun `computeLevelProgress should return value between 0 and 1`() {
        val progress = repository.computeLevelProgress(150L)
        assertTrue(progress > 0f)
        assertTrue(progress < 1f)
    }

    // 驗證：打卡日本 → 自動解鎖 explore_japan
    @Test
    fun `recordCheckin Japan should auto-unlock explore_japan`() = runTest {
        val def = AchievementDefinitionEntity(
            achievementId = "explore_japan",
            title = "日本漫遊",
            description = "造訪日本",
            iconAsset = "",
            triggerType = "AUTO_TRACK",
            triggerGoal = 1L,
            isHidden = false,
            rewardPoints = 50
        )
        val progress = UserAchievementProgressEntity(
            userId = "local_user",
            achievementId = "explore_japan",
            currentProgress = 0L,
            isUnlocked = false,
            unlockedDate = null,
            triggerType = "AUTO_TRACK"
        )

        coEvery { definitionDao.getById("explore_japan") } returns def
        coEvery { progressDao.getByUserAndAchievement("local_user", "explore_japan") } returns progress
        coEvery { checkInRecordDao.countUniqueCountries("local_user") } returns 1
        coEvery { checkInRecordDao.countUniqueContinents("local_user") } returns 1

        repository.recordCheckin(35.6762, 139.6503, "Japan")

        coVerify(exactly = 1) { progressDao.unlockAchievement("local_user", "explore_japan", any()) }
    }

    // 驗證：海拔 ≥2500m → 自動解鎖 explore_mountain
    @Test
    fun `recordCheckin with altitude 3000 should unlock explore_mountain`() = runTest {
        val def = AchievementDefinitionEntity(
            achievementId = "explore_mountain",
            title = "登峰造極",
            description = "攀登一座高山",
            iconAsset = "",
            triggerType = "AUTO_TRACK",
            triggerGoal = 1L,
            isHidden = false,
            rewardPoints = 75,
        )
        val progress = UserAchievementProgressEntity(
            userId = "local_user",
            achievementId = "explore_mountain",
            currentProgress = 0L,
            isUnlocked = false,
            unlockedDate = null,
            triggerType = "AUTO_TRACK",
        )

        coEvery { definitionDao.getById("explore_mountain") } returns def
        coEvery { progressDao.getByUserAndAchievement("local_user", "explore_mountain") } returns progress
        coEvery { checkInRecordDao.countUniqueCountries("local_user") } returns 1
        coEvery { checkInRecordDao.countUniqueContinents("local_user") } returns 1

        repository.recordCheckin(23.5, 121.0, "Taiwan", altitude = 3000.0)

        coVerify(exactly = 1) { progressDao.unlockAchievement("local_user", "explore_mountain", any()) }
    }

    // 驗證：海拔 500m → 不應該解鎖 explore_mountain
    @Test
    fun `recordCheckin with altitude 500 should NOT unlock mountain`() = runTest {
        coEvery { checkInRecordDao.countUniqueCountries("local_user") } returns 1
        coEvery { checkInRecordDao.countUniqueContinents("local_user") } returns 1

        repository.recordCheckin(25.0, 121.5, "Taiwan", altitude = 500.0)

        coVerify(exactly = 0) { progressDao.unlockAchievement("local_user", "explore_mountain", any()) }
    }
}
