# 🌍 地球 Online — 開發任務

> **總進度**：▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░ 32/39 完成 (82%)

---

## ⏳ 待辦 (Phase 2 — 中優先)

| # | 任務 | 類型 | 說明 |
|---|---|---|---|
| P2-1 | Health Connect 重接 | API | 步數 / 睡眠 / 運動時長 / 卡路里 |
| P2-2 | 活動識別 | API | Activity Recognition（步行 / 跑步 / 騎行 / 駕駛） |
| P2-3 | 藍牙社交偵測 | API | BluetoothAdapter 掃描附近裝置數 |
| P2-4 | 日曆事件 | API | CalendarContract（聚會 / 旅行 / 會議） |
| P2-5 | 故事化等級面板 | UI | 地球地圖解鎖區域 + ContinentMapper 擴充 |
| P2-6 | 寵物 v2 互動 | UI | 隨機氣泡對話深化 + 點擊反饋 |
| P2-7 | 寵物 v2 商店 | UI | 裝飾商店 + 貨幣系統（金幣打卡獲得） |

---

## 📋 待辦 (Phase 3 — 低優先)

| # | 任務 | 類型 |
|---|---|---|
| P3-1 | IAP 裝飾商店 | Google Play Billing |
| P3-2 | 本地自訂成就 | 新 DAO + UI |
| P3-3 | 成就擴充路線 | 0 → 300 → 1000+ |
| P3-4 | 足跡地圖 | react-leaflet + 霓虹標記 |
| P3-5 | Google Drive 備份 | AppData 雲端儲存 |
| P3-6 | 語言切換 | 英文版 / 多語言 |
| P3-7 | ProGuard | R8 代碼混淆 |

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
|---|---|---|
| U-1 | 導航系統搭建 | Screen sealed class + NavHost + 4 頁籤 |
| U-2 | RPG HUD 底部導航列 | 滑動指示器 + scale 動畫 + 頁面轉場 |
| U-3 | 狀態面板重構 | 角色卡 + 寵物 + 快速打卡 + 里程碑提示 |
| U-4 | 任務日誌獨立頁 | 九頁籤成就牆 + HorizontalPager |
| U-5 | 足跡與設定頁解耦 | 獨立 Nav 節點 + 返回鍵 |
| U-6 | 視覺與動畫打磨 | Tab 轉場 / 寵物彈跳 / 氣泡對話 / Shimmer |

### Phase 1.6 — 淺色主題與修復

| # | 任務 | 說明 |
|---|---|---|
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
