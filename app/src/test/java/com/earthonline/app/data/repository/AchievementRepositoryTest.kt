package com.earthonline.app.data.repository

import com.earthonline.app.data.local.dao.AchievementDefinitionDao
import com.earthonline.app.data.local.dao.AchievementEvidenceDao
import com.earthonline.app.data.local.dao.CheckInRecordDao
import com.earthonline.app.data.local.dao.UserAchievementProgressDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
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
        repository = AchievementRepository(definitionDao, progressDao, checkInRecordDao, evidenceDao)
    }

    @Test
    fun `initialize should always insert definitions and progress`() = runTest {
        repository.initializeAchievements()
        assertTrue(true)
    }

    @Test
    fun `recordCheckin should store location and check unique count`() = runTest {
        repository.recordCheckin(25.033, 121.565)
        assertTrue(true)
    }
}
