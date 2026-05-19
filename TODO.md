# 地球 Online - 代辦事項

## 方案 B：ML Kit Image Labeling（裝置端食物辨識）
- [ ] 加入 `com.google.mlkit:image-labeling` 依賴
- [ ] 實作 `ImageLabeler`，拍照後將照片轉為 `InputImage`
- [ ] 設定信心度閾值（建議 0.7），低於閾值顯示「這似乎不是食物」提示
- [ ] 拿掉手動確認對話框（或保留兩層驗證）

## 方案 C：自訂 TensorFlow Lite 食物模型
- [ ] 收集台灣/亞洲常見餐點訓練資料（至少 2000 張）
- [ ] 標註分類（滷肉飯、臭豆腐、腸粉等）
- [ ] 訓練 TFLite 分類模型並量化壓縮
- [ ] 部署至 App 並取代 ML Kit 通用模型
- [ ] 加入菜系分類，為未來成就鋪路

## 其他待辦
- [ ] 步數成就重新評估後重新實作
- [ ] 語言切換功能修復後加回
- [ ] 音效檔 `achievement_unlock.mp3` 需放入 `res/raw/`
- [ ] Lottie 粒子特效 JSON 動畫檔
- [ ] 單元測試覆蓋新增的成就確認流程
