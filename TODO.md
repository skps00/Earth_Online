# 地球 Online — 代辦事項

---

## 🔴 Phase 1 — 本週 (高優先)

| # | 任務 | 說明 | 狀態 |
|---|------|------|:--:|
| Bug | 打卡歷史地址修復 | state 讀寫順序 + 區/市層級顯示 | ✅ |
| P1-1 | 照片壓縮 | 1080p WebP ≤200KB + EXIF 旋轉 | ✅ |
| P1-2 | Evidence 照片清理 | 取消打卡時刪除暫存照 | ✅ |
| P1-3 | 寵物系統 v1 | 12 寵物可選 + 5 維權重屬性 + 備份 | ✅ |
| P1-4 | 上架準備 | 隱私政策 ✅ / 商店文案 ✅ / 圖示 ⬜ / 截圖 ⬜ | 🔶 |

> 🔶 P1-4 剩餘：App 圖示 512x512 PNG + 手機截圖（需你提供）

---

## 🟡 Phase 2 — 1-2 個月 (中優先)

### 自動偵測成就
| # | 任務 | 技術 |
|---|------|------|
| P2-1 | Health Connect 重接 | 步數 / 睡眠 / 運動時長 / 卡路里 |
| P2-2 | 活動識別 | Activity Recognition API（步行/跑步/騎行/駕駛） |
| P2-3 | 藍牙社交偵測 | BluetoothAdapter 掃描附近裝置數 → 聚會成就 |
| P2-4 | 日曆事件 | CalendarContract（聚會/旅行/會議觸發） |

### 遊戲化
| # | 任務 |
|---|------|
| P2-5 | 故事化等級面板（地球地圖解鎖區域） |

### 寵物系統 v2
| # | 任務 |
|---|------|
| P2-6 | 點擊互動 + 隨機對話 |
| P2-7 | 裝飾商店 + 貨幣系統 |

---

## 🟢 Phase 3 — 日後 (低優先)

### 營收
- [ ] IAP 裝飾商店（解鎖成就動畫主題）
- [ ] 本地自訂成就建立器（新 DAO + UI）
- [ ] 千條成就擴充路線（0→300→1000+）

### 雲端遷移（待決定）
- [ ] 方案 B：同步層（Room + Firestore）← 建議，約 2-3 天
- [ ] 方案 C：只遷照片（Firebase Storage）← 約 0.5-1 天
- [ ] 方案 A：全雲端（Firestore 替代 Room）← 約 3-5 天

### 其他
- [ ] 足跡地圖（react-leaflet + 霓虹標記）
- [ ] Google Drive AppData 雲端儲存
- [ ] 語言切換 / 英文版
- [ ] Room exportSchema / ProGuard

---

## ✅ 已完成

<details>
<summary>點擊展開（18 項）</summary>

- [x] 打卡系統（GPS + Geocoder）
- [x] 129 成就（86 + 43 社群成就）
- [x] 權重整合進 AchievementDefinitionEntity（Room）
- [x] 多步進度 + AUTO_TRACK + 國家/大洲自動解鎖
- [x] 隱藏成就 + 稀有度分級
- [x] 9 頁籤成就牆
- [x] 成就解鎖動畫 + 螢火蟲粒子特效
- [x] 照片預覽 + ContentResolver 載入修正
- [x] 分享卡 PNG
- [x] JSON 備份匯出/匯入（含寵物資料）
- [x] 玩家等級系統 + 寵物養成系統
- [x] 打卡歷史清單（地址 Bug 修復）
- [x] 設定頁面 + 空狀態提示
- [x] 3 頁 Onboarding 首次引導
- [x] 17 項重構（Dashboard 666→~300 行）
- [x] 硬編碼清理 + FK CASCADE 修復
- [x] 隱私權政策（MD + HTML）
- [x] Google Play 商店文案

</details>
