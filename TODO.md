# 地球 Online - 代辦事項

## V3 已完成 ✅
- [x] 打卡系統（GPS + Geocoder 反編碼 + 打卡確認對話框）
- [x] 86 個成就（6 打卡 + 31 探索 + 10 職涯 + 11 日常 + 9 史詩 + 6 健康 + 5 交通 + 5 大洋 + 3 大洲）
- [x] 手動成就多步進度（世界奇觀 7 次、環球旅人 50 次等）
- [x] ML Kit Image Labeling（裝置端照片分析證據）
- [x] 7 頁籤成就牆（LazyRow + HorizontalPager）
- [x] 成就解鎖動畫 + 音效
- [x] Room DB v6（4 表，移除 FK CASCADE）
- [x] GitHub 版本控制
- [x] 探索成就自動追蹤（國家數/大洲數由打卡數據自動更新）
- [x] 成就分享功能（生成 1080x1080 分享卡 PNG + 文字）
- [x] 國家→大洲對照表（Geocoder 反編碼後自動歸類大洲）
- [x] 成就詳情對話框排版優化（關閉鈕置底、按鈕等寬）
- [x] 證據照片顯示（已解鎖成就詳情顯示拍照存證圖片）

---

## 🔴 高優先

### 實機完整測試
- [x] 打卡流程測試（權限請求 → GPS 取得 → 地址顯示 → 確認打卡 → 成就觸發）
- [x] 手動成就多步進度測試（多次認領 → 進度條更新 → 達到門檻解鎖）
- [x] 成就牆分頁測試（7 頁籤切換 → 滑動 → 點擊卡片）
- [x] 多國家打卡測試（auto-track 5/10/50 國是否正確觸發）
- [x] 資料庫遷移測試（v1 → v4 fallbackToDestructiveMigration）— 成功，但成就牆需打卡後才刷新
- [x] **已知 Bug：重開 App 後成就牆顯示未解鎖** — 根本原因：FK ON DELETE CASCADE + REPLACE 每次啟動刪除所有進度。修復：移除 CASCADE，DB v6
- [x] 不同 Android 版本相容性測試（SDK 26 ~ 34）— 無其他裝置，跳過
- [x] 音效測試 — 解鎖時正常播放
- [ ] ML Kit 照片分析測試（暫移除，不穩定）

### 程式碼品質修正
- [x] **TriggerType 矛盾修正**：`explore_5countries` 等用 `MANUAL_CONFIRM` 但實際上被 `autoTrackExploreCountry()` 自動更新 → 開新的 `AUTO_TRACK` TriggerType
- [x] **DAO method rename**：`getUnlockedByUserAndType()` → `getPendingByUserAndType()`
- [x] **ViewModel error 靜默修正**：`initialize()` 的 `catch { }` 空塊 → 改為寫入 `DashboardUiState` error state
- [x] **MediaPlayer 多個同時播放**：`AchievementUnlockDialog` 內直接 create MediaPlayer，快速連續解鎖可能多個重疊 → 抽成 shared static instance

---

## 🟡 中優先

### 七大洋成就
- [x] 新增探索成就：太平洋、大西洋、印度洋、北冰洋、南冰洋（隱藏成就）
- [x] 設計為手動認領成就（附提示）

### 打卡歷史地圖
- [ ] 加入地圖功能 — 已嘗試 osmdroid、WebView+Leaflet、Google Maps，全遇阻礙（無 API 金鑰 / 渲染問題）。**暫緩**，改為打卡歷史清單方案
- [ ] 打卡歷史清單：按國家分組，顯示時間和地址

### 打卡歷史清單
- [ ] 新增打卡歷史頁面（按國家分組的打卡記錄清單）
- [ ] Dashboard 新增「打卡記錄」按鈕導向歷史頁面

### 成就系統擴充
- [ ] 加入更多社群成就（r/outside 社群持續更新）
- [x] 隱藏成就（達成條件前不顯示標題和說明）
- [x] 成就稀有度分級（普通 / 稀有 / 史詩 / 傳說，不同顏色邊框）
- [x] 成就定義抽成 JSON 資源檔 → 改為 `AchievementSeedData.kt`
- [x] JSON 備份匯出/匯入 — 使用者換機必備
- [ ] **千條成就擴充路線**：0–300 SeedData → 300–1000 拆多檔案 → 1000+ 介面抽象+後端推送

---

## 🟢 低優先

### 設定頁面
- [ ] 音效開關
- [ ] 權限管理入口
- [ ] 清除所有資料按鈕
- [ ] 匯出/匯入成就進度

### 技術債清理
- [ ] `userId = "local_user"` 抽成 `UserRepository` 或 `AppConfig`
- [ ] Room `exportSchema = true` + `ksp { arg("room.schemaLocation", ...) }` 啟用 migration 驗證
- [ ] Evidence 照片垃圾清理（取消確認時刪除暫存照，啟動時掃 orphan files）
- [ ] **照片壓縮機制**：儲存前等比例縮放至 1080p + JPEG/WebP 壓縮（單張控制在 200KB 內）
- [ ] **快取清理/封存**：允許使用者解鎖成就後，選擇「只保留記錄，刪除老照片」
- [ ] **Version Catalogs**：`build.gradle.kts` 硬編碼改為 `libs.versions.toml`

### UI 打磨
- [ ] 過場動畫（頁籤切換 Crossfade）
- [ ] 空狀態提示（尚無打卡時顯示引導文字）
- [ ] 載入骨架屏（Shimmer loading）
- [ ] 下拉刷新打卡數據
- [ ] 成就牆頁籤選中動畫（smooth indicator transition）
- [ ] 打卡確認對話框顯示國家/大洲資訊
- [ ] 分享卡片優化（加入打卡位置資訊、自訂背景圖）
- [ ] Dark mode / Light mode 切換
- [ ] 主畫面儀表板重新設計（打卡次數視覺化圖表）
- [ ] 成就解鎖 Toast 替代（底部 snackbar 或橫幅）
- [ ] 首次使用引導（Onboarding 頁面）

### 語言功能
- [ ] 語言切換功能修復後加回
- [ ] 英文版字串資源（values-en）

### 其他
- [ ] Lottie 粒子特效 JSON 動畫檔（螢火蟲光點，目前用 "★" + shimmer alpha 模擬）
- [ ] 步數成就重新評估後重新實作（前期已有 `HealthConnectManager.kt`，非從零開發）
- [ ] 方案 C：自訂 TensorFlow Lite 食物模型
- [ ] ProGuard / R8 規則驗證（ML Kit、Geocoder 等第三方庫混淆後正常運作）
- [ ] Android 13+ (SDK 33+) `POST_NOTIFICATIONS` 權限（若未來加入背景提醒）
- [ ] **Google Timeline 自動打卡**：整合 Google Sign-In + Maps Platform API，讀取位置歷史來自動解鎖國家/大洲成就，取代手動打卡（需 OAuth 審核，開發量大）
- [ ] **Google Drive AppData 雲端儲存**：將 Room 換成 Google Drive AppData 隱藏資料夾，資料跟 Google 帳號走，換機不遺失，開發者零存取，零伺服器成本

### Google Play 上架準備
- [ ] 隱私權政策網址（即使資料全存本地，有相機/定位權限就必須提供）
- [ ] App 圖示（目前為自訂向量圖，上架建議 1024x1024 PNG）
- [ ] 商店文案（標題、短描述、長描述、螢幕截圖）
- [ ] 內容分級問卷（Google Play Console 必填）
- [ ] 測試用帳號（若有後端功能）

---

## 📊 優先級路線圖（更新）

1. **地圖標記**（OSM > Google Maps）— 符合開源+隱私 ethos
2. **隱藏成就 + 稀有度分級** ✅
3. **JSON 備份匯出/匯入** — 認真用的使用者需要
4. **音效開關 + 設定頁** — 使用者體驗細節
5. **Lottie 粒子特效** — 螢火蟲光點，加分項

---

## ✅ 17 項重構完成

DashboardScreen 666→~300 行，刪除 AchievementService，新增 10 個專職檔案：
AchievementCard、AchievementDetailDialog、CheckInConfirmDialog、EvidenceConfirmDialog、
SoundPlayer、ContinentMapper、AchievementTriggers、Rarity、AchievementSeedData、
AchievementCategories、AchievementDisplayMapper、CheckInCoordinator、ShareHelper

---
