# N-1: Screen Time 自動偵測 — 實作計畫

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 使用 `UsageStatsManager.queryEvents()` 自動偵測每日螢幕使用行為，將 `daily_earlybird`/`daily_allnighter`/`daily_no_phone` 改為 AUTO_TRACK。

**Architecture:** 新增 `ScreenTimeManager`（Hilt `@Singleton`），封裝 `queryEvents()` 查詢邏輯；`AchievementRepository` 注入後透過 `evaluateScreenTimeAchievements()` 回傳解鎖事件；`DashboardViewModel.loadAchievementDisplay()` 呼叫後一併處理。

**Tech Stack:** Kotlin, Android `UsageStatsManager` (`queryEvents`), Hilt DI, Room, MockK (測試)

---

## 檔案結構

| 檔案 | 操作 | 職責 |
|------|------|------|
| `app/src/main/AndroidManifest.xml` | 修改 | 新增 `PACKAGE_USAGE_STATS` 權限 |
| `app/src/main/java/com/earthonline/app/data/screentime/ScreenTimeManager.kt` | 🆕 | `queryEvents()` 封裝、3 個查詢方法、`evaluateAchievements()` |
| `app/src/main/java/com/earthonline/app/data/local/AchievementSeedData.kt` | 修改 | 3 成就 triggerType → AUTO_TRACK |
| `app/src/main/java/com/earthonline/app/data/repository/AchievementRepository.kt` | 修改 | 注入 `ScreenTimeManager`；+ `evaluateScreenTimeAchievements()` |
| `app/src/main/java/com/earthonline/app/ui/screens/dashboard/DashboardUiState.kt` | 修改 | + `screenTimeMinutes: Int` |
| `app/src/main/java/com/earthonline/app/ui/screens/dashboard/DashboardViewModel.kt` | 修改 | `loadAchievementDisplay()` 呼叫螢幕時間評估 |
| `app/src/test/java/com/earthonline/app/data/repository/ScreenTimeAchievementTest.kt` | 🆕 | 6 個測試案例（每成就觸發/不觸發各 1） |

---

### Task 1: 新增 AndroidManifest 權限

**Files:**
- Modify: `app/src/main/AndroidManifest.xml:9`

- [ ] **Step 1: 新增 PACKAGE_USAGE_STATS 權限宣告**

在 `<uses-permission android:name="com.google.android.gms.permission.ACTIVITY_RECOGNITION" />` 之後插入：

```xml
    <uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" />
```

**完整修改後的權限區塊：**
```xml
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

    <uses-permission android:name="com.google.android.gms.permission.ACTIVITY_RECOGNITION" />
    <uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" />
```

- [ ] **Step 2: 寫入日誌**

記錄至 `code_change_log.md`。

- [ ] **Step 3: Commit**

```bash
git add app/src/main/AndroidManifest.xml code_change_log.md
git commit -m "feat(n1): add PACKAGE_USAGE_STATS permission to AndroidManifest"
```

---

### Task 2: 3 成就 triggerType → AUTO_TRACK

**Files:**
- Modify: `app/src/main/java/com/earthonline/app/data/local/AchievementSeedData.kt:77,80,83`

- [ ] **Step 1: 修改 daily_earlybird（第 77 行）**

```kotlin
// 修改前
AchievementDefinitionEntity("daily_earlybird", "早起的鳥兒", "清晨 5 點前起床", "ic_achievement_photo_3", TriggerType.MANUAL_CONFIRM.value, 1L, false, 30, strengthWeight = 0.2f, agilityWeight = 0f, intelligenceWeight = 0.3f, charismaWeight = 0f, vitalityWeight = 0.5f),

// 修改後
AchievementDefinitionEntity("daily_earlybird", "早起的鳥兒", "清晨 5 點前起床", "ic_achievement_photo_3", TriggerType.AUTO_TRACK.value, 1L, false, 30, strengthWeight = 0.2f, agilityWeight = 0f, intelligenceWeight = 0.3f, charismaWeight = 0f, vitalityWeight = 0.5f),
```

- [ ] **Step 2: 修改 daily_allnighter（第 80 行）**

```kotlin
// 修改前
AchievementDefinitionEntity("daily_allnighter", "徹夜未眠", "通宵一次", "ic_achievement_photo_3", TriggerType.MANUAL_CONFIRM.value, 1L, false, 30, strengthWeight = 0.2f, agilityWeight = 0f, intelligenceWeight = 0.3f, charismaWeight = 0f, vitalityWeight = 0.5f),

// 修改後
AchievementDefinitionEntity("daily_allnighter", "徹夜未眠", "通宵一次", "ic_achievement_photo_3", TriggerType.AUTO_TRACK.value, 1L, false, 30, strengthWeight = 0.2f, agilityWeight = 0f, intelligenceWeight = 0.3f, charismaWeight = 0f, vitalityWeight = 0.5f),
```

- [ ] **Step 3: 修改 daily_no_phone（第 83 行）**

```kotlin
// 修改前
AchievementDefinitionEntity("daily_no_phone", "數位排毒", "一整天不用手機", "ic_achievement_photo_3", TriggerType.MANUAL_CONFIRM.value, 1L, false, 50, strengthWeight = 0f, agilityWeight = 0f, intelligenceWeight = 0.5f, charismaWeight = 0.3f, vitalityWeight = 0.2f),

// 修改後
AchievementDefinitionEntity("daily_no_phone", "數位排毒", "一整天不用手機", "ic_achievement_photo_3", TriggerType.AUTO_TRACK.value, 1L, false, 50, strengthWeight = 0f, agilityWeight = 0f, intelligenceWeight = 0.5f, charismaWeight = 0.3f, vitalityWeight = 0.2f),
```

- [ ] **Step 4: 寫入日誌**

記錄至 `code_change_log.md`。

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/earthonline/app/data/local/AchievementSeedData.kt code_change_log.md
git commit -m "feat(n1): change screen time achievements from MANUAL_CONFIRM to AUTO_TRACK"
```

---

### Task 3: 🆕 建立 ScreenTimeManager

**Files:**
- Create: `app/src/main/java/com/earthonline/app/data/screentime/ScreenTimeManager.kt`

- [ ] **Step 1: 建立檔案**

```kotlin
package com.earthonline.app.data.screentime

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScreenTimeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "ScreenTimeManager"
        private const val MINUTES_NIGHT_OWL = 30L
        private const val EARLY_HOUR = 5
        private const val NIGHT_OWL_START = 2
    }

    fun isUsageStatsPermissionGranted(): Boolean {
        return try {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as android.app.AppOpsManager
            val mode = appOps.checkOpNoThrow(
                android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
            mode == android.app.AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            Log.e(TAG, "isUsageStatsPermissionGranted failed", e)
            false
        }
    }

    fun openUsageAccessSettings() {
        try {
            context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        } catch (e: Exception) {
            Log.e(TAG, "openUsageAccessSettings failed", e)
        }
    }

    suspend fun evaluateAchievements(): List<String> {
        if (!isUsageStatsPermissionGranted()) return emptyList()
        return withContext(Dispatchers.IO) {
            val result = mutableListOf<String>()
            if (isEarlyBird()) result.add("daily_earlybird")
            if (isNightOwl()) result.add("daily_allnighter")
            if (hasNoPhoneToday()) result.add("daily_no_phone")
            result
        }
    }

    private fun getStartOfToday(): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    private fun getEarlyBirdThreshold(): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, EARLY_HOUR)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    private fun getNightOwlStart(): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, NIGHT_OWL_START)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    private fun isEarlyBird(): Boolean {
        return try {
            val todayStart = getStartOfToday()
            val now = System.currentTimeMillis()
            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val events = usageStatsManager.queryEvents(todayStart, now) ?: return false
            val event = UsageEvents.Event()

            var earliestKeyguardHidden: Long = Long.MAX_VALUE
            var earliestForeground: Long = Long.MAX_VALUE

            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                    && event.eventType == UsageEvents.Event.KEYGUARD_HIDDEN
                ) {
                    if (event.timeStamp < earliestKeyguardHidden) {
                        earliestKeyguardHidden = event.timeStamp
                    }
                }
                if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    if (event.timeStamp < earliestForeground) {
                        earliestForeground = event.timeStamp
                    }
                }
            }

            val earliest = if (earliestKeyguardHidden != Long.MAX_VALUE) earliestKeyguardHidden else earliestForeground
            val threshold = getEarlyBirdThreshold()
            earliest != Long.MAX_VALUE && earliest < threshold
        } catch (e: Exception) {
            Log.e(TAG, "isEarlyBird failed", e)
            false
        }
    }

    private fun isNightOwl(): Boolean {
        return try {
            val todayStart = getStartOfToday()
            val now = System.currentTimeMillis()
            val nightStart = getNightOwlStart()
            val threshold = getEarlyBirdThreshold()

            if (now < threshold) {
                false
            } else {
                val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                val events = usageStatsManager.queryEvents(todayStart, now) ?: return false
                val event = UsageEvents.Event()

                var totalMillis = 0L
                var lastForegroundTime: Long? = null

                while (events.hasNextEvent()) {
                    events.getNextEvent(event)
                    if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                        lastForegroundTime = event.timeStamp
                    }
                    if (event.eventType == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                        if (lastForegroundTime != null) {
                            val duration = event.timeStamp - lastForegroundTime
                            if (lastForegroundTime >= nightStart) {
                                totalMillis += duration
                            }
                            lastForegroundTime = null
                        }
                    }
                }

                if (lastForegroundTime != null && lastForegroundTime >= nightStart) {
                    totalMillis += now - lastForegroundTime
                }

                (totalMillis / 60_000) >= MINUTES_NIGHT_OWL
            }
        } catch (e: Exception) {
            Log.e(TAG, "isNightOwl failed", e)
            false
        }
    }

    private fun hasNoPhoneToday(): Boolean {
        return try {
            val oneDayAgo = System.currentTimeMillis() - 24 * 60 * 60 * 1000
            val now = System.currentTimeMillis()
            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val events = usageStatsManager.queryEvents(oneDayAgo, now) ?: return false
            val event = UsageEvents.Event()

            var hasActivity = false
            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    hasActivity = true
                    break
                }
            }
            !hasActivity
        } catch (e: Exception) {
            Log.e(TAG, "hasNoPhoneToday failed", e)
            false
        }
    }
}
```

- [ ] **Step 2: 寫入日誌**

記錄至 `code_change_log.md`。

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/earthonline/app/data/screentime/ScreenTimeManager.kt code_change_log.md
git commit -m "feat(n1): add ScreenTimeManager for UsageStats queryEvents"
```

---

### Task 4: AchievementRepository 注入 ScreenTimeManager

**Files:**
- Modify: `app/src/main/java/com/earthonline/app/data/repository/AchievementRepository.kt`

- [ ] **Step 1: 在建構子中注入 ScreenTimeManager**

將第 37-45 行的建構子修改為：

```kotlin
@Singleton
class AchievementRepository @Inject constructor(
    private val definitionDao: AchievementDefinitionDao,
    private val progressDao: UserAchievementProgressDao,
    private val checkInRecordDao: CheckInRecordDao,
    private val evidenceDao: AchievementEvidenceDao,
    private val petDao: PetDao,
    private val activityRecognitionManager: ActivityRecognitionManager,
    private val screenTimeManager: ScreenTimeManager
) {
```

並在檔案開頭加入 import：

```kotlin
import com.earthonline.app.data.screentime.ScreenTimeManager
```

- [ ] **Step 2: 新增 evaluateScreenTimeAchievements() 方法**

在檔案中新增方法（例如在 `evaluateActivityAchievements()` 方法之後，約第 363 行）：

```kotlin
    suspend fun evaluateScreenTimeAchievements(): List<UnlockedAchievementEvent> {
        val achievementIds = screenTimeManager.evaluateAchievements()
        if (achievementIds.isEmpty()) return emptyList()

        val userId = AppConstants.LOCAL_USER_ID
        val now = System.currentTimeMillis()
        val events = mutableListOf<UnlockedAchievementEvent>()

        for (id in achievementIds) {
            tryAutoUnlock(userId, id, now)?.let { events.add(it) }
        }
        return events
    }
```

- [ ] **Step 3: 寫入日誌**

記錄至 `code_change_log.md`。

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/earthonline/app/data/repository/AchievementRepository.kt code_change_log.md
git commit -m "feat(n1): add evaluateScreenTimeAchievements to AchievementRepository"
```

---

### Task 5: DashboardUiState 新增 screenTimeMinutes

**Files:**
- Modify: `app/src/main/java/com/earthonline/app/ui/screens/dashboard/DashboardUiState.kt:52`

- [ ] **Step 1: 在 activityPermissionGranted 之後加入新欄位**

在第 52 行 `val activityPermissionGranted: Boolean = false` 之後新增：

```kotlin
    val screenTimeMinutes: Int = 0,                        // 今日螢幕使用時間（分鐘）
```

修改後的 UiState 末尾：

```kotlin
    val bikingKm: Int = 0,                                 // 騎行公里數
    val showActivityPermissionDialog: Boolean = false,     // 是否顯示活動權限對話框
    val activityPermissionGranted: Boolean = false,        // 活動辨識權限是否已授予
    val screenTimeMinutes: Int = 0,                        // 今日螢幕使用時間（分鐘）
)
```

- [ ] **Step 2: 寫入日誌**

記錄至 `code_change_log.md`。

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/earthonline/app/ui/screens/dashboard/DashboardUiState.kt code_change_log.md
git commit -m "feat(n1): add screenTimeMinutes to DashboardUiState"
```

---

### Task 6: DashboardViewModel 整合螢幕時間評估

**Files:**
- Modify: `app/src/main/java/com/earthonline/app/ui/screens/dashboard/DashboardViewModel.kt:224-262`

- [ ] **Step 1: 在 loadAchievementDisplay() 中呼叫 evaluateScreenTimeAchievements()**

在 `loadAchievementDisplay()` 方法中，於 `evaluateActivityAchievements()` 之後加入螢幕時間評估。修改區段（第 224-262 行）：

```kotlin
    private suspend fun loadAchievementDisplay() {
        repository.syncAutoTrackFromHistory()
        repository.refreshAll()
        val (activity, activityEvents) = repository.evaluateActivityAchievements()
        handleUnlockEvents(activityEvents)
        val screenTimeEvents = repository.evaluateScreenTimeAchievements()
        handleUnlockEvents(screenTimeEvents)
        val definitions = repository.getAllDefinitions()
        val allProgress = repository.getAllAchievementProgress()
        val displayItems = AchievementDisplayMapper.map(definitions, allProgress)
        val totalCheckins = repository.getCheckinCount().toLong()
        val totalPoints = repository.getTotalPoints()
        val unlockedCount = repository.getUnlockedCount()
        val level = repository.computePlayerLevel(totalPoints)
        val progress = repository.computeLevelProgress(totalPoints)
        val xpNext = repository.computeXpToNext(totalPoints)
        repository.computeAndSavePetStats()
        val petEntity = repository.getPet()
        val pet = repository.petToUiState(petEntity)

        _uiState.update {
            it.copy(
                totalCheckins = totalCheckins,
                totalPoints = totalPoints,
                unlockedCount = unlockedCount,
                playerLevel = level,
                levelProgress = progress,
                xpToNext = xpNext,
                achievements = displayItems,
                pet = pet,
                isLoading = false,
                walkingMinutes = activity.first,
                bikingMinutes = activity.second,
                bikingKm = activity.third,
                activityPermissionGranted = androidx.core.content.ContextCompat.checkSelfPermission(
                    context, "com.google.android.gms.permission.ACTIVITY_RECOGNITION"
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED,
                showActivityPermissionDialog = !settingsManager.activityTrackingEnabled
                    || (!isActivityPermissionGranted() && !isActivityPermissionRequested())
            )
        }
    }
```

- [ ] **Step 2: 寫入日誌**

記錄至 `code_change_log.md`。

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/earthonline/app/ui/screens/dashboard/DashboardViewModel.kt code_change_log.md
git commit -m "feat(n1): integrate evaluateScreenTimeAchievements into DashboardViewModel"
```

---

### Task 7: 🆕 單元測試 evaluateScreenTimeAchievements

**Files:**
- Create: `app/src/test/java/com/earthonline/app/data/repository/ScreenTimeAchievementTest.kt`

- [ ] **Step 1: 建立測試檔案**

```kotlin
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
        rewardPoints: Long = 30
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

    // daily_earlybird：ScreenTimeManager 回傳 daily_earlybird → 應解鎖
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

    // daily_allnighter：ScreenTimeManager 回傳 daily_allnighter → 應解鎖
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

    // daily_no_phone：ScreenTimeManager 回傳 daily_no_phone → 應解鎖
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

    // 無偵測 → 不解鎖任何成就
    @Test
    fun `evaluateScreenTimeAchievements should NOT unlock when no achievements detected`() = runTest {
        coEvery { screenTimeManager.evaluateAchievements() } returns emptyList()

        repository.evaluateScreenTimeAchievements()

        coVerify(exactly = 0) { progressDao.unlockAchievement(any(), any(), any()) }
    }

    // 已解鎖成就 → 不重複解鎖
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

    // 權限未授予 → 回傳空列表，不崩潰
    @Test
    fun `evaluateScreenTimeAchievements should handle empty result gracefully`() = runTest {
        coEvery { screenTimeManager.evaluateAchievements() } returns listOf("daily_earlybird")
        coEvery { definitionDao.getById("daily_earlybird") } returns null

        val result = repository.evaluateScreenTimeAchievements()

        assertTrue(result.isEmpty())
    }
}
```

- [ ] **Step 2: 執行測試確保通過**

```bash
cd "app"; ./gradlew testDebugUnitTest --tests "com.earthonline.app.data.repository.ScreenTimeAchievementTest" -q
```

預期：所有 6 個測試 PASS。

- [ ] **Step 3: 寫入日誌**

記錄至 `code_change_log.md`。

- [ ] **Step 4: Commit**

```bash
git add app/src/test/java/com/earthonline/app/data/repository/ScreenTimeAchievementTest.kt code_change_log.md
git commit -m "test(n1): add unit tests for evaluateScreenTimeAchievements"
```

---

### Task 8: 更新 code_change_log.md

**Files:**
- Modify: `code_change_log.md`

在 Task 1–7 執行過程中，每步驟都已寫入日誌，此 Task 為最後確認日誌完整性。

- [ ] **Step 1: 確認所有 7 筆日誌已寫入**

```bash
git log --oneline -7
```

確認本次分支包含 Task 1–7 的 7 個 commit。

- [ ] **Step 2: 最終確認**

執行完整測試：

```bash
cd "app"; ./gradlew testDebugUnitTest -q
```

預期：所有測試 PASS。

---

## 自檢

| 檢查項 | 結果 |
|------|------|
| **Spec 覆蓋** | Task 1 對應權限、Task 2 對應成就類型、Task 3 對應 ScreenTimeManager、Task 4 對應 evaluateScreenTimeAchievements、Task 5 對應 UiState、Task 6 對應 ViewModel、Task 7 對應測試 ✅ |
| **Placeholder 掃描** | 無 TBD/TODO/「implement later」/「add error handling」等模糊步驟 ✅ |
| **型別一致性** | `ScreenTimeManager.evaluateAchievements()` 回傳 `List<String>`，`AchievementRepository.evaluateScreenTimeAchievements()` 回傳 `List<UnlockedAchievementEvent>`，DashboardViewModel 中變數名 `screenTimeEvents` 一致 ✅ |
| **建構子一致性** | `AchievementRepository` 建構子在 Task 4 加入 `screenTimeManager`，Task 7 測試建構子同樣包含此參數 ✅ |
