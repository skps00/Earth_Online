# 🌍 地球 Online (Earth Online)

> 將現實生活遊戲化的 Android App — 你的人生 RPG 正在進行中

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024-4285F4?logo=android)](https://developer.android.com/compose)
[![Min SDK](https://img.shields.io/badge/minSdk-26-34A853?logo=android)](https://developer.android.com)
[![Test](https://img.shields.io/badge/tests-21%20passed-brightgreen)](#)
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
| 🎭 日常 | 11 | 手動確認 |
| 🏆 史詩 | 15 | 手動確認 |
| 🩺 健康 | 6 | 手動確認 |
| 🚗 交通 | 5 | 部分自動（活動識別） |
| 🌊 大海 | 5 | 隱藏成就 |

- 9 個國家/洲成就自動解鎖（打卡日本 → `explore_japan`）
- 多步進度 + 隱藏成就 + 4 級稀有度邊框（含 glow 特效）
- 9 頁籤成就牆 (LazyRow + HorizontalPager)
- 螢火蟲粒子解鎖動畫 + 音效
- 成就分享卡 PNG (1080×1080)

### 🚶 活動識別 (Activity Recognition)
- 自動偵測走路 / 騎行 / 駕駛（Google Play Services Activity Recognition API）
- Dashboard 顯示活動統計（步行分鐘 / 騎行分鐘 / 騎行距離）
- 自動解鎖 `transport_bike`、`transport_bike_100` 成就

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

---

## 技術棧

| 類別 | 技術 |
|---|---|
| 語言 | Kotlin |
| UI | Jetpack Compose + Material 3 |
| 架構 | MVVM (ViewModel + StateFlow) |
| 導航 | Navigation Compose |
| 資料庫 | Room (v12, 7 entities, 5 DAOs) |
| DI | Hilt |
| 建置 | AGP 8.9.1, Gradle 8.11.1, compileSdk 36 |
| 相機 | CameraX |
| 定位 | LocationManager + Geocoder + Nominatim |
| 活動識別 | Google Play Services Activity Recognition |
| 測試 | JUnit 4 + MockK (21 tests) |
| 分析 | CodeGraph MCP (72 files, 1,192 nodes) |
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
│   └── repository/      # AchievementRepository
├── di/                  # Hilt Module
├── domain/
│   ├── model/           # TriggerType, Rarity, AchievementTriggers
│   └── service/         # CheckInCoordinator, SettingsManager
└── ui/
    ├── components/      # 14 共享元件
    ├── navigation/      # Screen + AppNavigation + BottomBar
    ├── screens/
    │   ├── camera/      # CameraScreen
    │   ├── dashboard/   # DashboardScreen + PetCard + ViewModel
    │   ├── history/     # CheckInHistoryScreen
    │   ├── settings/    # SettingsScreen
    │   └── onboarding/  # OnboardingScreen (3 頁)
    ├── share/           # ShareCardGenerator (1080×1080 PNG)
    └── theme/           # Color.kt + Theme.kt (雙主題)

app/src/test/java/com/earthonline/app/
├── domain/model/        # RarityTest, TriggerTypeTest
└── data/repository/     # AchievementRepositoryTest
```

---

## 權限

| 權限 | 用途 |
|---|---|
| `ACCESS_FINE_LOCATION` | GPS 打卡定位 |
| `ACCESS_COARSE_LOCATION` | 網路定位 fallback |
| `CAMERA` | 拍照存證 |
| `ACTIVITY_RECOGNITION` | 自動偵測走路/騎行/駕駛 |

---

## 隱私

- 所有資料僅存於裝置本地 Room 資料庫
- 無帳號系統、無雲端上傳、無自動備份
- 照片儲存於 App 私密目錄
- 隱私權政策：https://skps00.github.io/Earth_Online/privacy.html

---

## 待辦

見 [TODO.md](TODO.md) — 進度 47/55 (85%)

---

## License

Proprietary — All rights reserved.
