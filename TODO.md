# 🌍 地球 Online — 開發任務

> **總進度**：▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░ 47/55 完成 (85%)

---

## ✅ 規則重構 (Phase 1-4) — 全部完成

| Phase | 規則 | 內容 | 狀態 |
|-------|------|------|:--:|
| **1** | 規則六 | 14 處靜默吞異常 → `Log.e(TAG, msg, e)` | ✅ |
| **2** | 規則四 | 4 個長函數分解為 23 個小型函數 | ✅ |
| **3** | 規則五 | 3 個測試檔，16 個測試案例 | ✅ |
| **4** | 規則十一 | 自我檢查：無密鑰、無 TODO、等 | ✅ |

---

## 🔴 Phase 1.8 — Google Play 合規修復

| # | 任務 | 說明 | 狀態 |
|---|------|------|:--:|
| **C-1** | 更新隱私權政策 | PRIVACY.md + privacy.html：加入活動識別/Nominatim/ML Kit 說明 | ✅ |
| **C-2** | 修正 allowBackup | 改為 false，避免自動備份上傳 | ✅ |
| **C-3** | 移除 usesCleartextTraffic | 已無需明文 HTTP，移除安全風險 | ✅ |
| **C-4** | 移除未用權限 READ_MEDIA_IMAGES | Manifest 有宣告但從未使用 | ✅ |
| **C-5** | 隱私權政策公開 URL | https://skps00.github.io/Earth_Online/privacy.html | ✅ |
| **C-6** | P2-2a 權限請求對話框 | A+B+C：ActivityPermissionDialog + Dashboard 提示 + Settings 開關 | ✅ |
| **C-7** | clearAllData 含照片清除 | 刪除 filesDir/photos/ 孤兒檔案 | ✅ |
| **C-8** | Settings 加入隱私權政策連結 | 設定頁可點擊連結到隱私權政策 | ✅ |
| **C-9** | EXIF GPS 剝離 | WebP 轉換已自動移除 + 原 JPEG 已刪除，不需額外處理 | ✅ |

---

## 🔴 Phase 1.7 — Stitch 設計系統套用

| # | 任務 | 說明 | 狀態 |
|---|------|------|:--:|
| **S-1** | 字型 Press Start 2P | 標題/成就名套用像素字型 | ⬜ |
| **S-2** | 調色板更新 | Dark: `#161308` bg / `#FFF6DF` gold | ⬜ |
| **S-3** | 稀有度邊框規範化 | Common 無/Rare `#4169E1` 1dp/Epic `#800080` 1.5dp/Legendary `#FFD700` 2dp+glow | ⬜ |
| **S-4** | 動畫參數標準化 | 按鈕 `scale(0.95)` 100ms / 成就 `spring(0.6)` 500ms | ⬜ |
| **S-5** | 卡片陰影/深度 | Dark: inner shadow / Light: elevation 2dp | ⬜ |
| **S-6** | 自訂相機畫面 | CameraX RPG 風格 + Reticle + 閃光燈/相簿/格線 | ✅ |

---

## 🟡 Phase 2 — 中優先

| # | 任務 | 說明 | 狀態 |
|---|------|------|:--:|
| P2-1 | Health Connect 重接 | 步數 / 睡眠 / 運動時長 / 卡路里 | ⏸️ |
| P2-2 | 活動識別 | Activity Recognition — 走路/騎行/駕駛自動偵測 | ✅ |
| P2-3 | 藍牙社交偵測 | ~~BluetoothAdapter 掃描~~ — 實用性不足以自動偵測社交場合 | ❌ |
>
> ❌ P2-3 已移除 — 6 個社交相關成就全部無法用藍牙裝置數推斷。4 個可透過 P2-4 日曆事件處理（會議/約會/演唱會/簡報）。其餘保持手動確認。 | | |
| P2-4 | 日曆事件 | CalendarContract — 聚會/旅行/會議（含社交成就自動化） | ⬜ |
| P2-5 | 故事化等級面板 | 地球地圖解鎖區域 + ContinentMapper 擴充 | ⬜ |
| P2-6 | 寵物 v2 互動 | 隨機氣泡對話深化 + 點擊反饋 | ⬜ |
| P2-7 | 寵物 v2 商店 | 裝飾商店 + 貨幣系統（金幣打卡獲得） | ⬜ |

### P2-2 剩餘工作 — 全部完成

| # | 任務 | 說明 | 狀態 |
|---|------|------|:--:|
| P2-2a | 權限請求對話框 | A+B+C：對話框 + Dashboard 提示 + Settings 開關 | ✅ |
| P2-2b | 隱私權政策更新 | PRIVACY.md 已加入活動識別說明（C-1 一併處理） | ✅ |

> ⏸️ P2-1 — Health Connect 暫停。Samsung S23U 上 `getOrCreate()` 拋 `IllegalStateException`，`1.1.0` 需 compileSdk 36+AGP 8.9。見 `code_change_log.md`。

---

## 🟢 Phase 3 — 低優先

| # | 任務 |
|---|------|
| P3-1 | IAP 裝飾商店 (Google Play Billing) |
| P3-2 | 本地自訂成就 (新 DAO + UI) |
| P3-3 | 成就擴充路線 (0 → 300 → 1000+) |
| P3-4 | 足跡地圖 |
| P3-5 | Google Drive 備份 |
| P3-6 | 語言切換 |
| P3-7 | ProGuard (R8) |

### 雲端方案（待決定）

| 方案 | 說明 | 工時 |
|---|---|---|
| A | Firestore 完全替代 Room | 3-5 天 |
| B | Room 主 + Firestore 同步 | 2-3 天 |
| C | 只遷照片 (Firebase Storage) | 0.5-1 天 |

---

## ✅ 已完成

### Phase 1.5 — UI/UX 大重構

| # | 任務 | 說明 |
|---|------|------|
| U-1 | 導航系統搭建 | Screen sealed class + NavHost + 4 頁籤 |
| U-2 | RPG HUD 底部導航列 | 滑動指示器 + scale 動畫 + 頁面轉場 |
| U-3 | 狀態面板重構 | 角色卡 + 寵物 + 快速打卡 + 里程碑提示 |
| U-4 | 任務日誌獨立頁 | 九頁籤成就牆 + HorizontalPager |
| U-5 | 足跡與設定頁解耦 | 獨立 Nav 節點 + 返回鍵 |
| U-6 | 視覺與動畫打磨 | Tab 轉場 / 寵物彈跳 / 氣泡對話 / Shimmer |

### Phase 1.6 — 淺色主題與修復

| # | 任務 | 說明 |
|---|------|------|
| T-1 | 設計系統標準化 | 語義色 + 字型系統 + 動畫常數 |
| T-2 | 淺色/深色主題切換 | 下拉選單 + colorScheme 全局適配 |
| T-3 | 空狀態元件 | EmptyState + ShimmerEffect + ErrorState |
| T-4 | 硬編碼顏色清理 | 14 檔案 → `MaterialTheme.colorScheme.*` |
| T-5 | 分享按鈕條件修復 | 未解鎖隱藏「分享成就」 |
| T-6 | 成就卡背景適配 | `AchievementLocked` → `surfaceVariant` |
| T-7 | 文字顏色適配 | `Gold` / `Color.White` → `primary` / `onSurfaceVariant` |
| T-8 | 硬編碼字串清理 | Screen / LocationHelper / Dashboard / History |
| T-9 | CodeGraph 整合 | MCP Server + 72 files / 1,192 nodes |
| T-10 | 指示器對齊修復 | weight 佈局 + AnimatedContent |
| T-11 | 主題下拉選單 | Switch → ExposedDropdownMenuBox |
| T-12 | 深色按鈕清理 | AchievementLocked 全清除 |
| T-13 | 成就頁籤文字修復 | Color.White → onSurfaceVariant |
| T-14 | 隱藏成就文字適配 | Rarity.LEGENDARY → onSurfaceVariant |

### 基礎建設（前期完成）

| 項目 |
|---|
| 打卡系統 (GPS + Geocoder + Nominatim fallback) |
| 129 成就定義 (86 + 43 社群) |
| Room 資料庫 (7 entities + 5 DAOs) |
| 5 維寵物系統 + 等級系統 |
| 照片管理 (1080p WebP ≤200KB + EXIF 旋轉) |
| 成就解鎖螢火蟲粒子特效 |
| 隱藏成就 + 4 級稀有度 |
| JSON 備份匯出/匯入 |
| Onboarding 首次引導 (3 頁) |
| 隱私權政策 + 商店文案 |
| Hilt DI + MVVM 架構 |
| RPG 風格自訂相機畫面 (CameraX + Reticle) |
| Activity Recognition 活動識別 (Walking/Biking/Driving) |
