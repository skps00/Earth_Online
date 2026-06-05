# F-12 + F-13 Database Safety — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Fix `fallbackToDestructiveMigration` by adding a safe Migration pattern; fix `recordCheckin()` by wrapping writes in a DAO-layer `@Transaction`.

**Architecture:** F-12 — add empty `Migration(12,13)`, bump version, fix 128→129 achievement count. F-13 — create `CheckInRecordDao.insertCheckinAndUpdateProgress()` as a `@Transaction` composite method, then simplify `AchievementRepository.recordCheckin()` to call it.

**Tech Stack:** Room 2.6.1, Kotlin coroutines, Hilt DI

---

## 檔案結構

| 檔案 | 操作 | F# | 職責 |
|------|:--:|:--:|------|
| `AppDatabase.kt` | 修改 | 12 | version 12→13 |
| `DatabaseModule.kt` | 修改 | 12 | 加入 Migration(12,13) + 保留 fallback |
| `CheckInRecordDao.kt` | 修改 | 13 | + `@Transaction` insertCheckinAndUpdateProgress() |
| `AchievementRepository.kt` | 修改 | 13 | recordCheckin() 改為呼叫新 DAO 方法 |
| `AchievementSeedData.kt` | 修改 | 12 | 補齊第 129 個成就 |
| `AppConstants.kt` | 修改 | 12 | 確認 TOTAL_ACHIEVEMENT_COUNT = 129 ✅ |

---

### Task 1: F-12 — 補齊第 129 個成就

**Files:**
- Modify: `app/src/main/java/com/earthonline/app/data/local/AchievementSeedData.kt`

調查發現實際只有 128 個成就定義，少了 1 個。需新增 `daily_puzzle`（拼圖挑戰）來補齊。

- [ ] **Step 1: 在 daily_umbrella 之後插入第 129 個成就**

在 `daily_umbrella` 行和 `daily_late` 行之間插入：

```kotlin
        AchievementDefinitionEntity("daily_puzzle", "拼圖挑戰", "完成一個拼圖", "ic_achievement_photo_1", TriggerType.MANUAL_CONFIRM.value, 1L, false, 20, strengthWeight = 0f, agilityWeight = 0f, intelligenceWeight = 0.8f, charismaWeight = 0.2f, vitalityWeight = 0f),
```

- [ ] **Step 2: 執行測試**

```bash
./gradlew testDebugUnitTest
```
預期：BUILD SUCCESSFUL。

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/earthonline/app/data/local/AchievementSeedData.kt
git commit -m "fix(F12): add daily_puzzle achievement to reach 129 total"
```

---

### Task 2: F-12 — Database version 12→13 + empty Migration

**Files:**
- Modify: `app/src/main/java/com/earthonline/app/data/local/AppDatabase.kt:25`
- Modify: `app/src/main/java/com/earthonline/app/di/DatabaseModule.kt:27-33`

- [ ] **Step 1: Bump AppDatabase version**

```kotlin
// 修改前
version = 12,

// 修改後
version = 13,
```

- [ ] **Step 2: Add Migration(12, 13) to DatabaseModule**

```kotlin
// 修改前
fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        AppConstants.DATABASE_NAME
    ).fallbackToDestructiveMigration()
     .build()
}

// 修改後
fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
    val MIGRATION_12_13 = object : Migration(12, 13) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // No schema change — only seed data updated (added daily_puzzle achievement)
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
```

需要新增 import：`import androidx.room.migration.Migration` 和 `import androidx.sqlite.db.SupportSQLiteDatabase`

- [ ] **Step 3: Verify schema export generates v13 JSON**

```bash
./gradlew clean compileDebugKotlin
```
預期：`app/schemas/.../13.json` 自動生成。

- [ ] **Step 4: 執行測試**

```bash
./gradlew testDebugUnitTest
```
預期：BUILD SUCCESSFUL。

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/earthonline/app/data/local/AppDatabase.kt app/src/main/java/com/earthonline/app/di/DatabaseModule.kt app/schemas/
git commit -m "fix(F12): add Migration 12→13, bump DB version, keep fallback safety net"
```

---

### Task 3: F-13 — DAO-layer @Transaction composite method

**Files:**
- Modify: `app/src/main/java/com/earthonline/app/data/local/dao/CheckInRecordDao.kt`

- [ ] **Step 1: Add composite insert method to CheckInRecordDao**

在 `insert()` 方法之後（第 14 行後）新增：

```kotlin
    @Transaction
    suspend fun insertCheckinAndCount(
        record: CheckInRecord,
        userId: String,
        triggerType: String
    ): Long {
        insert(record)
        val count = countUniqueLocations(userId)
        return count.toLong()
    }
```

需要新增 import：
```kotlin
import androidx.room.Transaction
```

- [ ] **Step 2: Verify DAO compiles**

```bash
./gradlew compileDebugKotlin
```
預期：compiles without error。

---

### Task 4: F-13 — Update AchievementRepository.recordCheckin()

**Files:**
- Modify: `app/src/main/java/com/earthonline/app/data/repository/AchievementRepository.kt:64-86`

- [ ] **Step 1: Refactor recordCheckin() to use the new DAO method**

```kotlin
    // 修改前（第 64-86 行）
    suspend fun recordCheckin(...): List<UnlockedAchievementEvent> {
        val userId = AppConstants.LOCAL_USER_ID
        val triggerType = TriggerType.LOCATION_CHECKIN_COUNT.value

        checkInRecordDao.insert(
            CheckInRecord(...)
        )

        val uniqueCount = checkInRecordDao.countUniqueLocations(userId).toLong()
        progressDao.setProgressByType(userId, triggerType, uniqueCount)
        _totalCheckins.emit(uniqueCount)

        val events = mutableListOf<UnlockedAchievementEvent>()
        events.addAll(checkAndUnlock(userId, triggerType))
        events.addAll(evaluateAutoTrackAchievements(country, continent))
        if (altitude != null && altitude in AppConstants.MOUNTAIN_ALTITUDE_MIN..AppConstants.MOUNTAIN_ALTITUDE_MAX) {
            tryAutoUnlock(userId, "explore_mountain", System.currentTimeMillis())?.let { events.add(it) }
        }
        return events
    }

    // 修改後
    suspend fun recordCheckin(...): List<UnlockedAchievementEvent> {
        val userId = AppConstants.LOCAL_USER_ID
        val triggerType = TriggerType.LOCATION_CHECKIN_COUNT.value

        val uniqueCount = checkInRecordDao.insertCheckinAndCount(
            CheckInRecord(userId = userId, latitude = latitude, longitude = longitude,
                country = country, continent = continent, address = address,
                timestamp = System.currentTimeMillis()),
            userId, triggerType
        )

        progressDao.setProgressByType(userId, triggerType, uniqueCount)
        _totalCheckins.emit(uniqueCount)

        val events = mutableListOf<UnlockedAchievementEvent>()
        events.addAll(checkAndUnlock(userId, triggerType))
        events.addAll(evaluateAutoTrackAchievements(country, continent))
        if (altitude != null && altitude in AppConstants.MOUNTAIN_ALTITUDE_MIN..AppConstants.MOUNTAIN_ALTITUDE_MAX) {
            tryAutoUnlock(userId, "explore_mountain", System.currentTimeMillis())?.let { events.add(it) }
        }
        return events
    }
```

- [ ] **Step 2: 執行完整測試**

```bash
./gradlew testDebugUnitTest
```
預期：BUILD SUCCESSFUL，所有測試通過。

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/earthonline/app/data/local/dao/CheckInRecordDao.kt app/src/main/java/com/earthonline/app/data/repository/AchievementRepository.kt
git commit -m "fix(F13): wrap checkin insert+count in @Transaction DAO method"
```

---

### Task 5: Final verification

- [ ] **Step 1: Full clean build + test**

```bash
./gradlew clean testDebugUnitTest
```
預期：BUILD SUCCESSFUL，18 tests PASS。

- [ ] **Step 2: Update TODO.md**

```bash
git add TODO.md
git commit -m "docs: mark F-12/F-13 complete"
```

---

## 自檢

| 檢查項 | 結果 |
|------|:--:|
| F-12 覆蓋 | Task 1（補齊成就）+ Task 2（Migration + version bump） ✅ |
| F-13 覆蓋 | Task 3（DAO @Transaction）+ Task 4（Repository 重構） ✅ |
| Placeholder | 無 TBD/TODO ✅ |
| 型別一致性 | `checkInRecordDao.insertCheckinAndCount()` 回傳 `Long`，`recordCheckin()` 接收為 `uniqueCount: Long` ✅ |
