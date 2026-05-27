# 🌍 地球 Online (Earth Online)

> 將現實生活遊戲化的 Android App — 你的人生 RPG 正在進行中

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024-4285F4?logo=android)](https://developer.android.com/compose)
[![Min SDK](https://img.shields.io/badge/minSdk-26-34A853?logo=android)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-Proprietary-red)](LICENSE)

---

## 概述

「地球 Online」將現實生活轉化為 RPG 遊戲體驗。在任何地點打卡，自動解鎖成就，累積點數培養寵物，探索你的人生地圖。

---

## 核心功能

### 📍 打卡系統
- GPS 定位 + Geocoder 反編碼 + Nominatim fallback
- 自動辨識國家/大洲，簡化地址顯示
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
| 🚗 交通 | 5 | 手動確認 |
| 🌊 大海 | 5 | 隱藏成就 |

- 多步進度 + 隱藏成就 + 4 級稀有度邊框
- 9 頁籤成就牆 (LazyRow + HorizontalPager)
- 螢火蟲粒子解鎖動畫 + 音效
- 成就分享卡 PNG (1080×1080)

### 🐉 寵物系統
- 12 種 emoji 可選 (🐉🦊🐱🐶🦄🐲🐰🐼🦋🐙🐢🦅)
- 5 維屬性養成 (力量/敏捷/智力/魅力/體力)
- 點擊彈跳動畫 + 隨機對話氣泡
- 自訂命名 + 換寵 UI

### 📸 證據系統
- CameraX 拍照存證 + ContentResolver 預覽
- 1080p WebP 壓縮 (≤200KB) + EXIF 旋轉修正
- 多步進度保留全部證據照 + 滾動瀏覽

### 🎨 雙主題
- 深色 RPG 主題 (深藍 + 金 + 翠綠)
- 淺色現代主題 (藍灰 + 白 + 深金)
- 下拉選單即時切換，全 14+ 元件適配

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
| 相機 | CameraX |
| 定位 | LocationManager + Geocoder + Nominatim |
| 分析 | CodeGraph MCP (72 files, 1,192 nodes) |
| 最低 SDK | 26 (Android 8.0) |

---

## 專案結構

```
app/src/main/java/com/earthonline/app/
├── data/
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
│   ├── model/           # TriggerType, Rarity, PetStatContributions
│   └── service/         # CheckInCoordinator, SettingsManager
└── ui/
    ├── components/      # 11 共享元件 (卡片/對話框/動畫)
    ├── navigation/      # Screen + AppNavigation + BottomBar
    ├── screens/
    │   ├── dashboard/   # DashboardScreen + PetCard + ViewModel
    │   ├── history/     # CheckInHistoryScreen
    │   ├── settings/    # SettingsScreen
    │   └── onboarding/  # OnboardingScreen (3 頁)
    ├── share/           # ShareCardGenerator (1080×1080 PNG)
    └── theme/           # Color.kt + Theme.kt (雙主題)
```

---

## 權限

| 權限 | 用途 |
|---|---|
| `ACCESS_FINE_LOCATION` | GPS 打卡定位 |
| `ACCESS_COARSE_LOCATION` | 網路定位 fallback |
| `CAMERA` | 拍照存證 |

---

## 隱私

- 所有資料僅存於裝置本地 Room 資料庫
- 無帳號系統、無雲端上傳
- 照片儲存於 App 私密目錄

---

## 待辦

見 [TODO.md](TODO.md) — 進度 32/39 (82%)

---

## License

Proprietary — All rights reserved.
