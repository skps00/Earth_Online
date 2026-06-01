# N-1: Screen Time 自動偵測 — 設計文件

**日期**：2026-06-01  
**狀態**：待審閱  
**範圍**：6 檔案 / 1 功能

---

## 1. 概述

使用 Android `UsageStatsManager.queryEvents()` 自動偵測每日螢幕使用行為，將 3 個現有 MANUAL_CONFIRM 成就改為 AUTO_TRACK。

---

## 2. 技術選擇

| 項目 | 值 |
|------|------|
| API | `UsageStatsManager.queryEvents(beginTime, endTime)` |
| 兼容 | API 21+（minSdk 26 完全覆蓋，所有目標裝置通用 ✅） |
| 權限 | `android.permission.PACKAGE_USAGE_STATS`（特殊權限，需使用者手動至系統設定開啟） |
| 事件保留期 | 系統通常保留 2–5 天，遠大於本功能所需的 24 小時視窗 |

### 事件類型

| 事件常數 | API | 用途 |
|------|------|------|
| `UsageEvents.Event.KEYGUARD_HIDDEN` | 28+ | 螢幕解鎖事件 → `earlybird` 最精確判斷 |
| `UsageEvents.Event.MOVE_TO_FOREGROUND` | 21+ | App 進入前景 → 計算螢幕使用時長 |
| `UsageEvents.Event.MOVE_TO_BACKGROUND` | 21+ | App 離開前景 → 配對計算時長 |

### 為什麼不用 `queryUsageStats()`

Android API 僅支援 `INTERVAL_DAILY`/`WEEKLY`/`MONTHLY`/`YEARLY`，無 `INTERVAL_HOURLY`。`daily_allnighter` 需要分辨凌晨 2–5 點的時段，`INTERVAL_DAILY` 無法滿足。`queryEvents()` 提供精確 timestamp 的事件級數據。

---

## 3. 成就觸發邏輯

| 成就 ID | 名稱 | 目標條件 | 偵測方式 | 觸發時機 |
|------|------|------|------|------|
| `daily_earlybird` | 早起的鳥兒 | 清晨 5 點前起床 | 今天最早螢幕活動 timestamp < 當日 5:00 AM | 每日 Dashboard 載入 |
| `daily_allnighter` | 徹夜未眠 | 通宵一次 | 凌晨 2:00–5:00 AM 前景 App 總時長 > 30 分鐘 | 每日 Dashboard 載入 |
| `daily_no_phone` | 數位排毒 | 一整天不用手機 | 過去 24h 無任何前景 App 活動 | 每日 Dashboard 載入 |

### `daily_earlybird` 細節

- API 28+：遍歷今天 `KEYGUARD_HIDDEN` 事件 → 取最早 timestamp
- API 26–27（降級）：用 `MOVE_TO_FOREGROUND` 的最早 timestamp（近似，誤差在可接受範圍內）
- 若今天無任何事件 → 不觸發

### `daily_allnighter` 細節

- 遍歷今天 2:00–5:00 AM 的 `MOVE_TO_FOREGROUND` + `MOVE_TO_BACKGROUND` 事件配對
- 累計前景時長（配對 `MOVE_TO_FOREGROUND` → `MOVE_TO_BACKGROUND` 的時間差）
- 若累計 > 30 分鐘 → 觸發

### `daily_no_phone` 細節

- 遍歷過去 24h 的 `MOVE_TO_FOREGROUND` 事件
- 若沒有任何前景事件 → 觸發
- 第一次載入（無歷史記錄）不會觸發，因為需要過去 24h 確實無活動

### 防重複觸發

每個成就使用 `tryAutoUnlock()`——若 `UserAchievementProgressEntity.isUnlocked` 已為 true，則跳過不重複解鎖。

---

## 4. 架構

### 新增檔案

```
app/src/main/java/com/earthonline/app/data/screentime/ScreenTimeManager.kt  🆕
```

#### `ScreenTimeManager` 介面

```kotlin
@Singleton
class ScreenTimeManager @Inject constructor(
    private val context: Context
) {
    // 權限檢查
    fun isUsageStatsPermissionGranted(): Boolean

    // 跳轉至系統設定頁
    fun openUsageAccessSettings()

    // 私人查詢方法（在 IO 執行緒執行）
    private suspend fun queryEvents(startMs: Long, endMs: Long): UsageEvents?

    // 公開評估方法 — 回傳達成條件的成就 ID 列表
    suspend fun evaluateAchievements(): List<String>
}
```

### 內部實作細節

- `queryEarlyStartOfDay()`：迴歸今天最早 `KEYGUARD_HIDDEN`（API 28+）或 `MOVE_TO_FOREGROUND` timestamp → 比較是否 < 5:00 AM
- `queryNightOwlDuration()`：遍歷 2:00–5:00 AM 區間，配對 `MOVE_TO_FOREGROUND` / `MOVE_TO_BACKGROUND`，累計前景時長 → 比較是否 > 30 min
- `queryPast24hHasAnyActivity()`：遍歷過去 24h，檢查是否有任何 `MOVE_TO_FOREGROUND` → 若無則觸發 `daily_no_phone`
- 所有 `queryEvents()` 呼叫在 `withContext(Dispatchers.IO)` 中執行

### 修改檔案

| # | 檔案 | 變更 |
|---|------|------|
| 1 | `ScreenTimeManager.kt` 🆕 | 如上所述 |
| 2 | `AchievementSeedData.kt` | `daily_earlybird`/`daily_allnighter`/`daily_no_phone`：`TriggerType.MANUAL_CONFIRM` → `TriggerType.AUTO_TRACK` |
| 3 | `AchievementRepository.kt` | + 建構子注入 `ScreenTimeManager`；+ `evaluateScreenTimeAchievements()` 方法，回傳 `List<UnlockedAchievementEvent>` |
| 4 | `DashboardViewModel.kt` | `loadAchievementDisplay()` 中呼叫 `evaluateScreenTimeAchievements()`；UiState 新增 `screenTimeMinutes` |
| 5 | `DashboardUiState.kt` | + `screenTimeMinutes: Int = 0`（今日前景總時長，Dashboard 顯示用） |
| 6 | `AndroidManifest.xml` | + `<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" />` |

---

## 5. 權限流程

```
Dashboard 首次載入
  → ScreenTimeManager.isUsageStatsPermissionGranted()?
    ├─ Yes → evaluateAchievements() → evaluateScreenTimeAchievements() → 解鎖成就
    └─ No  → 不崩潰，回傳空結果
              → 未來可在 Dashboard 顯示提示橫幅（類似現有活動追蹤提示）
```

`PACKAGE_USAGE_STATS` 是特殊權限，無法透過標準 `requestPermissions()` 彈窗。需呼叫：

```kotlin
context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
```

---

## 6. 錯誤處理

| 情境 | 行為 |
|------|------|
| 權限未授予 | `isUsageStatsPermissionGranted()` 回傳 false，`evaluateAchievements()` 回傳空列表 |
| `queryEvents()` 拋異常 | `try/catch` + `Log.e`，回傳 0 或空，不崩潰 |
| 無事件記錄（新裝置 / 清除記錄） | 視為無活動（`daily_no_phone` 不觸發，因無法確認過去 24h 確實無活動） |
| 跨時區 | 使用 `java.util.Calendar` + `System.currentTimeMillis()`，一律以裝置本地時間判斷 |
| 事件序列不完整（缺 MOVE_TO_BACKGROUND） | 忽略該配對，不計入時長 |

---

## 7. 測試策略

### 單元測試

| 測試 | 內容 |
|------|------|
| `AchievementRepository.evaluateScreenTimeAchievements()` | Mock `ScreenTimeManager`，驗證 3 成就各自觸發/不觸發（共 6 案例） |

### 手動測試

| 測試 | 內容 |
|------|------|
| `ScreenTimeManager` 方法 | Android-dependent，在實機手動驗證 `queryEvents()` 回傳數據正確性 |

---

## 8. 專案模式遵循

- `ScreenTimeManager` 注入模式：`@Singleton` + `@Inject constructor(context: Context)`（與 `ActivityRecognitionManager` 一致）
- `AchievementRepository` 評估方法：`suspend fun` 回傳 `List<UnlockedAchievementEvent>`（與 `evaluateActivityAchievements()` 一致）
- ViewModel 整合：`loadAchievementDisplay()` 中 `withContext(Dispatchers.IO)` → 呼叫評估 → `handleUnlockEvents()`
- 日誌：所有異常使用 `Log.e(TAG, ...)`，與現有規則重構一致

---

## 9. 不納入範圍

- Dashboard 螢幕時間顯示卡片（純視覺改動，等待全新 UI 設計）
- 權限提示橫幅（留待 UI 設計）
- UsageStats 歷史分析或圖表
- 跨裝置螢幕時間同步（純本機）
- 「每日只觸發一次」的日期重置邏輯由 `tryAutoUnlock()` 的 `isUnlocked` 檢查自然保證（成就解鎖後不再觸發，無需手動管理）
