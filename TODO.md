# 地球 Online - 代辦事項

## ✅ 已完成
- [x] 打卡系統（GPS + Geocoder + 打卡確認）
- [x] 86 成就（9 分類：打卡/探索/職涯/日常/史詩/健康/交通/大洋/大洲）
- [x] 多步進度 + AUTO_TRACK 自動追蹤 + 國家/大洲特定自動解鎖
- [x] 隱藏成就 + 稀有度分級（普通/稀有/史詩/傳說，邊框顏色）
- [x] 9 頁籤成就牆（LazyRow + HorizontalPager）
- [x] 成就解鎖動畫 + 音效 + 對話框重啟
- [x] 分享卡 PNG 生成（1080x1080）
- [x] 拍照證據 + ML Kit 分析（暫移除不穩定）
- [x] JSON 備份匯出/匯入（SAF 檔案選擇器）
- [x] Room DB v6（4 表，FK CASCADE 修復）
- [x] 17 項重構（Dashboard 666→~300 行，10 個新組件）
- [x] FK ON DELETE CASCADE bug 修復（成就不再重設）
- [x] GitHub 版本控制（100+ commits）

---

## 🔴 高優先

### Bug
- [ ] 打卡歷史記錄僅顯示國家，無法顯示街道地址 — 已試 Android Geocoder / Nominatim / getAddressLine(0)，仍僅解析到國家級別，待修

---

## 🟡 中優先

### 打卡歷史清單
- [x] 新增頁面，按國家分組顯示所有打卡記錄（地址 + 時間）
- [x] Dashboard 新增「記錄」按鈕

### 成就系統
- [ ] 加入更多社群成就
- [ ] 千條成就擴充路線（0–300 SeedData → 300–1000 拆檔案 → 1000+ 抽象架構）

---

## 🟢 低優先

### 設定頁面
- [ ] 音效開關
- [ ] 權限管理入口
- [ ] 清除所有資料

### UI 打磨
- [ ] 打卡確認對話框顯示國家/大洲
- [ ] 首次使用引導（Onboarding）
- [ ] 空狀態提示（無打卡時引導文字）
- [ ] Lottie 螢火蟲粒子特效

### 技術債
- [ ] 照片壓縮機制（1080p + WebP，單張 ≤200KB）
- [ ] Evidence 照片清理（取消時刪除暫存照）
- [ ] Room exportSchema + Version Catalogs
- [ ] ProGuard / R8 規則驗證
- [ ] **消除硬編碼**：
  - [ ] `"local_user"` 重複 24 次 → 抽出 `AppConstants.LOCAL_USER_ID`
  - [ ] 35+ 個中文字串 → 放入 `strings.xml`（設定頁、歷史頁、等級面板）
  - [ ] `Color(0xFF1A1A2E)` 重複 11 次 → 改用既有 `DeepBlue`
  - [ ] `Color(0xFF1E1E3A)` 重複 4 次 → 新增 `DialogDark`
  - [ ] `"earth_online.db"` / `"earth_online_settings"` 重複 → 常數化
  - [ ] `SimpleDateFormat("yyyy/MM/dd HH:mm")` 重複 → 共用工具
  - [ ] 魔法數字（3500ms、0.92f、60.dp 等）→ 具名常數

### Google Play 上架準備
- [ ] 隱私權政策網址
- [ ] App 圖示 PNG（1024x1024）
- [ ] 商店文案 + 螢幕截圖
- [ ] 內容分級問卷

### 🟡 階段一：寵物基礎
- [ ] `PetEntity` Room 表（名稱、經驗值、攻擊/防禦/速度 等屬性）
- [ ] Dashboard 寵物區塊（2D Lottie 動畫）
- [ ] 屬性公式：各分類成就解鎖數加權（🗺️→體力, 🎓→智力, 🏆→幸運...）
- [ ] 總成就點數 → 寵物經驗值 → 寵物等級
- [ ] 技術：2D + Lottie（已有依賴，APK 體積小，相容性高）

### 🟢 階段二：裝飾商店
- [ ] 成就點數作為貨幣系統
- [ ] 裝飾定義表（品名、價格、圖示、部位）
- [ ] 購買裝飾 → 扣除點數 → 已擁有列表
- [ ] 寵物穿戴 → Dashboard 即時顯示

### 🟢 階段三：互動
- [ ] 點擊寵物動畫反應
- [ ] 寵物隨機對話（根據最近解鎖成就）

### 未來功能
- [ ] **足跡地圖**：react-leaflet 暗色地圖 + 霓虹光點標記每個打卡地點
- [ ] **排行榜**：成就數/國家數雙維度排名（需後端支援）
- [ ] 打卡歷史地圖（暫緩 — osmdroid/Leaflet/GMaps 皆未成功）
- [ ] Google Timeline 自動打卡
- [ ] Google Drive AppData 雲端儲存
- [ ] 步數成就重新實作
- [ ] 語言切換 / 英文版
