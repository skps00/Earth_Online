package com.earthonline.app.data.repository

import com.earthonline.app.data.activity.ActivityRecognitionManager
import com.earthonline.app.data.local.dao.AchievementDefinitionDao
import com.earthonline.app.data.local.dao.AchievementEvidenceDao
import com.earthonline.app.data.local.dao.CheckInRecordDao
import com.earthonline.app.data.local.dao.PetDao
import com.earthonline.app.data.local.dao.UserAchievementProgressDao
import com.earthonline.app.data.local.entity.AchievementDefinitionEntity
import com.earthonline.app.data.local.entity.UserAchievementProgressEntity
import com.earthonline.app.data.screentime.ScreenTimeManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ScreenTimeAchievementTest {

    private lateinit var repository: AchievementRepository
    private lateinit var definitionDao: AchievementDefinitionDao
    private lateinit var progressDao: UserAchievementProgressDao
    private lateinit var screenTimeManager: ScreenTimeManager

    @Before
    fun setup() {
        definitionDao = mockk(relaxed = true)
        progressDao = mockk(relaxed = true)
        val checkInRecordDao = mockk<CheckInRecordDao>(relaxed = true)
        val evidenceDao = mockk<AchievementEvidenceDao>(relaxed = true)
        val petDao = mockk<PetDao>(relaxed = true)
        val activityManager = mockk<ActivityRecognitionManager>(relaxed = true)
        screenTimeManager = mockk(relaxed = true)
        repository = AchievementRepository(
            definitionDao, progressDao, checkInRecordDao, evidenceDao, petDao, activityManager, screenTimeManager
        )
    }

    private fun mockAchievement(
        achievementId: String,
        title: String,
        description: String,
        rewardPoints: Int = 30
    ): AchievementDefinitionEntity {
        return AchievementDefinitionEntity(
            achievementId = achievementId,
            title = title,
            description = description,
            iconAsset = "",
            triggerType = "AUTO_TRACK",
            triggerGoal = 1L,
            isHidden = false,
            rewardPoints = rewardPoints
        )
    }

    private fun mockProgress(achievementId: String, isUnlocked: Boolean = false): UserAchievementProgressEntity {
        return UserAchievementProgressEntity(
            userId = "local_user",
            achievementId = achievementId,
            currentProgress = 0L,
            isUnlocked = isUnlocked,
            unlockedDate = null,
            triggerType = "AUTO_TRACK"
        )
    }

    @Test
    fun `evaluateScreenTimeAchievements should unlock daily_earlybird when early bird detected`() = runTest {
        val def = mockAchievement("daily_earlybird", "早起的鳥兒", "清晨 5 點前起床")
        val prog = mockProgress("daily_earlybird", isUnlocked = false)
        coEvery { definitionDao.getById("daily_earlybird") } returns def
        coEvery { progressDao.getByUserAndAchievement("local_user", "daily_earlybird") } returns prog
        coEvery { screenTimeManager.evaluateAchievements() } returns listOf("daily_earlybird")

        repository.evaluateScreenTimeAchievements()

        coVerify(exactly = 1) { progressDao.unlockAchievement("local_user", "daily_earlybird", any()) }
    }

    @Test
    fun `evaluateScreenTimeAchievements should unlock daily_allnighter when night owl detected`() = runTest {
        val def = mockAchievement("daily_allnighter", "徹夜未眠", "通宵一次")
        val prog = mockProgress("daily_allnighter", isUnlocked = false)
        coEvery { definitionDao.getById("daily_allnighter") } returns def
        coEvery { progressDao.getByUserAndAchievement("local_user", "daily_allnighter") } returns prog
        coEvery { screenTimeManager.evaluateAchievements() } returns listOf("daily_allnighter")

        repository.evaluateScreenTimeAchievements()

        coVerify(exactly = 1) { progressDao.unlockAchievement("local_user", "daily_allnighter", any()) }
    }

    @Test
    fun `evaluateScreenTimeAchievements should unlock daily_no_phone when no phone activity detected`() = runTest {
        val def = mockAchievement("daily_no_phone", "數位排毒", "一整天不用手機", rewardPoints = 50)
        val prog = mockProgress("daily_no_phone", isUnlocked = false)
        coEvery { definitionDao.getById("daily_no_phone") } returns def
        coEvery { progressDao.getByUserAndAchievement("local_user", "daily_no_phone") } returns prog
        coEvery { screenTimeManager.evaluateAchievements() } returns listOf("daily_no_phone")

        repository.evaluateScreenTimeAchievements()

        coVerify(exactly = 1) { progressDao.unlockAchievement("local_user", "daily_no_phone", any()) }
    }

    @Test
    fun `evaluateScreenTimeAchievements should NOT unlock when no achievements detected`() = runTest {
        coEvery { screenTimeManager.evaluateAchievements() } returns emptyList()

        repository.evaluateScreenTimeAchievements()

        coVerify(exactly = 0) { progressDao.unlockAchievement(any(), any(), any()) }
    }

    @Test
    fun `evaluateScreenTimeAchievements should NOT re-unlock already unlocked achievement`() = runTest {
        val def = mockAchievement("daily_earlybird", "早起的鳥兒", "清晨 5 點前起床")
        val prog = mockProgress("daily_earlybird", isUnlocked = true)
        coEvery { definitionDao.getById("daily_earlybird") } returns def
        coEvery { progressDao.getByUserAndAchievement("local_user", "daily_earlybird") } returns prog
        coEvery { screenTimeManager.evaluateAchievements() } returns listOf("daily_earlybird")

        repository.evaluateScreenTimeAchievements()

        coVerify(exactly = 0) { progressDao.unlockAchievement("local_user", "daily_earlybird", any()) }
    }

    @Test
    fun `evaluateScreenTimeAchievements should handle empty result gracefully`() = runTest {
        coEvery { screenTimeManager.evaluateAchievements() } returns listOf("daily_earlybird")
        coEvery { definitionDao.getById("daily_earlybird") } returns null

        val result = repository.evaluateScreenTimeAchievements()

        assertTrue(result.isEmpty())
    }
}
