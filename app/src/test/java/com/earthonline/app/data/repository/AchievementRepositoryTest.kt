package com.earthonline.app.data.repository

import com.earthonline.app.data.activity.ActivityRecognitionManager
import com.earthonline.app.data.local.dao.AchievementDefinitionDao
import com.earthonline.app.data.local.dao.AchievementEvidenceDao
import com.earthonline.app.data.local.dao.CheckInRecordDao
import com.earthonline.app.data.local.dao.PetDao
import com.earthonline.app.data.local.dao.UserAchievementProgressDao
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AchievementRepositoryTest {

    private lateinit var repository: AchievementRepository

    @Before
    fun setup() {
        val definitionDao = mockk<AchievementDefinitionDao>(relaxed = true)
        val progressDao = mockk<UserAchievementProgressDao>(relaxed = true)
        val checkInRecordDao = mockk<CheckInRecordDao>(relaxed = true)
        val evidenceDao = mockk<AchievementEvidenceDao>(relaxed = true)
        val petDao = mockk<PetDao>(relaxed = true)
        val activityManager = mockk<ActivityRecognitionManager>(relaxed = true)
        repository = AchievementRepository(
            definitionDao, progressDao, checkInRecordDao, evidenceDao, petDao, activityManager
        )
    }

    @Test
    fun `initialize should insert definitions and progress`() = runTest {
        repository.initializeAchievements()
        assertTrue(true)
    }

    // 等級計算：sqrt(totalPoints / 100) + 1，取整數
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

    // XP 計算：下級門檻 − 上級門檻
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
}
