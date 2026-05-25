# 地球 Online — 代辦事項

---

## 🔴 Phase 1.5 — UI/UX 大重構 (高優先 — 進行中)

| # | 任務 | 說明 | 狀態 |
|---|------|------|:--:|
| **U-1** | 導航系統搭建 (Navigation Setup) | 引入 `navigation-compose`，建立 `Screen` 密封類與 `NavHost` | ⬜ |
| **U-2** | 賽博遊戲 HUD 底部導航列 | 實作具備霓虹與高對比遊戲風格的 `BottomNavigationBar` | ⬜ |
| **U-3** | 狀態面板重構 (Status HUD) | 重構 `DashboardScreen`，移除非必要組件，只保留：頭像、角色卡、快速打卡與**互動式寵物** | ⬜ |
| **U-4** | 任務日誌獨立頁 (Quest Log) | 建立 `AchievementsScreen`，搬移九頁籤成就牆，徹底解決 nested scrolling 衝突 | ⬜ |
| **U-5** | 足跡與設定頁解耦 | 將 `HistoryScreen` 與 `SettingsScreen` 從條件渲染改為獨立 Nav 節點，由系統接管返回鍵 | ⬜ |
| **U-6** | 視覺與動畫打磨 (Polish) | 增加 Tab 切換淡入淡出、寵物點擊彈跳動畫、與氣泡隨機對話 | ⬜ |

---

## 🟡 Phase 2 — 1-2 個月 (中優先 — 導航重構後開始)

| # | 任務 | 說明 | 狀態 |
|---|------|------|:--:|
| P2-1 | Health Connect 重接 | 步數 / 睡眠 / 運動時長 / 卡路里 | ⬜ |
| P2-2 | 活動識別 | Activity Recognition（步行/跑步/騎行/駕駛） | ⬜ |
| P2-3 | 藍牙社交偵測 | BluetoothAdapter 掃描附近裝置數 | ⬜ |
| P2-4 | 日曆事件 | CalendarContract（聚會/旅行/會議觸發） | ⬜ |
| P2-5 | 故事化等級面板 | 地球地圖解鎖區域 + ContinentMapper 擴充 | ⬜ |
| P2-6 | 寵物 v2 — 互動 | 寵物隨機氣泡對話與點擊反饋深化 | ⬜ |
| P2-7 | 寵物 v2 — 商店 | 裝飾商店 + 貨幣系統（金幣打卡獲得） | ⬜ |

---

## 🟢 Phase 3 — 日後 (低優先)

| # | 任務 | 說明 | 狀態 |
|---|------|------|:--:|
| P3-1 | IAP 裝飾商店 | 成就動畫主題購買 + Google Play Billing | ⬜ |
| P3-2 | 本地自訂成就 | 用戶自建成就（新 DAO + UI） | ⬜ |
| P3-3 | 成就擴充路線 | 0→300→1000+ 成就 | ⬜ |
| P3-4 | 足跡地圖 | react-leaflet + 霓虹標記 | ⬜ |
| P3-5 | Google Drive 備份 | AppData 雲端儲存 | ⬜ |
| P3-6 | 語言切換 | 英文版 / 多語言 | ⬜ |
| P3-7 | ProGuard | R8 代碼混淆 | ⬜ |

### 雲端遷移方案（待決定）

| # | 方案 | 說明 | 工時 |
|---|------|------|:--:|
| 雲端-B | 同步層 | Room 主 + Firestore 同步 | 2-3 天 |
| 雲端-C | 只遷照片 | Firebase Storage | 0.5-1 天 |
| 雲端-A | 全雲端 | Firestore 完全替代 Room | 3-5 天 |

---

## ✅ 已完成

<details>
<summary>點擊展開（21 項）</summary>

| # | 任務 |
|---|------|
| — | 打卡系統（GPS + Geocoder） |
| — | 129 成就（86 + 43 社群成就） |
| — | 權重整合進 AchievementDefinitionEntity（Room） |
| — | 多步進度 + AUTO_TRACK + 國家/大洲自動解鎖 |
| — | 隱藏成就 + 稀有度分級（4 級邊框） |
| — | 9 頁籤成就牆（LazyRow + HorizontalPager） |
| — | 成就解鎖動畫 + 螢火蟲粒子特效 |
| — | 照片預覽（EvidenceConfirmDialog + ContentResolver） |
| — | 照片壓縮（1080p WebP ≤200KB） |
| — | 多證據照保留（PK 改為自增 id，保留多張照片歷史，支持 DetailDialog 滾動查看）|
| — | 分享卡 PNG（1080x1080） |
| — | JSON 備份匯出/匯入（含完整寵物資料：名/emoji/等級/XP/五維） |
| — | 玩家等級系統 + 寵物養成系統 v1 |
| — | 打卡歷史清單 + 地址顯示修復 |
| — | 設定頁面 + 空狀態提示 |
| — | 3 頁 Onboarding 首次引導 |
| — | 17 項重構（Dashboard 666→~300 行） |
| — | 硬編碼清理 + FK CASCADE 修復 |
| — | 隱私權政策（MD + HTML） |
| — | Google Play 商店文案 |
| — | 修正 AAPT2 錯誤與 progress_format 格式警告 |

</details>
