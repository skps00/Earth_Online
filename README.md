# 地球 Online (Earth Online)

> 將現實生活遊戲化的 Android App — 你的人生 RPG 正在進行中

## 概述

「地球 Online」是一款 Android 原生應用程式，核心概念源自網路社群中「地球是一款 MMORPG」的梗。透過打卡記錄現實地點，自動給予如同遊戲成就系統的回饋與鼓勵。

## 當前版本 (v1.3.0)

### 打卡系統
- GPS 定位 + Geocoder 反編碼（經緯度 → 地址 + 國家 + 大洲）
- 打卡確認對話框顯示可讀地址
- 打卡紀錄儲存於本機 Room 資料庫

### 78 個成就（8 大分類）
| 分類 | 數量 | 觸發方式 |
|---|---|---|
| 📍 打卡 | 6 | 自動（不重複地點數） |
| 🗺️ 探索 | 31 | AUTO_TRACK（國家/大洲自動）+ MANUAL_CONFIRM |
| 🎓 職涯 | 10 | MANUAL_CONFIRM |
| 🎭 日常 | 11 | MANUAL_CONFIRM |
| 🏆 史詩 | 9 | MANUAL_CONFIRM |
| 🩺 健康 | 6 | MANUAL_CONFIRM |
| 🚗 交通 | 5 | MANUAL_CONFIRM |

### 成就系統
- 多步進度（世界奇觀需手動認領 7 次）
- 探索成就自動追蹤（打卡後自動計算不重複國家/大洲數）
- Minecraft 風格解鎖彈窗動畫（滑入 + 彈跳 + 縮放，3.5 秒自動淡出）
- 解鎖音效
- 7 頁籤成就牆（LazyRow + HorizontalPager）

### 拍照證據
- 手動成就可拍照存證
- 證據照片儲存於本機，成就詳情可查看
- ML Kit Image Labeling（裝置端照片分析，目前暫移除）

### 成就分享
- 生成 1080x1080 分享卡 PNG（金色主題 + 星星 + 成就資訊）
- Android 原生分享選單

## 技術棧

| 類別 | 技術 |
|---|---|
| 語言 | Kotlin |
| UI | Jetpack Compose (Material 3) |
| 架構 | MVVM (ViewModel + StateFlow) |
| 資料庫 | Room (v4, 4 tables) |
| DI | Hilt |
| 相機 | CameraX |
| 定位 | Android LocationManager + Geocoder |
| 圖片 | ExifInterface, BitmapFactory |
| 最低 SDK | 26 (Android 8.0) |

## 專案結構

```
app/src/main/java/com/earthonline/app/
├── data/
│   ├── local/
│   │   ├── entity/           # Room Entities (4 表)
│   │   │   ├── AchievementDefinitionEntity.kt
│   │   │   ├── UserAchievementProgressEntity.kt
│   │   │   ├── CheckInRecord.kt
│   │   │   └── AchievementEvidence.kt
│   │   ├── dao/              # Data Access Objects (5 DAOs)
│   │   └── AppDatabase.kt
│   ├── ml/                   # ImageAnalyzer (ML Kit)
│   ├── photo/                # PhotoManager + Geocoder
│   └── repository/           # AchievementRepository
├── domain/
│   ├── model/                # TriggerType (LOCATION_CHECKIN_COUNT, MANUAL_CONFIRM, AUTO_TRACK)
│   └── service/              # AchievementService, LocaleManager
├── di/                       # Hilt DatabaseModule
├── ui/
│   ├── theme/                # 暗色遊戲風主題 (Gold, EmeraldGreen, AccentOrange)
│   ├── screens/dashboard/    # DashboardScreen + ViewModel + UiState
│   ├── components/           # AchievementUnlockDialog
│   └── share/                # ShareCardGenerator (1080x1080 PNG)
├── EarthOnlineApplication.kt
└── MainActivity.kt
```

## 隱私

- 所有資料僅存於裝置本地端 Room 資料庫，絕不上傳
- 無強制帳號系統，單機使用
- 照片儲存於 App 私密目錄
- 僅請求必要權限：CAMERA、ACCESS_FINE_LOCATION、ACCESS_COARSE_LOCATION

## 待辦事項

見 [TODO.md](TODO.md)

## License

Proprietary — All rights reserved.
