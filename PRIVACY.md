# 隱私權政策

**最後更新日期：2026 年 5 月 31 日**

## 概述

「地球 Online」是一款純本機運行的遊戲化生活記錄 App。我們**不會收集、上傳或分享您的任何個人數據**至我們的伺服器（我們根本沒有伺服器）。

## 我們不使用以下技術

- ❌ 不收集個人身份信息
- ❌ 不連接遠端伺服器
- ❌ 不使用第三方分析工具（如 Google Analytics）
- ❌ 不多載廣告 SDK
- ❌ 不追蹤您的使用行為

## 權限說明

本 App 請求以下權限，所有數據**預設僅存儲於您的設備本地**：

| 權限 | 用途 | 數據去向 |
|------|------|---------|
| 位置 (GPS) | 記錄打卡地點（國家/城市層級） | 存於設備 Room 資料庫；若裝置 Geocoder 不可用，會將經緯度發送至 Nominatim（OpenStreetMap）API 以獲取地址 |
| 相機 | 拍攝成就證據照片 | 僅存於設備內部儲存空間 |
| 活動識別 | 自動偵測步行/跑步/騎行/駕駛以解鎖交通成就 | 活動分鐘數存於設備 SharedPreferences，不上傳 |
| 網路 | 僅用於 Nominatim 地址反查詢（後備方案）| 發送 GPS 經緯度至 nominatim.openstreetmap.org |

## 活動識別數據

本 App 使用裝置的活動識別功能（透過 Google Play Services）自動偵測您的運動類型（步行、跑步、騎行、駕駛），以自動解鎖相關成就。

- 追蹤的數據：步行分鐘數、騎行分鐘數、騎行估算距離
- 儲存位置：僅存於設備 SharedPreferences
- 數據不會離開您的裝置
- 您可在「設定」中隨時清除所有活動數據

## ML Kit 影像標籤

本 App 使用 Google ML Kit 的裝置端影像標籤功能分析證據照片。所有分析**完全在裝置上執行**，照片不會被上傳至任何伺服器。ML Kit 會在首次使用時下載基礎模型檔（透過 Google Play Services 管理），此過程不涉及您的個人數據。

## 第三方 API 使用

### Nominatim (OpenStreetMap)

當您的裝置不支援 Android Geocoder 時，本 App 會使用 Nominatim API 進行地址反查詢。這會將您的**經緯度座標**發送至 `nominatim.openstreetmap.org`。

- 目的：將 GPS 座標轉換為可讀地址（國家/城市）
- 發送數據：經緯度
- 隱私政策：https://operations.osmfoundation.org/policies/nominatim/
- 此 API 為後備方案，僅在裝置 Geocoder 無法使用時觸發

## 數據安全

- 所有數據以本地 SQLite 資料庫（Room）儲存
- 照片儲存於 App 內部目錄（其他 App 無法存取）
- 本 App 不使用 Android 自動備份功能，數據不會上傳至 Google Drive
- 照片壓縮為 WebP 格式，原始 JPEG 檔案（含 EXIF GPS 標籤）會被刪除
- 您可以隨時從「設定」頁面清除所有數據（含資料庫、活動統計、照片）
- 您可以隨時匯出 JSON 備份檔案自行保管

## 無雲端分享

我們**不向任何第三方商業機構傳輸或分享**您的數據。本 App 無後端伺服器。唯一的例外是 Nominatim API（如上所述），這是將 GPS 座標轉換為地名的必要步驟。

## 兒童隱私

本 App 不針對 13 歲以下兒童設計，也不會有意收集兒童的個人信息。

## 政策變更

若未來隱私權政策有變更，將在 App 內更新並通知用戶。

## 聯絡我們

如有隱私相關問題，請聯繫：**skps00@proton.me**
