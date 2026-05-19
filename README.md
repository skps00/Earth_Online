# 地球 Online (Earth Online)

> 將現實生活遊戲化的 Android App — 你的人生 RPG 正在進行中

## 概述

「地球 Online」是一款 Android 原生應用程式，核心概念是透過分析用戶的現實世界行為數據，自動給予如同遊戲成就系統的回饋與鼓勵。

## MVP 功能（當前版本）

### 拍照記錄成就
- 使用 CameraX 拍照，照片儲存於裝置私密目錄
- 拍照後彈出食物確認對話框，確保僅記錄餐點
- 23 層拍照成就（1 次 → 10,000 次）
- Minecraft 風格成就解鎖彈窗動畫 + 音效

## 技術棧

| 類別 | 技術 |
|---|---|
| 語言 | Kotlin |
| UI | Jetpack Compose (Material 3) |
| 架構 | MVVM (ViewModel + StateFlow) |
| 資料庫 | Room |
| DI | Hilt |
| 相機 | CameraX |
| 最低 SDK | 26 (Android 8.0) |

## 專案結構

```
app/src/main/java/com/earthonline/app/
├── data/
│   ├── local/
│   │   ├── entity/       # Room Entity (雙表)
│   │   ├── dao/          # Data Access Object
│   │   └── AppDatabase.kt
│   ├── photo/            # PhotoManager (拍照管理)
│   └── repository/       # AchievementRepository (成就觸發邏輯)
├── domain/
│   ├── model/            # TriggerType 枚舉
│   └── service/          # AchievementService
├── di/                   # Hilt DatabaseModule
├── ui/
│   ├── theme/            # 暗色遊戲風主題
│   ├── screens/dashboard/ # 主畫面 + ViewModel
│   └── components/       # 成就彈窗動畫
└── MainActivity.kt
```

## 成就系統

| 成就 | 說明 | 門檻 |
|---|---|---|
| 初嚐記錄 | 拍攝 1 次餐點 | 1 |
| 三菜一湯 | 拍攝 3 次餐點 | 3 |
| 五感俱全 | 拍攝 5 次餐點 | 5 |
| ... | ... | ... |
| 地球美食征服者 | 拍攝 10,000 次餐點 | 10,000 |

共 23 層成就，完整清單見 `AchievementRepository.kt`

## 隱私

- 所有資料僅存於裝置本地端，絕不上傳
- 無強制帳號系統，單機使用
- 照片儲存於 App 私密目錄

## 待辦事項

見 [TODO.md](TODO.md)

## License

MIT
