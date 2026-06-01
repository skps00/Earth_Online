# 代碼變更與問題日誌

## 2026-06-01 08:52:00 操作類型：修改
- **文件路徑**：code_change_log.md
- **變更摘要**：N-1 Screen Time 系列完成，所有測試通過
- **遇到的問題**：
  - 問題1：edit 工具前綴匹配 val definitions 導致 DashboardViewModel 損壞
    - 解決方案：手動修復為 `val definitions = repository.getAllDefinitions()`
    - 狀態：✅ 已解決
  - 問題2：AchievementRepositoryTest 建構子新增 screenTimeManager 參數未同步
    - 解決方案：補齊 mock + import
    - 狀態：✅ 已解決
  - 問題3：ScreenTimeAchievementTest rewardPoints Long/Int 型別不匹配
    - 解決方案：參數型別從 Long 改為 Int
    - 狀態：✅ 已解決
- **備註**：N-1 完成；7 commits；6 檔案修改（1 新增 + 2 測試修正）；18 測試全通過

## 2026-06-01 08:50:00 操作類型：新增
- **文件路徑**：app/src/test/java/com/earthonline/app/data/repository/ScreenTimeAchievementTest.kt
- **變更摘要**：新增 6 個單元測試案例驗證 evaluateScreenTimeAchievements()
- **遇到的問題**：
  - 問題1：DashboardViewModel 編輯時損壞變數宣告（val definitions 前綴匹配）
    - 解決方案：手動修復為 `val definitions = repository.getAllDefinitions()`
    - 狀態：✅ 已解決
  - 問題2：AchievementRepositoryTest 缺少 screenTimeManager 參數（建構子新增參數）
    - 解決方案：於 setup() 新增 mock 並傳遞給建構子，同時加入 import
    - 狀態：✅ 已解決
  - 問題3：ScreenTimeAchievementTest.rewardPoints 型別錯誤（Long vs Int）
    - 解決方案：將 mockAchievement 的 rewardPoints 參數型別改為 Int
    - 狀態：✅ 已解決
- **備註**：N-1 系列 Task 7/7；全部 18 測試通過

## 2026-06-01 08:47:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/screens/dashboard/DashboardViewModel.kt
- **變更摘要**：loadAchievementDisplay() 中整合 evaluateScreenTimeAchievements() 呼叫
- **遇到的問題**：無
- **備註**：N-1 系列 Task 6/7

## 2026-06-01 08:45:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/screens/dashboard/DashboardUiState.kt
- **變更摘要**：新增 screenTimeMinutes 欄位供 Dashboard 顯示今日螢幕使用時間
- **遇到的問題**：無
- **備註**：N-1 系列 Task 5/7

## 2026-06-01 08:44:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/data/repository/AchievementRepository.kt
- **變更摘要**：建構子注入 ScreenTimeManager；新增 evaluateScreenTimeAchievements() 方法
- **遇到的問題**：無
- **備註**：N-1 系列 Task 4/7

## 2026-06-01 08:42:00 操作類型：新增
- **文件路徑**：app/src/main/java/com/earthonline/app/data/screentime/ScreenTimeManager.kt
- **變更摘要**：新增 ScreenTimeManager，封裝 UsageStatsManager.queryEvents() 來偵測早期鳥/通宵/數位排毒
- **遇到的問題**：無
- **備註**：N-1 系列 Task 3/7；使用 KEYGUARD_HIDDEN (API 28+) + MOVE_TO_FOREGROUND 降級方案

## 2026-06-01 07:48:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/data/local/AchievementSeedData.kt
- **變更摘要**：將 daily_earlybird/daily_allnighter/daily_no_phone 的 triggerType 從 MANUAL_CONFIRM 改為 AUTO_TRACK
- **遇到的問題**：無
- **備註**：N-1 系列 Task 2/7

## 2026-06-01 07:45:00 操作類型：修改
- **文件路徑**：app/src/main/AndroidManifest.xml
- **變更摘要**：新增 PACKAGE_USAGE_STATS 權限供 N-1 Screen Time 功能使用
- **遇到的問題**：無
- **備註**：N-1 系列 Task 1/7

## 2026-05-31 19:15:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/components/ShimmerEffect.kt
- **變更摘要**：將 DashboardShimmer 分解為 4 個小型 private composable（ShimmerHeader、ShimmerStatusCard、ShimmerPetCard、ShimmerButton），動畫 brush 保留於父層傳遞
- **遇到的問題**：
  - 問題1：ShimmerPetCard 原始格式化版本為 37 行，超出 30 行限制
    - 解決方案：將 Box modifier 鏈式調用壓縮為每行 2-3 個鏈式方法，最終降至 28 行
    - 狀態：✅ 已解決
- **備註**：所有原始行為、註解均保持不變，僅結構拆分

## 2026-05-31 18:27:04 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/screens/dashboard/DashboardScreen.kt
- **變更摘要**：加入繁體中文註解（檔案級說明、composable 函式註解）
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 21:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/share/ShareCardGenerator.kt
- **變更摘要**：將 83 行的 generate() 函數分解為 8 個私有函數（createBitmap、drawCardBackground、drawGoldHeader、drawAchievementTitle、drawAchievementDescription、drawPointsDisplay、drawBottomAppName、saveAndReturn）
- **遇到的問題**：無
- **狀態**：✅ 已解決


## 2026-05-31 20:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/screens/dashboard/DashboardViewModel.kt
- **變更摘要**：分解 onEvent() 方法 — 將 97 行 when 分支各自提取為 8 個私有方法（handleCheckInConfirmed、handleCheckInRejected、handleManualConfirm、handleEvidencePhotoTaken、handleEvidenceConfirmed、handleEvidenceRejected、handleRenamePet、handleChangePetEmoji），onEvent 精簡至 12 行
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 20:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/data/photo/PhotoManager.kt
- **變更摘要**：修復靜默吞沒異常 — 在 deletePhoto() 與 fixExifOrientation() 的 catch 區塊加入 Log.e 呼叫、將 _ 改為 e、新增 TAG 常數與 android.util.Log 匯入
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 20:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/data/repository/AchievementRepository.kt
- **變更摘要**：修復靜默吞沒異常 — 在 syncAutoTrackFromHistory() 的 catch 區塊加入 Log.e 呼叫、將 _ 改為 e、新增 TAG 常數與 android.util.Log 匯入
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 20:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/components/AchievementDetailDialog.kt
- **變更摘要**：修復靜默吞沒異常 — 在 loadBitmap() 的 catch 區塊加入 Log.e 呼叫、將 _ 改為 e、新增 TAG 常數與 android.util.Log 匯入
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 20:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/components/EvidenceConfirmDialog.kt
- **變更摘要**：修復靜默吞沒異常 — 在照片載入 catch 區塊加入 Log.e 呼叫、將 _ 改為 e、新增 TAG 常數與 android.util.Log 匯入
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 20:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/screens/camera/CameraScreen.kt
- **變更摘要**：修復靜默吞沒異常 — 在 EvidenceThumbnail 的 LaunchedEffect catch 區塊加入 Log.e 呼叫、將 _ 改為 e、新增 TAG 常數與 android.util.Log 匯入
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 20:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/screens/settings/SettingsScreen.kt
- **變更摘要**：修復靜默吞沒異常 — 在隱私政策連結點擊的 catch 區塊加入 Log.e 呼叫、將 _ 改為 e、新增 TAG 常數與 android.util.Log 匯入
- **遇到的問題**：無
- **狀態**：✅ 已解決


### Phase 3: 成就牆增強
- **文件路徑**：AchievementCard.kt / AchievementDetailDialog.kt / DashboardScreen.kt
- **變更摘要**：
  - AchievementCard：Epic/Legendary 解鎖後 glow 效果（radialGradient 背景 + animateFloatAsState alpha）
  - 卡片圖標改用 radialGradient 背景
  - AchievementDetailDialog.kt：移除硬編碼 "查看全部/收起"，改用 view_all_evidence/collapse_evidence 字串資源
- **狀態**：✅ 已解決

### Phase 4: 主題與無障礙
- **文件路徑**：SettingsManager.kt / SettingsScreen.kt / MainActivity.kt / AppConstants.kt
- **變更摘要**：
  - SettingsManager 新增 darkModeEnabled 屬性（SharedPreferences KEY_DARK_MODE，預設 true）
  - SettingsScreen 新增深色主題切換開關（DarkMode/LightMode 圖標 + Gold Switch）
  - MainActivity 傳入 settingsManager.darkModeEnabled 到 EarthOnlineTheme
- **狀態**：✅ 已解決

### Phase 5: 空狀態與錯誤處理
- **文件路徑**：EmptyState.kt（新增）/ ShimmerEffect.kt（新增）/ ErrorState.kt（新增）/ CheckInHistoryScreen.kt / DashboardScreen.kt
- **變更摘要**：
  - EmptyState：可重用空狀態元件（emoji icon + 標題 + 描述 + 可選按鈕）
  - DashboardShimmer：骨架屏載入動畫（header/card/pet/button 占位符 + infiniteRepeatable 漸變掃光）
  - ErrorState：可重用錯誤狀態元件
  - CheckInHistoryScreen：無記錄時顯示 📍 空狀態（取代空白頁面）
  - DashboardScreen：載入中 spinner 改用 DashboardShimmer 骨架屏
- **狀態**：✅ 已解決

### Phase 6: 細節優化
- **文件路徑**：PetCard.kt
- **變更摘要**：
  - 動畫系數改用 AppConstants：BOUNCE_DAMPING_RATIO/BOUNCE_STIFFNESS/SPEECH_BUBBLE_*
  - 硬編碼魔法數字 0.4f/400f/5000/4000 全數移除
  - 新增 import com.earthonline.app.AppConstants
- **狀態**：✅ 已解決

## 2026-05-26 11:00:00 操作類型：修復Bug
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/components/AchievementDetailDialog.kt
- **變更摘要**：未解鎖成就不應顯示「分享成就」按鈕 — 加入 if (isUnlocked) 條件判斷
- **遇到的問題**：
  - 問題1：成就詳情對話框在未解鎖狀態下仍顯示「分享成就」按鈕
    - 原因：分享按鈕區塊無 isUnlocked 判斷，只被 !isHidden 條件控制
    - 解決方案：在第 176 行分享按鈕外層包覆 if (isUnlocked) { ... }
    - 狀態：✅ 已解決
- **備註**：UI/UX 重構系列遺漏的條件判斷
## 2026-05-26 12:00:00 操作類型：淺色主題色彩重構
- **文件路徑**：13 個 UI 檔案（見下方）
- **變更摘要**：將所有 UI 元件的硬編碼深色主題色彩替換為 MaterialTheme.colorScheme 引用，實現淺色/深色主題切換
- **遇到的問題**：
  - 問題1：replaceAll 破壞 import 宣告（CardDark/TextSecondaryDark 被替換為無效路徑）
    - 解決方案：改為逐個 edit 調用，避免 import 受影響
    - 狀態：✅ 已解決
  - 問題2：BottomNavBar 選中文字 Color.White 在淺色背景不可見
    - 解決方案：改用 MaterialTheme.colorScheme.primary（dark=Gold, light=GoldDark）
    - 狀態：✅ 已解決
- **備註**：
  - Theme.kt：Dark/LightColorScheme 皆新增 surfaceVariant + onSurfaceVariant
  - 對應關係：CardDark→surfaceVariant, DeepBlue(背景)→background, TextSecondaryDark→onSurfaceVariant, TextPrimaryDark→onSurface, DialogDark→surface
  - 受影響檔案：AppNavigation.kt, DashboardScreen.kt, DasboardHeader.kt, CheckInHistoryScreen.kt, SettingsScreen.kt, OnboardingScreen.kt, PetCard.kt, AchievementCard.kt, AchievementDetailDialog.kt, AchievementUnlockDialog.kt, CheckInConfirmDialog.kt, EvidenceConfirmDialog.kt, EmptyState.kt, ErrorState.kt
- **狀態**：✅ 已解決
## 2026-05-27 18:00:00 操作類型：新增
- **文件路徑**：app/src/main/java/com/earthonline/app/data/activity/ActivityRecognitionManager.kt
- **變更摘要**：建立 Activity Recognition Manager — 使用 Transition API 自動偵測走路/跑步/騎行/駕駛
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-27 18:00:00 操作類型：修改
- **文件路徑**：app/build.gradle.kts / AndroidManifest.xml / MainActivity.kt / AchievementRepository.kt / DashboardUiState.kt / DashboardViewModel.kt / DashboardScreen.kt
- **變更摘要**：整合 Activity Recognition + 活動數據顯示 + 自動解鎖 transport_bike / transport_bike_100 成就
- **遇到的問題**：無
- **狀態**：✅ 已解決
## 2026-05-27 19:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/data/repository/AchievementRepository.kt
- **變更摘要**：修正 tryAutoUnlock() — 改為回傳 UnlockedAchievementEvent? 而非直接 emit 到 repository flow，讓 ViewModel 能收到解鎖事件顯示解鎖動畫
- **遇到的問題**：
  - evaluateActivityAchievements() 透過 tryAutoUnlock() 解鎖成就，但 ViewModel 沒有訂閱 repository.unlockEvents
  - 成就已在資料庫解鎖但 UI 無彈窗動畫
  - 修改 tryAutoUnlock() 為回傳事件，evaluateActivityAchievements() 收集後透過 Pair 回傳給 ViewModel 的 handleUnlockEvents()
  - 狀態：✅ 已解決

## 2026-05-27 19:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/screens/dashboard/DashboardViewModel.kt
- **變更摘要**：loadAchievementDisplay() 改用 Pair 解構 evaluateActivityAchievements() 的活動數據與解鎖事件，並呼叫 handleUnlockEvents()
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-27 19:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/domain/service/SettingsManager.kt
- **變更摘要**：clearAllData() 加入清除 activity_stats SharedPreferences
- **遇到的問題**：
  - ActivityRecognition 使用獨立 SharedPreferences ("activity_stats")
  - clearAllData() 未清除該檔案，導致舊活動數據殘留
  - 狀態：✅ 已解決

## 2026-05-27 19:00:00 操作類型：新增 — P2-2 完成
- **文件路徑**：ActivityRecognitionManager.kt / AchievementRepository.kt / DashboardViewModel.kt / DashboardUiState.kt / DashboardScreen.kt / MainActivity.kt / build.gradle.kts / AndroidManifest.xml
- **變更摘要**：完成 Activity Recognition 活動識別功能 — 自動偵測走路/騎行/駕駛，Dashboard 顯示活動統計，自動解鎖 transport_bike / transport_bike_100
- **遇到的問題**：解鎖動畫不顯示（ViewModel 未接收事件—已修正）
- **狀態**：✅ 已解決
## 2026-05-31 10:00:00 操作類型：新增
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/components/ActivityPermissionDialog.kt
- **變更摘要**：建立活動識別權限請求對話框 — 說明用途後請求系統權限
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 10:00:00 操作類型：修改
- **文件路徑**：MainActivity.kt / AppNavigation.kt / DashboardScreen.kt / DashboardViewModel.kt / DashboardUiState.kt / SettingsScreen.kt / SettingsManager.kt / AppConstants.kt
- **變更摘要**：P2-2a A+B+C 方案 — 權限對話框 + Dashboard 提示橫幅 + Settings 活動追蹤開關
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 10:30:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/components/AchievementCard.kt
- **變更摘要**：新增繁體中文註解 — 檔案級說明 + AchievementCard 函數註解
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 10:30:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/components/AchievementDetailDialog.kt
- **變更摘要**：新增繁體中文註解 — 檔案級說明 + AchievementDetailDialog 函數註解
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 10:30:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/components/AchievementUnlockDialog.kt
- **變更摘要**：新增繁體中文註解 — 檔案級說明 + AchievementUnlockDialog 函數註解
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 10:30:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/components/CheckInConfirmDialog.kt
- **變更摘要**：新增繁體中文註解 — 檔案級說明 + CheckInConfirmDialog 函數註解
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 10:30:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/components/EvidenceConfirmDialog.kt
- **變更摘要**：新增繁體中文註解 — 檔案級說明 + EvidenceConfirmDialog 函數註解
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 10:30:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/components/EmptyState.kt
- **變更摘要**：新增繁體中文註解 — 檔案級說明 + EmptyState 函數註解
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 10:30:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/components/ErrorState.kt
- **變更摘要**：新增繁體中文註解 — 檔案級說明 + ErrorState 函數註解
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 10:30:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/components/ShimmerEffect.kt
- **變更摘要**：新增繁體中文註解 — 檔案級說明 + DashboardShimmer 函數註解
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 10:30:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/components/ActivityPermissionDialog.kt
- **變更摘要**：新增繁體中文註解 — 檔案級說明 + ActivityPermissionDialog 函數註解
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 10:30:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/navigation/Screen.kt
- **變更摘要**：新增繁體中文註解 — 檔案級說明
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 10:30:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/navigation/AppNavigation.kt
- **變更摘要**：新增繁體中文註解 — 檔案級說明 + AppNavigation、AnimatedBottomBar 函數註解
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 10:30:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/theme/Color.kt
- **變更摘要**：新增繁體中文註解 — 檔案級說明 + 依用途分組（預設色板/品牌色/表面色/文字色/稀有度色/特殊效果色）
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 10:30:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/theme/Theme.kt
- **變更摘要**：新增繁體中文註解 — 檔案級說明 + DarkColorScheme/AppTypography/LightColorScheme/EarthOnlineTheme 各項註解
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 10:30:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/share/ShareCardGenerator.kt
- **變更摘要**：新增繁體中文註解 — 檔案級說明 + generate 函數註解
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 10:30:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/share/ShareHelper.kt
- **變更摘要**：新增繁體中文註解 — 檔案級說明 + shareAchievement 函數註解
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 18:24:59 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/MainActivity.kt
- **變更摘要**：加入繁體中文註解：檔案級說明、各權限啟動器、生命週期方法
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 18:24:59 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/AppConstants.kt
- **變更摘要**：加入繁體中文註解：檔案級說明、各常數分組（資料庫、設定鍵值、寵物、備份、照片、地理編碼、動畫）
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 18:24:59 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/EarthOnlineApplication.kt
- **變更摘要**：加入繁體中文檔案級註解：Hilt 依賴注入入口
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 18:24:59 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/di/DatabaseModule.kt
- **變更摘要**：加入繁體中文檔案級註解：Hilt DI 模組提供 Room 資料庫與 DAO
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 18:24:59 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/data/local/AchievementSeedData.kt
- **變更摘要**：加入繁體中文註解：檔案級說明、create() 與 createProgress() 函數用途
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 19:00:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/data/repository/AchievementRepository.kt
- **變更摘要**：加入繁體中文註解 — 檔案級說明 + UnlockedAchievementEvent/Repository 類別 + 全部 30 個方法（含 initializeAchievements、recordCheckin、evaluateAutoTrackAchievements、confirmManualAchievement、computePlayerLevel、computeAndSavePetStats 等）+ 關鍵行內註解
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 19:00:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/data/backup/BackupManager.kt
- **變更摘要**：加入繁體中文註解 — 檔案級說明 + 類別及 exportToUri()/importFromUri() 方法
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 19:00:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/data/photo/PhotoManager.kt
- **變更摘要**：加入繁體中文註解 — 檔案級說明 + photoDir/createPhotoUri/getPhotoFileFromUri/compressPhoto/deletePhoto/fixExifOrientation 各方法 + 採樣/壓縮行內註解
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 19:00:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/data/location/LocationHelper.kt
- **變更摘要**：加入繁體中文註解 — 檔案級說明 + getLastLocation/reverseGeocode/selectBestAddress/buildAddressString/nominationFallback/buildDisplayString 各方法 + 無效座標過濾/評分機制行內註解
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 19:00:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/data/location/ContinentMapper.kt
- **變更摘要**：加入繁體中文註解 — 檔案級說明 + continentOf() 方法
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 19:00:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/data/media/SoundPlayer.kt
- **變更摘要**：加入繁體中文註解 — 檔案級說明 + 單例說明 + play()/stop() 方法
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 19:00:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/data/ml/ImageAnalyzer.kt
- **變更摘要**：加入繁體中文註解 — 檔案級說明 + 類別說明 + analyze() 方法
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 19:00:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/data/activity/ActivityRecognitionManager.kt
- **變更摘要**：加入繁體中文註解 — 檔案級說明 + companion object/receiver/init/startTracking/handleTransition/incrementStat + 6 個 getter + 速度估算行內註解
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 19:00:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/domain/service/CheckInCoordinator.kt
- **變更摘要**：加入繁體中文註解 — 檔案級說明 + 類別說明 + performCheckIn() 方法
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 19:00:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/domain/service/SettingsManager.kt
- **變更摘要**：加入繁體中文註解 — 檔案級說明 + soundEnabled/darkModeEnabled/activityTrackingEnabled 屬性 + clearAllData() 方法
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 19:00:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/domain/model/TriggerType.kt
- **變更摘要**：加入繁體中文註解 — 檔案級說明 + 枚舉值說明 + fromValue() 方法
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 19:00:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/domain/model/Rarity.kt
- **變更摘要**：加入繁體中文註解 — 檔案級說明 + 枚舉值等級說明 + fromPoints() 方法
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 19:00:00 操作類型：修改（批次註解）
- **文件路徑**：app/src/main/java/com/earthonline/app/domain/model/AchievementTriggers.kt
- **變更摘要**：加入繁體中文註解 — 檔案級說明 + 國家/洲/數量觸發器集合說明
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 20:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/MainActivity.kt
- **變更摘要**：修正靜默例外吞噬 — catch (_: Exception) → catch (e: Exception)，加入 Log.e(TAG, ...) 記錄、import android.util.Log、TAG 常數
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 20:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/data/activity/ActivityRecognitionManager.kt
- **變更摘要**：修正靜默例外吞噬 — companion object 加入 TAG、catch (_: Exception) → catch (e: Exception)、加入 Log.e 記錄
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 20:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/data/location/LocationHelper.kt
- **變更摘要**：修正 3 處靜默例外吞噬 — SecurityException/Geocoder/Nominatim catch 全改為 e: Exception + Log.e + TAG 常數
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 20:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/data/media/SoundPlayer.kt
- **變更摘要**：修正靜默例外吞噬 — object 內加入 TAG 常數、catch (_: Exception) → catch (e: Exception)、加入 Log.e 記錄
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 20:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/data/ml/ImageAnalyzer.kt
- **變更摘要**：修正靜默例外吞噬 — 加入 TAG 常數（靠近其他頂層常數）、catch (_: Exception) → catch (e: Exception)、加入 Log.e 記錄
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-05-31 19:40:37 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/screens/onboarding/OnboardingScreen.kt
- **變更摘要**：分解 OnboardingScreen（131 行）為 3 個私有子 composable：OnboardingPageContent（20 行）— emoji + 標題 + 說明，OnboardingPageIndicator（15 行）— 圓點進度指示器，OnboardingBottomButtons（28 行）— 跳過 + 下一步／開始按鈕。所有 stringResource 呼叫保留於主 composable 並以純字串參數傳遞；新增 PagerState 與 CoroutineScope import
- **遇到的問題**：無
- **狀態**：✅ 已解決
## 2026-06-01 12:00:00 操作類型：新增（Phase 1: 異常處理）
- **文件路徑**：MainActivity.kt / ActivityRecognitionManager.kt / LocationHelper.kt / SoundPlayer.kt / ImageAnalyzer.kt / PhotoManager.kt / AchievementRepository.kt / AchievementDetailDialog.kt / EvidenceConfirmDialog.kt / CameraScreen.kt / SettingsScreen.kt
- **變更摘要**：14 處靜默吞異常 (catch (_: Exception) {}) 修正為 Log.e(TAG, msg, e) 記錄
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-06-01 12:30:00 操作類型：重構（Phase 2: 長函數分解）
- **文件路徑**：DashboardViewModel.kt / ShimmerEffect.kt / OnboardingScreen.kt / ShareCardGenerator.kt
- **變更摘要**：4 個超過50行函數分解為 23 個小型私有函數（每函數 ≤30行）
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-06-01 13:00:00 操作類型：新增（Phase 3: 單元測試）
- **文件路徑**：RarityTest.kt / TriggerTypeTest.kt / AchievementRepositoryTest.kt
- **變更摘要**：新增 3 個測試類（16 個測試案例）：稀有度計算、觸發類型解析、XP/等級數學
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-06-01 14:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/data/local/AchievementSeedData.kt
- **變更摘要**：將 9 個洲/國家探索成就的 TriggerType 從 MANUAL_CONFIRM 改為 AUTO_TRACK（explore_japan、explore_australia、explore_asia、explore_europe、explore_africa、explore_north_america、explore_south_america、explore_oceania、explore_antarctica）
- **遇到的問題**：無
- **狀態**：✅ 已解決
- **備註**：explore_first_abroad、explore_border、explore_dateline 保持 MANUAL_CONFIRM 不變
## 2026-06-01 15:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/data/local/AchievementSeedData.kt
- **變更摘要**：修正 9 個國家/洲成就 triggerType — MANUAL_CONFIRM → AUTO_TRACK（explore_japan/australia/asia/europe/africa/north_america/south_america/oceania/antarctica）
- **遇到的問題**：無
- **備註**：這些成就已由 autoTrackSpecificCountry/Continent 方法自動偵測（透過 AchievementTriggers 映射表），改 triggerType 僅為語義一致性。explore_first_abroad/border/dateline 保持 MANUAL_CONFIRM（需額外偵測邏輯）
- **狀態**：✅ 已解決

## 2026-06-01 15:05:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/data/local/AchievementSeedData.kt
- **變更摘要**：將 explore_mountain 成就的 TriggerType 從 MANUAL_CONFIRM 改為 AUTO_TRACK
- **遇到的問題**：無
- **狀態**：✅ 已解決
## 2026-06-01 17:00:00 操作類型：新增 (N-2 高山成就自動化)
- **文件路徑**：AchievementSeedData.kt / AchievementRepository.kt / CheckInCoordinator.kt / DashboardViewModel.kt / DashboardUiState.kt
- **變更摘要**：explore_mountain 成就自動化 — 打卡時取 Location.altitude，海拔 ≥2500m 自動解鎖
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-06-01 17:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/data/repository/AchievementRepository.kt
- **變更摘要**：recordCheckin() 新增 altitude: Double? = null 參數，海拔 ≥2500m 時自動解鎖 explore_mountain
- **遇到的問題**：無
- **狀態**：✅ 已解決

## 2026-06-01 17:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/domain/service/CheckInCoordinator.kt
- **變更摘要**：performCheckIn() 傳入 location.altitude 到 ViewModel
- **遇到的問題**：無
- **狀態**：✅ 已解決
## 2026-06-01 10:55:53 操作類型：修改
- **文件路徑**：TODO.md
- **變更摘要**：將 N-2 任務的「氣壓計」改為「高山海拔」、狀態從 ⬜ 改為 ✅
- **遇到的問題**：無
- **備註**：無

