# 🌍 地球 Online — 開發任務

> **總進度**：▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░ 47/54 (87%)

---

## 🔴 Phase 1.7 — Stitch 設計系統

| # | 任務 | 說明 | 狀態 |
|---|------|------|:--:|
| S-1 | 字型 Press Start 2P | 標題 / 成就名套用像素字型 | ⬜ |
| S-2 | 調色板更新 | Dark: `#161308` bg / `#FFF6DF` gold | ⬜ |
| S-3 | 稀有度邊框規範化 | Common 無 / Rare `#4169E1` 1dp / Epic `#800080` 1.5dp / Legendary `#FFD700` 2dp+glow | ⬜ |
| S-4 | 動畫參數標準化 | 按鈕 `scale(0.95)` 100ms / 成就 `spring(0.6)` 500ms / Crossfade 300ms | ⬜ |
| S-5 | 卡片陰影 / 深度 | Dark: inner shadow / Light: Material 3 elevation 2dp | ⬜ |
| S-6 | 自訂相機畫面 | CameraX RPG 風格 + Reticle + 閃光燈 / 相簿 / 格線 | ✅ |

> ⏸️ 純視覺改動全部暫緩 — 等待全新 UI 設計完成

---

## 🟡 Phase 2 — 功能開發

| # | 任務 | 說明 | 狀態 |
|---|------|------|:--:|
| P2-1 | Health Connect | 步數 / 睡眠 / 運動 / 卡路里 | ⏸️ |
| P2-2 | 活動識別 | Activity Recognition — 走路 / 騎行 / 駕駛 | ✅ |
| P2-3 | 藍牙社交偵測 | 藍牙裝置數無法可靠推斷社交場合 | ❌ |
| P2-4 | 日曆事件 | 僅 1 成就可靠 + READ_CALENDAR 為高風險權限 | ❌ |
| P2-5 | 故事化等級面板 | 地球地圖解鎖區域 + ContinentMapper | ⬜ |
| P2-6 | 寵物 v2 互動 | 隨機對話深化 + 點擊反饋 | ⬜ |
| P2-7 | 寵物 v2 商店 | 裝飾商店 + 貨幣系統 | ⬜ |

> ⏸️ P2-1：Samsung S23U 上 `getOrCreate()` 拋 `IllegalStateException`，需 compileSdk 36+AGP 8.9。見 `code_change_log.md`  
> ❌ P2-3：社交成就無法用藍牙偵測。Calendar 也無法可靠處理。保持手動  
> ❌ P2-4：僅 1 成就（會議地獄）可自動化，CP 值不足

### 🆕 新方向（成就審計）

| # | 方向 | API | 成就數 | 權限 | 狀態 |
|---|------|-----|:--:|------|:--:|
| N-1 | Screen Time | UsageStatsManager | 4-7 | PACKAGE_USAGE_STATS | ⬜ |
| N-2 | 氣壓計 | TYPE_PRESSURE sensor | 2 | 無 | ⬜ |
| N-3 | 天氣 / 地震 API | OpenWeatherMap + USGS | 3 | 網路 | ⬜ |

---

## 🟢 Phase 3 — 低優先

| # | 任務 | 說明 | 狀態 |
|---|------|------|:--:|
| P3-1 | IAP 裝飾商店 | Google Play Billing | ⬜ |
| P3-2 | 本地自訂成就 | 新 DAO + UI | ⬜ |
| P3-3 | 成就擴充路線 | 0 → 300 → 1000+ | ⬜ |
| P3-4 | 足跡地圖 | 互動式打卡地圖 | ⬜ |
| P3-5 | Google Drive 備份 | AppData 雲端儲存 | ⬜ |
| P3-6 | 語言切換 | 英文版 / 多語言 | ⬜ |
| P3-7 | ProGuard | R8 代碼混淆 | ⬜ |

### 雲端方案（待決定）

| 方案 | 說明 | 工時 |
|---|---|:--:|
| A | Firestore 完全替代 Room | 3-5 天 |
| B | Room 主 + Firestore 同步 | 2-3 天 |
| C | 只遷照片 (Firebase Storage) | 0.5-1 天 |

---

## ✅ 已完成

### Phase 1.8 — Google Play 合規

| # | 任務 | 說明 |
|---|------|------|
| C-1 | 更新隱私權政策 | PRIVACY.md + privacy.html：活動識別 / Nominatim / ML Kit |
| C-2 | 修正 allowBackup | 改為 false |
| C-3 | 移除 usesCleartextTraffic | 安全風險 |
| C-4 | 移除 READ_MEDIA_IMAGES | 未使用權限 |
| C-5 | 隱私政策公開 URL | https://skps00.github.io/Earth_Online/privacy.html |
| C-6 | P2-2a 權限對話框 | A+B+C：ActivityPermissionDialog + Dashboard + Settings |
| C-7 | clearAllData 含照片清除 | 刪除 filesDir/photos/ |
| C-8 | Settings 隱私權政策連結 | Shield icon → 瀏覽器打開 |
| C-9 | EXIF GPS 剝離 | WebP 轉換已自動處理 |

### 規則重構 (Phase 1-4)

| Phase | 內容 |
|:--:|------|
| 1 | 14 處 `catch {}` → `Log.e(TAG, msg, e)` |
| 2 | 4 長函數 → 23 小型函數（≤30 行） |
| 3 | 3 測試檔 / 21 測試案例 |
| 4 | 自我檢查：無密鑰、無 TODO、命名合規 |

### Phase 1.6 — 淺色主題與修復

| # | 任務 |
|---|------|
| T-1~T-14 | 設計系統 / 雙主題 / 空狀態 / 顏色清理 / 字串清理 / CodeGraph |

### Phase 1.5 — UI/UX 大重構

| # | 任務 |
|---|------|
| U-1~U-6 | 導航 / BottomBar / 狀態面板 / 成就牆 / 解耦 / 動畫打磨 |

### 基礎建設

| 項目 |
|---|
| 打卡系統 (GPS + Geocoder + Nominatim) |
| 129 成就 (86 + 43 社群) |
| Room 資料庫 (7 entities / 5 DAOs) |
| 5 維寵物系統 + 玩家等級 |
| 照片管理 (1080p WebP ≤200KB + EXIF) |
| 螢火蟲粒子解鎖動畫 |
| 隱藏成就 + 4 級稀有度 glow |
| JSON 備份匯出 / 匯入 |
| Onboarding 首次引導 (3 頁) |
| 隱私權政策 + 商店文案 |
| Hilt DI + MVVM |
| RPG 風格自訂相機 (CameraX + Reticle) |
| Activity Recognition (Walking / Biking / Driving) |
| 9 成就 AUTO_TRACK + 中英文地名支援 |
