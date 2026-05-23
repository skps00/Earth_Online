# 地球 Online - 代辦事項

## ✅ 已完成
- [x] 打卡系統（GPS + Geocoder + 打卡確認）
- [x] 86 成就（9 分類）
- [x] 多步進度 + AUTO_TRACK + 國家/大洲自動解鎖
- [x] 隱藏成就 + 稀有度分級（4 級邊框顏色）
- [x] 9 頁籤成就牆（LazyRow + HorizontalPager）
- [x] 成就解鎖動畫（頂部小卡片 + 滑動關閉）+ 音效
- [x] 分享卡 PNG（1080x1080）
- [x] JSON 備份匯出/匯入（SAF，已移至設定頁）
- [x] 玩家等級系統（Lv + XP 進度條 + 統計面板）
- [x] 打卡歷史清單（按國家分組）
- [x] 設定頁面（音效開關、權限入口、清除資料、備份）
- [x] 17 項重構（Dashboard 666→~300 行，10+ 新組件）
- [x] 硬編碼清理（`local_user` 常數、35+ 字串資源、Color 去重、魔法數字具名）
- [x] FK CASCADE bug 修復、BackHandler 返回鍵修復
- [x] Room DB v7 + GitHub 版本控制

---

## 🔴 Bug
- [ ] 打卡歷史記錄僅顯示國家，無法顯示街道地址

---

## 🟡 中優先
- [ ] 加入更多社群成就
- [ ] 千條成就擴充路線（0–300 → 300–1000 → 1000+）

---

## 🟢 低優先

### UI 打磨
- [ ] Onboarding 首次使用引導
- [ ] 空狀態提示
- [ ] Lottie 螢火蟲粒子特效
- [ ] 打卡確認對話框顯示國家/大洲

### 技術債
- [ ] 照片壓縮（1080p + WebP）
- [ ] Evidence 照片清理
- [ ] Room exportSchema + Version Catalogs
- [ ] ProGuard / R8

### Google Play 上架
- [ ] 隱私權政策網址
- [ ] App 圖示 PNG
- [ ] 商店文案 + 螢幕截圖
- [ ] 內容分級問卷

### 🐣 寵物系統（基礎已設計，待實作）
- [ ] PetEntity + Dashboard 寵物區塊（2D Lottie）
- [ ] 屬性 = 分類成就加權
- [ ] 裝飾商店 + 貨幣系統
- [ ] 點擊互動 + 隨機對話

### 未來功能
- [ ] 足跡地圖（react-leaflet + 霓虹標記）
- [ ] 排行榜（成就數/國家數，需後端）
- [ ] Google Timeline 自動打卡
- [ ] Google Drive AppData 雲端儲存
- [ ] 語言切換 / 英文版
