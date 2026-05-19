package com.earthonline.app.data.repository

import com.earthonline.app.data.local.dao.AchievementDefinitionDao
import com.earthonline.app.data.local.dao.UserAchievementProgressDao
import com.earthonline.app.data.local.entity.AchievementDefinitionEntity
import com.earthonline.app.data.local.entity.UserAchievementProgressEntity
import com.earthonline.app.domain.model.TriggerType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AchievementRepositoryTest {

    private lateinit var definitionDao: AchievementDefinitionDao
    private lateinit var progressDao: UserAchievementProgressDao
    private lateinit var repository: AchievementRepository

    private val userId = "local_user"
    private val photoType = TriggerType.PHOTO_UPLOAD_COUNT.value

    @Before
    fun setup() {
        definitionDao = mockk(relaxed = true)
        progressDao = mockk(relaxed = true)
        repository = AchievementRepository(definitionDao, progressDao)
    }

    @Test
    fun `initialize should insert photo definitions when empty`() = runTest {
        coEvery { definitionDao.count() } returns 0

        repository.initializeAchievements()

        coVerify(exactly = 1) { definitionDao.insertAll(any()) }
        coVerify(exactly = 1) { progressDao.insertAll(any()) }
    }

    @Test
    fun `initialize should skip when definitions already exist`() = runTest {
        coEvery { definitionDao.count() } returns 3

        repository.initializeAchievements()

        coVerify(exactly = 0) { definitionDao.insertAll(any()) }
        coVerify(exactly = 0) { progressDao.insertAll(any()) }
    }

    @Test
    fun `recordPhoto should unlock photo_1 achievement`() = runTest {
        val photoDefinition = AchievementDefinitionEntity(
            achievementId = "photo_1",
            title = "初嚐記錄",
            description = "拍攝 1 次餐點",
            iconAsset = "ic_photo",
            triggerType = photoType,
            triggerGoal = 1L
        )

        val progress = UserAchievementProgressEntity(
            userId = userId,
            achievementId = "photo_1",
            currentProgress = 1L,
            isUnlocked = false,
            triggerType = photoType
        )

        coEvery { progressDao.countByUserAndType(userId, photoType) } returns 1
        coEvery { definitionDao.getById("photo_1") } returns photoDefinition
        coEvery { progressDao.getUnlockedByUserAndType(userId, photoType) } returns listOf(progress)

        val events = repository.recordPhoto()

        coVerify { progressDao.incrementProgress(userId, photoType, 1L) }
        coVerify { progressDao.unlockAchievement(userId, "photo_1", any()) }

        assertEquals(1, events.size)
        assertEquals("photo_1", events[0].achievement.achievementId)
    }

    @Test
    fun `recordPhoto should not unlock if below goal`() = runTest {
        val photoDefinition = AchievementDefinitionEntity(
            achievementId = "photo_10",
            title = "美食獵人",
            description = "拍攝 10 次餐點",
            iconAsset = "ic_photo",
            triggerType = photoType,
            triggerGoal = 10L
        )

        val progress = UserAchievementProgressEntity(
            userId = userId,
            achievementId = "photo_10",
            currentProgress = 3L,
            isUnlocked = false,
            triggerType = photoType
        )

        coEvery { progressDao.countByUserAndType(userId, photoType) } returns 3
        coEvery { definitionDao.getById("photo_10") } returns photoDefinition
        coEvery { progressDao.getUnlockedByUserAndType(userId, photoType) } returns listOf(progress)

        val events = repository.recordPhoto()

        coVerify(exactly = 0) { progressDao.unlockAchievement(any(), any(), any()) }
        assertTrue(events.isEmpty())
    }
}
