# 地球 Online — 代辦事項

---

## 🔴 Phase 1 — 本週 (高優先)

| # | 任務 | 說明 |
|---|------|------|
| **Bug** | 打卡歷史地址修復 | 僅顯示國家，無法顯示街道地址（Geocoder / Nominatim 皆無效） |
| **P1-1** | 照片壓縮 | 1080p WebP，單張 ≤200KB（PhotoManager.kt） |
| **P1-2** | Evidence 照片清理 | 取消打卡時自動刪除暫存照（EvidenceConfirmDialog.kt） |
| **P1-3** | 寵物系統 v1 | 寵物實體 + Dashboard 區塊（2D Lottie）+ 屬性加權 |
| **P1-4** | Google Play 上架準備 | App 圖示 PNG / 隱私權政策 / 商店文案 / 內容分級問卷 |

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
| P2-5 | 故事化等級面板（地球地圖解鎖區域 + ContinentMapper 擴充） |

### 寵物系統 v2
| # | 任務 |
|---|------|
| P2-6 | 點擊互動 + 隨機對話 |
| P2-7 | 裝飾商店 + 貨幣系統 |

---

## 🟢 Phase 3 — 日後 (低優先)

### 技術債
- [ ] Room exportSchema + Version Catalogs
- [ ] ProGuard / R8

### 營收
- [ ] IAP 裝飾商店（解鎖成就動畫主題）
- [ ] 本地自訂成就建立器（新 DAO + UI）
- [ ] 千條成就擴充路線（0→300→1000+）

### 雲端遷移（待決定）
- [ ] 方案 B：同步層（Room 主 + Firestore 同步）← 建議，約 2-3 天
- [ ] 方案 C：只遷照片（Firebase Storage）← 最輕量，約 0.5-1 天
- [ ] 方案 A：全雲端（Firestore 完全替代 Room）← 約 3-5 天

### 未來
- [ ] 足跡地圖（react-leaflet + 霓虹標記）
- [ ] 排行榜（需後端）
- [ ] Google Drive AppData 雲端儲存
- [ ] 語言切換 / 英文版

---

## ✅ 已完成

<details>
<summary>點擊展開（共 16 項）</summary>

- [x] 打卡系統（GPS + Geocoder）
- [x] 129 成就（86 + 43 社群成就）
- [x] 多步進度 + AUTO_TRACK + 國家/大洲自動解鎖
- [x] 隱藏成就 + 稀有度分級（4 級邊框顏色）
- [x] 9 頁籤成就牆（LazyRow + HorizontalPager）
- [x] 成就解鎖動畫（頂部卡片 + 滑動關閉）+ 音效
- [x] 螢火蟲粒子特效（8 光點環繞星星飛行）
- [x] 分享卡 PNG（1080x1080）
- [x] JSON 備份匯出/匯入（SAF）
- [x] 玩家等級系統（Lv + XP 進度條 + 統計面板）
- [x] 打卡歷史清單（按國家分組）
- [x] 設定頁面（音效/權限/清除/備份）
- [x] 空狀態提示
- [x] 3 頁 Onboarding 首次引導
- [x] 17 項重構（Dashboard 666→~300 行）
- [x] 硬編碼清理（AppConstants / strings.xml / Color）
- [x] FK CASCADE bug 修復 / BackHandler 返回鍵

</details>
