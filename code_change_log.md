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
