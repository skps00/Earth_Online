# 地球 Online - 代辦事項

## ✅ 已完成
- [x] 打卡系統（GPS + Geocoder + 打卡確認）
- [x] 129 成就（86 + 43 社群成就：epic/health/daily/career/explore/transport）
- [x] 多步進度 + AUTO_TRACK + 國家/大洲自動解鎖
- [x] 隱藏成就 + 稀有度分級（4 級邊框顏色）
- [x] 9 頁籤成就牆（LazyRow + HorizontalPager）
- [x] 成就解鎖動畫（頂部小卡片 + 滑動關閉）+ 音效
- [x] 螢火蟲粒子特效（8 光點環繞星星飛行）
- [x] 分享卡 PNG（1080x1080）
- [x] JSON 備份匯出/匯入（SAF，已移至設定頁）
- [x] 玩家等級系統（Lv + XP 進度條 + 統計面板）
- [x] 打卡歷史清單（按國家分組）
- [x] 設定頁面（音效開關、權限入口、清除資料、備份）
- [x] 空狀態提示（「點擊下方按鈕開始你的第一次打卡！」）
- [x] 3 頁 Onboarding 首次引導（HorizontalPager + SharedPreferences）
- [x] 17 項重構（Dashboard 666→~300 行，10+ 新組件）
- [x] 硬編碼清理（AppConstants、35+ 字串資源、Color 去重、魔法數字具名）
- [x] FK CASCADE bug 修復、BackHandler 返回鍵修復
- [x] Room DB v7 + GitHub 版本控制

---

## 🔴 Bug
- [ ] 打卡歷史記錄僅顯示國家，無法顯示街道地址（Geocoder / Nominatim 皆無效）

---

## 🟡 中優先 (Phase 2 — 1-2 個月)

### 自動偵測成就
- [ ] Health Connect 重接 → 步數/睡眠/運動成就
- [ ] 活動識別成就（Activity Recognition API：步行/跑步/騎行/駕駛）
- [ ] 藍牙社交偵測（附近裝置數 → 聚會/人群成就）
- [ ] 日曆事件成就（CalendarContract：聚會/旅行/會議觸發）

### 遊戲化
- [ ] 故事化等級面板（地球地圖解鎖區域 + ContinentMapper 擴充）

### 🐣 寵物系統
- [ ] v1: 寵物實體 + Dashboard 寵物區塊（2D Lottie）
- [ ] v1: 屬性 = 分類成就加權
- [ ] v2: 點擊互動 + 隨機對話
- [ ] v2: 裝飾商店 + 貨幣系統

---

## 🟢 低優先 (Phase 3+ — 日後)

### 技術債
- [ ] 照片壓縮（1080p WebP ≤200KB）
- [ ] Evidence 照片清理（取消時刪除暫存照）
- [ ] Room exportSchema + Version Catalogs
- [ ] ProGuard / R8

### Google Play 上架
- [ ] 隱私權政策頁面
- [ ] App 圖示 PNG
- [ ] 商店文案 + 螢幕截圖
- [ ] 內容分級問卷

### 營收與擴充
- [ ] IAP 裝飾商店（解鎖成就動畫主題）
- [ ] 本地自訂成就建立器（新 DAO + UI）
- [ ] 千條成就擴充路線（0–300 → 300–1000 → 1000+）

### 雲端遷移方案（待決定）
- [ ] 方案 B: 同步層（Room 主 + Firestore 同步）← 建議起始方案（~2-3 天）
- [ ] 方案 C: 只遷照片（Firebase Storage）← 最輕量（~0.5-1 天）
- [ ] 方案 A: 全雲端（Firestore 完全替代 Room）← 最大工程（~3-5 天）

### 未來功能
- [ ] 足跡地圖（react-leaflet + 霓虹標記）
- [ ] 排行榜（成就數/國家數，需後端）
- [ ] Google Timeline 自動打卡
- [ ] Google Drive AppData 雲端儲存
- [ ] 語言切換 / 英文版
