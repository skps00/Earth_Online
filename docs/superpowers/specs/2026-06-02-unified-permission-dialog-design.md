# 統一權限對話框 — 設計文件

**日期**：2026-06-02  
**狀態**：已核准  
**範圍**：8 檔案

---

## 1. 概述

將現有 4 個獨立的權限對話框整合為 2 個，改善使用者體驗，避免多重彈窗干擾。

---

## 2. 設計目標

### 對話框 A：統一 App 權限（in-app）

在 Dashboard 首次載入時，若任一 in-app 權限未授予且「權限提醒」未關閉，顯示單一對話框，統合三個權限的授權流程。

### 對話框 B：系統設定權限

保留現有 `ScreenTimePermissionDialog`（Screen Time 權限需跳系統設定，無法 in-app 請求）。若使用者關閉「權限提醒」，此對話框也不顯示。

### 設定頁面

新增「權限提醒」開關。關閉時所有權限對話框不再出現。

---

## 3. 對話框 A 流程

```
Dashboard 載入
  │
  ├─ 全部已授權 → 不顯示
  ├─ 權限提醒已關閉 → 不顯示
  └─ 任一未授權 → 顯示對話框 A
        │
        ├─ [稍後]
        │   關閉，本次 App 啟動不再彈
        │
        ├─ [開始授權]
        │   依序請求：位置 → 身體活動 → 相機
        │   顯示結果畫面
        │      ├─ [重試未授權的] → 再次請求
        │      └─ [完成] → 關閉
        │
        └─ 全部已授權 → 自動關閉，永不再出現
```

每次 App 冷啟動最多彈一次。下次啟動時若仍有未授權的，會再次出現。

---

## 4. 使用者體驗

### 畫面 1：權限清單（初始狀態）

```
   📱 讓成就自動化

   為了自動偵測並解鎖相關成就，
   地球 Online 需要以下權限。
   你的所有資料只會儲存在本機。

   📍 位置　　　　　⬜ 打卡記錄，辨識國家大洲
   🚶 身體活動　　　⬜ 自動解鎖交通成就
   📷 相機　　　　　⬜ 拍攝成就證據照

   [開始授權]  [稍後]
```

### 畫面 2：成果顯示

```
   ✅ 位置　　　　　已授權
   ✅ 身體活動　　　已授權
   ❌ 相機　　　　　已拒絕

   [重試未授權的]  [完成]
```

### 設定頁面：權限提醒開關

```
   權限提醒  　　　　[開關]
```

---

## 5. 技術設計

### 5.1 DashboardUiState 變更

```kotlin
data class DashboardUiState(
    // 移除: showActivityPermissionDialog, activityPermissionGranted
    // 新增:
    val showUnifiedPermissionDialog: Boolean = false,
    val locationPermissionGranted: Boolean = false,
    val activityPermissionGranted: Boolean = false,
    val cameraPermissionGranted: Boolean = false,
    // 保留: showScreenTimePermissionDialog（對話框 B）
    ...
)
```

### 5.2 DashboardViewModel 變更

- `loadAchievementDisplay()`: 檢查三個權限狀態，設定 `showUnifiedPermissionDialog` 和 `showScreenTimePermissionDialog`
- 移除: `dismissActivityPermissionDialog()`, `grantActivityPermission()`, `isActivityPermissionGranted()`, `markActivityPermissionRequested()`, `isActivityPermissionRequested()`
- 新增: `dismissUnifiedPermissionDialog()`, `setPermissionGranted(permission: String, granted: Boolean)`

### 5.3 DashboardScreen 變更

在 `Box` 層級新增：
```kotlin
if (uiState.showUnifiedPermissionDialog) {
    UnifiedPermissionDialog(
        locationGranted = uiState.locationPermissionGranted,
        activityGranted = uiState.activityPermissionGranted,
        cameraGranted = uiState.cameraPermissionGranted,
        onStartGranting = { ... },   // 觸發 sequential launchers
        onDismiss = { viewModel.dismissUnifiedPermissionDialog() }
    )
}
```

移除: `ActivityPermissionDialog` 區塊

### 5.4 MainActivity 變更

- 移除 `showLocationRationale`, `showCameraRationale` flags
- 移除 `PermissionRationaleDialog` callback 與 composable 呼叫
- Location/Camera rationale 邏輯不再需要（由統一對話框覆蓋）

### 5.5 SettingsManager 變更

```kotlin
var permissionRemindersEnabled: Boolean
    get() = !prefs.getBoolean("permission_reminders_disabled", false)
    set(value) = prefs.edit().putBoolean("permission_reminders_disabled", !value).apply()
```

### 5.6 授權序列表

DashboardScreen 使用三個 `rememberLauncherForActivityResult`：

```kotlin
val permissionStep by remember { mutableIntStateOf(0) }

LaunchedEffect(permissionStep) {
    when (permissionStep) {
        0 -> {} // idle
        1 -> locationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        2 -> activityLauncher.launch(AppConstants.ACTIVITY_RECOGNITION_PERMISSION)
        3 -> cameraLauncher.launch(Manifest.permission.CAMERA)
        else -> viewModel.dismissUnifiedPermissionDialog()
    }
}
```

每次 launcher 回調將結果寫入 ViewModel 並遞增 step。

### 5.7 重試邏輯

「重試未授權的」按鈕只對 status != granted 的權限重新請求。重置 step 為 1 並將已授權的 flag 設為 skip。

---

## 6. 檔案變更摘要

| # | 檔案 | 操作 | 變更 |
|---|------|:--:|------|
| 1 | `DashboardUiState.kt` | 修改 | 整合權限旗標為單一對話框 + 3 個授權狀態 |
| 2 | `DashboardViewModel.kt` | 修改 | 整合權限檢查；+ dismiss/setGranted 方法；移除舊 5 個方法 |
| 3 | `DashboardScreen.kt` | 修改 | + `UnifiedPermissionDialog` 元件 + 3 launchers；移除舊 dialog |
| 4 | `MainActivity.kt` | 修改 | 移除 rationale dialog flags + composable 呼叫 |
| 5 | `SettingsManager.kt` | 修改 | + `permissionRemindersEnabled` |
| 6 | `SettingsScreen.kt` | 修改 | + 「權限提醒」開關 |
| 7 | `strings.xml` | 修改 | + 統一對話框字串 |
| 8 | `AchievementPermissionDialog.kt` | 刪除 | 被取代 |

---

## 7. 自檢

- ✅ 無 placeholder / TBD
- ✅ 架構與功能描述一致
- ✅ 範圍聚焦（8 檔案）
- ✅ 無歧義
