# 代碼變更與問題日誌

## 2026-05-24 18:51:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/screens/dashboard/DashboardScreen.kt
- **變更摘要**：加入 `import android.content.Context` 解決 `Context.MODE_PRIVATE` 未解析引用錯誤
- **遇到的問題**：
  - 問題1：`Context.MODE_PRIVATE` 觸發 "Unresolved reference: Context" 編譯錯誤
    - 解決方案：補上 `import android.content.Context`
    - 狀態：✅ 已解決
- **備註**：Onboarding 首次引導用 `SharedPreferences` 記錄是否已展示

## 2026-05-24 14:00:00 操作類型：新增
- **文件路徑**：app/src/main/java/com/earthonline/app/data/local/AchievementSeedData.kt
- **變更摘要**：新增 43 個社群成就（epic/health/daily/career/explore/transport），共 129 成就
- **遇到的問題**：
  - 問題1：曾使用 `achievements.json` 方案導致 resource linking 錯誤及進度遺失
    - 解決方案：回退至 Kotlin `AchievementSeedData.kt` 方案
    - 狀態：✅ 已解決
  - 問題2：FK ON DELETE CASCADE 導致成就因刪除關聯記錄而被非預期重設
    - 解決方案：修正 Room 資料庫外鍵級聯刪除設定
    - 狀態：✅ 已解決
- **備註**：不採用 JSON 方案

## 2026-05-24 15:00:00 操作類型：新增
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/components/AchievementUnlockDialog.kt
- **變更摘要**：實作螢火蟲粒子特效對話框：8 個金色光點環繞星星飛行，Compose Canvas + sin/cos 動畫帶閃爍和彈出效果
- **遇到的問題**：
  - 問題1：全螢幕 overlay 過於佔據畫面
    - 解決方案：改為頂部小卡片 + 滑動關閉
    - 狀態：✅ 已解決
  - 問題2：粒子動畫效能
    - 解決方案：使用 Canvas + `infiniteTransition` + `animateFloat` 控制旋轉與閃爍
    - 狀態：✅ 已解決
- **備註**：無

## 2026-05-24 15:30:00 操作類型：新增
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/screens/onboarding/OnboardingScreen.kt
- **變更摘要**：新增 3 頁 Onboarding 首次引導（emoji + HorizontalPager + SharedPreferences 標記）
- **遇到的問題**：
  - 問題1：`HorizontalPager` 實驗性 API 警告
    - 解決方案：後續補充 `@OptIn(ExperimentalFoundationApi::class)`
    - 狀態：✅ 已解決
- **備註**：無

## 2026-05-24 15:30:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/screens/dashboard/DashboardScreen.kt
- **變更摘要**：加入空狀態提示、17 項重構（666 行減至 ~300 行）、Onboarding 入口觸發
- **遇到的問題**：
  - 問題1：`edit` 工具損壞 UTF-8 編碼導致中文亂碼
    - 解決方案：改用 `write` 工具重新寫入完整文件
    - 狀態：✅ 已解決
  - 問題2：多次 `git push --force` 導致 DashboardScreen.kt 被覆蓋成 8 行
    - 解決方案：回滾後使用 `write` 工具重新生成
    - 狀態：✅ 已解決
- **備註**：需用 `write` 工具而非 `edit` 寫入 UTF-8 中文內容

## 2026-05-24 16:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/screens/settings/SettingsScreen.kt
- **變更摘要**：備份按鈕從 Dashboard 移至 SettingsScreen、BackHandler 返回鍵修復
- **遇到的問題**：
  - 問題1：返回鍵在設定頁無效
    - 解決方案：加入 `BackHandler` 處理返回導航
    - 狀態：✅ 已解決
- **備註**：無

## 2026-05-24 16:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/screens/history/HistoryScreen.kt
- **變更摘要**：打卡歷史清單（按國家分組，顯示地址/國家/座標）＋ BackHandler 返回鍵修復
- **遇到的問題**：
  - 問題1：Geocoder / Nominatim 僅返回國家，無法取得街道地址
    - 解決方案：Android Geocoder `getAddressLine(0)` 嘗試無效，Nominatim API 同樣無效
    - 狀態：❌ 未解決
  - 問題2：返回鍵無效
    - 解決方案：加入 `BackHandler`
    - 狀態：✅ 已解決
- **備註**：地址解析為已知阻斷問題

## 2026-05-24 17:00:00 操作類型：新增
- **文件路徑**：app/src/main/java/com/earthonline/app/AppConstants.kt
- **變更摘要**：建立 `AppConstants` 集中管理 `LOCAL_USER_ID`、`DATABASE_NAME`、`PREFS_NAME`
- **遇到的問題**：
  - 問題1：全專案散落 `"local_user"` 硬編碼字串
    - 解決方案：建立 `AppConstants` 物件常數化所有硬編碼
    - 狀態：✅ 已解決
- **備註**：硬編碼清理系列之一

## 2026-05-24 17:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/theme/Color.kt
- **變更摘要**：Color 去重複：`Color(0xFF1A1A2E)` → `DeepBlue`、定義 `DialogDark`、`DestructiveRed`
- **遇到的問題**：
  - 問題1：全專案多處重複 `Color(0xFF1A1A2E)` 等魔術顏色
    - 解決方案：統一命名為 `DeepBlue` 等常數，需補齊所有檔案 import
    - 狀態：✅ 已解決
- **備註**：無

## 2026-05-24 17:00:00 操作類型：修改
- **文件路徑**：app/src/main/res/values/strings.xml
- **變更摘要**：抽 35+ 中文字串至 `strings.xml`，總計 70+ 字串資源
- **遇到的問題**：
  - 問題1：`???` 字串觸發 AAPT2 `attr/??` 錯誤
    - 解決方案：從 `strings.xml` 刪除 `???`，改用 Kotlin 直接寫 `"???"`
    - 狀態：✅ 已解決
  - 問題2：`remember {}` 區塊內不可呼叫 `stringResource()` composable 函式
    - 解決方案：先提取 `stringResource()` 結果到外部變數再傳入 `remember {}`
    - 狀態：✅ 已解決
- **備註**：`???` 為 AAPT2 保留字元，不可進入資源檔

## 2026-05-24 17:30:00 操作類型：刪除
- **文件路徑**：app/src/main/java/com/earthonline/app/domain/service/AchievementService.kt
- **變更摘要**：刪除 `AchievementService`，ViewModel 改為直接依賴 Repository
- **遇到的問題**：
  - 問題1：過度抽象層增加維護成本
    - 解決方案：簡化架構，ViewModel → Repository → DAO
    - 狀態：✅ 已解決
- **備註**：無

## 2026-05-24 17:30:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/domain/service/SoundPlayer.kt
- **變更摘要**：WAV 音效轉 MP3 解決 `MediaPlayer.create()` 回傳 null；改用 SharedPreferences `sound_muted` 控制靜音
- **遇到的問題**：
  - 問題1：Mixkit WAV 編碼不相容，`MediaPlayer.create()` 回傳 null
    - 解決方案：轉換音效檔為 MP3 格式
    - 狀態：✅ 已解決
  - 問題2：多處靜音控制分散
    - 解決方案：統一 `sound_muted` 單一 key 控制
    - 狀態：✅ 已解決
- **備註**：無

## 2026-05-24 18:00:00 操作類型：新增
- **文件路徑**：app/src/main/java/com/earthonline/app/domain/service/BackupRestoreManager.kt
- **變更摘要**：實作 JSON 備份匯出/匯入功能（SAF 檔案選擇器）
- **遇到的問題**：
  - 問題1：需選擇跨應用持久化備份機制
    - 解決方案：JSON + SAF DocumentProvider 匯出/匯入
    - 狀態：✅ 已解決
- **備註**：無

## 2026-05-24 18:30:00 操作類型：新增
- **文件路徑**：app/src/main/java/com/earthonline/app/domain/service/SettingsManager.kt
- **變更摘要**：音效開關與清除資料集中管理
- **遇到的問題**：
  - 問題1：設定邏輯分散在 Dashboard 和 Settings
    - 解決方案：集中到 `SettingsManager`
    - 狀態：✅ 已解決
- **備註**：搭配 `SoundPlayer` 運作

## 2026-05-24 18:51:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/screens/dashboard/DashboardScreen.kt
- **變更摘要**：加入 `import android.content.Context` 解決 `Context.MODE_PRIVATE` 未解析引用錯誤
- **遇到的問題**：
  - 問題1：`Context.MODE_PRIVATE` 觸發 "Unresolved reference: Context" 編譯錯誤
    - 解決方案：補上 `import android.content.Context`
    - 狀態：✅ 已解決
- **備註**：Onboarding 首次引導用 `SharedPreferences` 記錄是否已展示

## 2026-05-24 18:52:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/screens/onboarding/OnboardingScreen.kt
- **變更摘要**：加入 `@OptIn(ExperimentalFoundationApi::class)` 註解與 `ExperimentalFoundationApi` import，消除 HorizontalPager 實驗性 API 警告
- **遇到的問題**：
  - 問題1：`HorizontalPager` 觸發 "This foundation API is experimental" 編譯警告
    - 解決方案：在函數前加入 `@OptIn(ExperimentalFoundationApi::class)`，補 `ExperimentalFoundationApi` import
    - 狀態：✅ 已解決
- **備註**：無

## 2026-05-25 12:00:00 操作類型：修改
- **文件路徑**：TODO.md
- **變更摘要**：從 GitHub 拉回原始版本，合併新開發計劃（自動偵測成就/寵物系統/雲端方案），更新已完成項目
- **遇到的問題**：
  - 問題1：local TODO.md 被新版本覆蓋，與 GitHub OG 版本不一致
    - 解決方案：`git checkout origin/main -- TODO.md` 還原 OG 版本後合併兩者內容
    - 狀態：✅ 已解決
  - 問題2：`Add-Content` PowerShell cmdlet 導致 code_change_log.md 中文亂碼
    - 解決方案：用 `write` 工具重寫整個檔案（UTF-8 編碼正確）
    - 狀態：✅ 已解決
- **備註**：保留 OG 結構（emoji 標題 + ✅/🔴/🟡/🟢），新項目追加至對應區段

## 2026-05-25 12:30:00 操作類型：修改
- **文件路徑**：TODO.md
- **變更摘要**：重構為清晰三階段表格（P1 本週/P2 1-2月/P3 日後），已完成項折疊，Bug 升為 P1
- **遇到的問題**：
  - 問題1：上次合併後 P1 項目分散在 Bug/中優先/低優先各區段，用戶找不到 P1
    - 解決方案：新建獨立 🔴 Phase 1 區段，用表格清楚列出 5 項 P1 任務
    - 狀態：✅ 已解決
- **備註**：無

## 2026-05-25 13:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/data/location/LocationHelper.kt
- **變更摘要**：重寫 reverseGeocode() — 替代 getAddressLine(0) 唯讀方案，改從 Address 個別欄位組裝地址，加入評分機制選最佳結果，Geocoder 不可用時 fallback 到 Nominatim
- **遇到的問題**：
  - 問題1：Geocoder.getAddressLine(0) 多數情況回空，只拿到國家名
    - 先前嘗試：getAddressLine(0)（❌）、Nominatim API（❌）
    - 新方案：getFromLocation(5筆) + 評分機制選最詳細結果 + 從 thoroughfare/subThoroughfare/locality/adminArea 手動組裝地址字串；Geocoder.isPresent() false 時 fallback 到 Nominatim
    - 狀態：待驗證（需在實機/模擬器測試）
- **備註**：日誌規則觸發 — 查閱後確認 ❌ 未解決，基於失敗記錄提出新路徑

## 2026-05-25 13:15:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/screens/dashboard/DashboardViewModel.kt
- **變更摘要**：修補 CheckInConfirmed 在清空 state 前先擷取 country/continent/address 到本地變數
- **遇到的問題**：
  - 問題1：打卡確認對話框有顯示地址，但歷史頁只顯示國家名（Hong Kong）
    - 根本原因：`_uiState.update { it.copy(pendingAddress = "") }` 在 `recordCheckin(...)` 之前執行，導致存入資料庫的 address 永遠是空字串
    - 解決方案：在 `_uiState.update` 之前用 `val address = _uiState.value.pendingAddress` 捕捉值，傳入 `recordCheckin` 用本地變數而非 state
    - 狀態：✅ 已解決
- **備註**：LocationHelper 改寫是正確但非根因；真正 Bug 是 state 讀寫順序錯誤

## 2026-05-25 13:30:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/data/location/LocationHelper.kt
- **變更摘要**：簡化地址顯示為「國家, 區/市」層級，移除街道細節
- **遇到的問題**：
  - 問題1：地址太詳細（街道+區+市+國家），用戶只要「香港, 將軍澳」
    - 解決方案：buildAddressString() 只取 subLocality/locality/subAdminArea/adminArea + countryName；Nominatim fallback 也改為從 address JSON 擷取 suburb/town/city/district/county/state 而非 display_name
    - 狀態：✅ 已解決
- **備註**：若區域名已包含國名（如 "香港"）則不重複加前綴

## 2026-05-25 14:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/data/photo/PhotoManager.kt
- **變更摘要**：新增 compressPhoto()（1080p WebP，質量遞減至 ≤200KB）+ EXIF 旋轉修正 + deletePhoto()；P1-1 + P1-2
- **遇到的問題**：
  - 無
- **備註**：照片從原圖數 MB 壓至 ≤200KB WebP，大幅節省儲存空間

## 2026-05-25 14:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/MainActivity.kt
- **變更摘要**：evidenceCaptureLauncher 先壓縮照片再存路徑 + 分析標籤；P1-1
- **遇到的問題**：
  - 無
- **備註**：壓縮改在 IO 線程非同步執行，不阻塞 UI

## 2026-05-25 14:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/screens/dashboard/DashboardViewModel.kt
- **變更摘要**：注入 PhotoManager，EvidenceRejected 時呼叫 deletePhoto() 清理暫存照；P1-2
- **遇到的問題**：
  - 無
- **備註**：PhotoManager 經 Hilt 注入，確認時保留照片，取消時刪除

## 2026-05-25 14:15:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/components/EvidenceConfirmDialog.kt
- **變更摘要**：新增 photoUri 參數，使用 ContentResolver 載入並顯示照片預覽
- **遇到的問題**：
  - 問題1：拍完證據照後看不到照片，只能看 AI 標籤
    - 解決方案：從 URI 透過 ContentResolver 解碼 Bitmap，在對話框顯示預覽
    - 狀態：✅ 已解決
- **備註**：同時修正 AchievementDetailDialog.decodeFile(URI) → ContentResolver

## 2026-05-25 14:15:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/components/AchievementDetailDialog.kt
- **變更摘要**：修正 Bitmap 載入方式：BitmapFactory.decodeFile(URI) → ContentResolver.openInputStream
- **遇到的問題**：
  - 問題1：decodeFile() 傳入 content:// URI 無法載入照片
    - 解決方案：改為 Uri.parse() + contentResolver.openInputStream() + BitmapFactory.decodeStream()
    - 狀態：✅ 已解決
- **備註**：無

## 2026-05-25 14:30:00 操作類型：新增
- **文件路徑**：app/src/main/java/com/earthonline/app/data/local/entity/PetEntity.kt
- **變更摘要**：建立寵物 Room Entity（id=1，name 可自訂）
- **遇到的問題**：無
- **備註**：P1-3 寵物系統 v1

## 2026-05-25 14:30:00 操作類型：新增
- **文件路徑**：app/src/main/java/com/earthonline/app/data/local/dao/PetDao.kt
- **變更摘要**：PetDao：save() + get()，單一寵物 CRUD
- **遇到的問題**：無
- **備註**：P1-3

## 2026-05-25 14:30:00 操作類型：新增
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/screens/dashboard/PetCard.kt
- **變更摘要**：寵物卡片 UI：隨機 emoji + 5 條屬性進度條 + 點擊名字可改名
- **遇到的問題**：無
- **備註**：P1-3

## 2026-05-25 14:30:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/data/repository/AchievementRepository.kt
- **變更摘要**：新增 getPet()/renamePet()/computePetStats()，根據成就分類計算五維屬性
- **遇到的問題**：無
- **備註**：屬性從成就 rewardPoints 加總：epic/ocean→力量, explore→敏捷, career→智力, daily→魅力, health/transport→體力

## 2026-05-25 14:30:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/data/local/AppDatabase.kt
- **變更摘要**：version 7→8，加入 PetEntity + PetDao
- **遇到的問題**：無
- **備註**：fallbackToDestructiveMigration 自動處理

## 2026-05-25 14:30:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/di/DatabaseModule.kt
- **變更摘要**：新增 providePetDao()
- **遇到的問題**：無
- **備註**：無

## 2026-05-25 14:30:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/screens/dashboard/DashboardUiState.kt
- **變更摘要**：新增 PetUiState（name/level/5 stats）+ DashboardEvent.RenamePet
- **遇到的問題**：無
- **備註**：P1-3

## 2026-05-25 14:30:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/screens/dashboard/DashboardViewModel.kt
- **變更摘要**：loadAchievementDisplay() 加入 computePetStats()；onEvent 加入 RenamePet 處理
- **遇到的問題**：無
- **備註**：P1-3

## 2026-05-25 14:30:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/screens/dashboard/DashboardScreen.kt
- **變更摘要**：在等級面板下方插入 PetCard
- **遇到的問題**：無
- **備註**：P1-3

## 2026-05-25 14:40:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/data/repository/AchievementRepository.kt
- **變更摘要**：修正 roundToInt 未解析引用與呼叫語法錯誤
- **遇到的問題**：
  - 問題1：`roundToInt` 未解析引用（Unresolved reference: roundToInt）
    - 解決方案：新增 `import kotlin.math.roundToInt` 導入
    - 狀態：✅ 已解決
  - 問題2：`roundToInt` 仍報未解析，因採用 `roundToInt(value)` 頂層函數語法而非擴展函數語法，且 Float 類型需要擴展調用
    - 解決方案：改寫為 `(strengthRaw / divisor).roundToInt()` 擴展函數語法
    - 狀態：✅ 已解決
- **備註**：無

## 2026-05-25 14:50:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/data/backup/BackupManager.kt
- **變更摘要**：備份導出/導入加入寵物資料（name/emoji/level/xp/5stats）
- **遇到的問題**：
  - 問題1：JSON 備份未包含寵物資料，恢復時寵物遺失
    - 解決方案：注入 PetDao，導出時 put("pet", JSONObject)，導入時解析並 `petDao.save()`
    - 狀態：✅ 已解決
- **備註**：無

## 2026-05-25 15:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/data/local/entity/AchievementEvidence.kt
- **變更摘要**：PK 從 achievementId 改為自增 id: Long = 0，支持一成就保留多證據照
- **遇到的問題**：無
- **備註**：為「閱讀10本書」等多次進度成就提供每步單獨拍照保存

## 2026-05-25 15:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/data/local/dao/AchievementEvidenceDao.kt
- **變更摘要**：getByAchievement 簽名改為回傳 List；新增 getLatestByAchievement() 與 getAllByUser()
- **遇到的問題**：無
- **備註**：配合 BackupManager 導出全部證據照

## 2026-05-25 15:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/components/AchievementDetailDialog.kt
- **變更摘要**：支持滾動多張證據照預覽（高 300dp 限制），修復 loadBitmap @Composable 警告與 heightIn 導入
- **遇到的問題**：
  - 問題1：在非 Composable 函數內調用 remember 觸發編譯錯誤
    - 解決方案：為 `loadBitmap` 加上 `@Composable` 註解
    - 狀態：✅ 已解決
  - 問題2：多照片時 Dialog 太高溢出、無法看全
    - 解決方案：使用 `Modifier.heightIn(max = 300.dp).verticalScroll` 使照片區可滾動，收起按鈕固定在底部
    - 狀態：✅ 已解決
  - 問題3：`heightIn` 未解析引用
    - 解決方案：補上 `import androidx.compose.foundation.layout.heightIn`
    - 狀態：✅ 已解決
- **備註**：無

## 2026-05-25 15:15:00 操作類型：修改
- **文件路徑**：TODO.md
- **變更摘要**：在 TODO.md 與 todowrite 注入 Phase 1.5 - UI Rebuild 大重構計劃，調整中優先 Phase 2 排程
- **遇到的問題**：無
- **備註**：無

## 2026-05-25 16:00:00 操作類型：新增
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/navigation/Screen.kt
- **變更摘要**：建立 Screen sealed class 定義 4 個底部導航目標（Dashboard / Achievements / History / Settings）
- **遇到的問題**：無
- **備註**：U-1 導航系統搭建

## 2026-05-25 16:00:00 操作類型：新增
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/navigation/AppNavigation.kt
- **變更摘要**：實作 AppNavigation — NavHost + BottomNavBar + 4 頁籤導航，承擔 Onboarding 邏輯管理
- **遇到的問題**：無
- **備註**：U-1 + U-2 + U-5 一併完成

## 2026-05-25 16:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/ui/screens/dashboard/DashboardScreen.kt
- **變更摘要**：移除 flag-based 導航（showHistory/showSettings），新增 Nav 回調參數，加入 showOnlyAchievementWall 模式供成就 Tab 使用
- **遇到的問題**：無
- **備註**：U-1 完成後 DashboardScreen 不再包含 History/Settings 條件渲染

## 2026-05-25 16:00:00 操作類型：修改
- **文件路徑**：app/src/main/java/com/earthonline/app/MainActivity.kt
- **變更摘要**：改呼叫 AppNavigation 而非直接渲染 DashboardScreen
- **遇到的問題**：無
- **備註**：U-1
