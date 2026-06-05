# 🌍 地球 Online (Earth Online)

> 將現實生活遊戲化的 Android App — 你的人生 RPG 正在進行中

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024-4285F4?logo=android)](https://developer.android.com/compose)
[![Min SDK](https://img.shields.io/badge/minSdk-26-34A853?logo=android)](https://developer.android.com)
[![Test](https://img.shields.io/badge/tests-18%20passed-brightgreen)](#)
[![License](https://img.shields.io/badge/License-Proprietary-red)](LICENSE)

---

## 概述

「地球 Online」將現實生活轉化為 RPG 遊戲體驗。在任何地點打卡，自動解鎖成就，累積點數培養寵物，探索你的人生地圖。

---

## 核心功能

### 📍 打卡系統
- GPS 定位 + Geocoder 反編碼 + Nominatim fallback
- 自動辨識國家/大洲（支援中英文名稱），簡化地址顯示
- 打卡歷史按國家分組瀏覽

### 🏆 129 個成就（8 大分類）

| 分類 | 數量 | 觸發 |
|---|---|---|
| 📍 打卡 | 6 | 不重複地點數 |
| 🗺️ 探索 | 31 | AUTO_TRACK + 手動 |
| 🎓 職涯 | 10 | 手動確認 |
| 🎭 日常 | 11 | 手動確認（早起/通宵/數位排毒自動化） |
| 🏆 史詩 | 15 | AUTO_TRACK + 手動（地震/天氣自動化） |
| 🩺 健康 | 6 | 手動確認 |
| 🚗 交通 | 5 | 部分自動（活動識別） |
| 🌊 大海 | 5 | 隱藏成就 |

- 12 個成就完全自動化解鎖（國家/洲別/高山海拔/螢幕時間/交通）
- 多步進度 + 隱藏成就 + 4 級稀有度邊框（含 glow 特效）
- 螢火蟲粒子解鎖動畫 + 音效
- 成就分享卡 PNG (1080×1080)

### 🌤 天氣與地震偵測 (Weather & Earthquake)
- `WorkManager` 每 15 分鐘後台週期檢查
- OpenWeatherMap API：自動偵測降雨、暴風、閃電、極端高溫（>35°C）
- USGS Earthquake API：自動偵測 200km 內 M4+ 地震
- 5 個天氣/地震成就全部 AUTO_TRACK
- OWM API key 透過 `local.properties` + `BuildConfig` 安全注入
- Dashboard 載入時自動評估

### ⏱ 螢幕時間偵測 (Screen Time)
- `UsageStatsManager.queryEvents()` 讀取每日手機使用記錄
- 自動解鎖 `daily_earlybird`（早起，5AM 前解鎖）、`daily_allnighter`（通宵，2-5AM >30min）、`daily_no_phone`（數位排毒，24h 無使用）
- 每次 Dashboard 載入時自動評估

### 🚶 活動識別 (Activity Recognition)
- 自動偵測走路 / 騎行 / 駕駛（Google Play Services Activity Recognition API）
- Dashboard 顯示活動統計（步行分鐘 / 騎行分鐘 / 騎行距離）
- 自動解鎖 `transport_bike`、`transport_bike_100` 成就

### 📱 統一權限系統
- 單一對話框整合位置、身體活動、相機權限，避免多重彈窗
- 權限序列表依序請求授權，完成後顯示結果（✅/❌）
- 設定頁「權限提醒」開關可關閉所有權限對話框
- Screen Time 權限獨立為系統設定對話框

### 📸 自訂相機 (RPG 風格)
- CameraX 內建相機 + RPG 風格外框
- 金色準星 Reticle + SCANNING ENV 掃描指示
- 閃光燈 / 相簿選取 / 格線開關
- 1080p WebP 壓縮 (≤200KB) + EXIF 旋轉修正
- 多步進度保留全部證據照 + 滾動瀏覽

### 🐉 寵物系統
- 12 種 emoji 可選 (🐉🦊🐱🐶🦄🐲🐰🐼🦋🐙🐢🦅)
- 5 維屬性養成 (力量/敏捷/智力/魅力/體力)
- 點擊彈跳動畫 + 隨機對話氣泡
- 自訂命名 + 換寵 UI

### 🎨 雙主題
- 深色 RPG 主題 (深藍 + 金 + 翠綠)
- 淺色現代主題 (藍灰 + 白 + 深金)
- 下拉選單即時切換，全元件適配

### 🔄 備份系統
- JSON 匯出/匯入 (含寵物資料 + 全部證據)
- SAF 檔案選擇器
- 錯誤處理 + 使用者提示

---

## 技術棧

| 類別 | 技術 |
|---|---|
| 語言 | Kotlin |
| UI | Jetpack Compose + Material 3 |
| 架構 | MVVM (ViewModel + StateFlow) |
| 導航 | Navigation Compose |
| 資料庫 | Room (v12, 7 entities, 5 DAOs, exportSchema) |
| DI | Hilt |
| 建置 | AGP 8.9.1, Gradle 8.11.1, compileSdk 36 |
| 相機 | CameraX |
| 定位 | LocationManager + Geocoder + Nominatim |
| 活動識別 | Google Play Services Activity Recognition |
| 螢幕時間 | UsageStatsManager (queryEvents) |
| 天氣/地震 | OpenWeatherMap + USGS + WorkManager |
| 測試 | JUnit 4 + MockK (18 tests) |
| 分析 | CodeGraph MCP (77 files indexed) |
| 最低 SDK | 26 (Android 8.0) |

---

## 專案結構

```
app/src/main/java/com/earthonline/app/
├── data/
│   ├── activity/        # ActivityRecognitionManager
│   ├── backup/          # BackupManager (JSON 匯出/匯入)
│   ├── local/
│   │   ├── entity/      # 7 Room Entities
│   │   ├── dao/         # 5 DAOs
│   │   └── AppDatabase.kt
│   ├── location/        # LocationHelper + ContinentMapper
│   ├── media/           # SoundPlayer
│   ├── ml/              # ImageAnalyzer
│   ├── photo/           # PhotoManager (壓縮 + EXIF)
│   ├── screentime/      # ScreenTimeManager (UsageStats)
│   ├── weather/          # WeatherManager + EarthquakeManager + WeatherWorker
│   └── repository/       # AchievementRepository
├── di/                  # Hilt Module
├── domain/
│   ├── model/           # TriggerType, Rarity, AchievementTriggers
│   └── service/         # CheckInCoordinator, SettingsManager
└── ui/
    ├── components/      # 13 共享元件
    ├── navigation/      # Screen + AppNavigation + BoottomBar
    ├── screens/
    │   ├── camera/      # CameraScreen
    │   ├── dashboard/   # DashboardScreen + PetCard + ViewModel + UnifiedPermissionDialog
    │   ├── history/     # CheckInHistoryScreen
    │   ├── settings/    # SettingsScreen
    │   └── onboarding/  # OnboardingScreen (3 頁)
    ├── share/           # ShareCardGenerator (1080×1080 PNG)
    └── theme/           # Color.kt + Theme.kt (雙主題)

app/src/test/java/com/earthonline/app/
├── data/repository/     # AchievementRepositoryTest, ScreenTimeAchievementTest
└── domain/model/        # RarityTest, TriggerTypeTest
```

---

## 權限

| 權限 | 用途 |
|---|---|
| `ACCESS_FINE_LOCATION` | GPS 打卡定位 |
| `ACCESS_COARSE_LOCATION` | 網路定位 fallback |
| `CAMERA` | 拍照存證 |
| `ACTIVITY_RECOGNITION` | 自動偵測走路/騎行/駕駛 |
| `PACKAGE_USAGE_STATS` | 讀取每日螢幕使用時間（早起/通宵/數位排毒） |

---

## 隱私

- 所有資料僅存於裝置本地 Room 資料庫
- 無帳號系統、無雲端上傳、無自動備份
- 照片儲存於 App 私密目錄
- 權限對話框明確說明每項資料用途
- 隱私權政策：https://skps00.github.io/Earth_Online/privacy.html

---

## 待辦

見 [TODO.md](TODO.md) — 進度持續更新

---

## License

Proprietary — All rights reserved.
