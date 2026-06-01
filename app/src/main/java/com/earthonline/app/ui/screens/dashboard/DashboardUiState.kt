package com.earthonline.app.ui.screens.dashboard

// 儀表板 UI 狀態定義，包含成就顯示項目、寵物狀態與事件密封類別

import com.earthonline.app.data.local.entity.AchievementDefinitionEntity
import com.earthonline.app.data.local.entity.UserAchievementProgressEntity

// 成就顯示項目，組合定義與進度資料供 UI 使用
data class AchievementDisplayItem(
    val definition: AchievementDefinitionEntity,  // 成就定義（名稱、類型、目標）
    val progress: UserAchievementProgressEntity   // 使用者進度（目前值、是否解鎖）
)

// 寵物 UI 狀態：名稱、表情、等級與五項屬性值
data class PetUiState(
    val name: String = "地球精靈",   // 寵物名稱
    val emoji: String = "🐉",       // 寵物表情符號
    val level: Int = 1,             // 寵物等級
    val strength: Int = 0,          // 力量屬性
    val agility: Int = 0,           // 敏捷屬性
    val intelligence: Int = 0,      // 智力屬性
    val charisma: Int = 0,          // 魅力屬性
    val vitality: Int = 0           // 活力屬性
)

// 儀表板完整 UI 狀態，包含簽到統計、等級進度、成就列表與各對話框顯示旗標
data class DashboardUiState(
    val totalCheckins: Long = 0L,                          // 總簽到次數
    val totalPoints: Long = 0L,                            // 總積分
    val unlockedCount: Int = 0,                            // 已解鎖成就數
    val totalAchievements: Int = 129,                      // 成就總數
    val playerLevel: Int = 1,                              // 玩家等級
    val levelProgress: Float = 0f,                         // 等級進度條 (0~1)
    val xpToNext: Long = 100L,                             // 升至下一級所需經驗值
    val achievements: List<AchievementDisplayItem> = emptyList(),  // 成就顯示列表
    val pet: PetUiState = PetUiState(),                    // 寵物狀態
    val isLoading: Boolean = true,                         // 是否正在載入
    val showCheckinConfirmDialog: Boolean = false,         // 是否顯示簽到確認對話框
    val pendingLocation: Pair<Double, Double>? = null,     // 待確認的簽到座標
    val pendingAddress: String = "",                       // 待確認的簽到地址
    val pendingCountry: String = "",                       // 待確認的簽到國家
    val pendingContinent: String = "",                     // 待確認的簽到洲別
    val pendingAltitude: Double? = null,                   // 待確認的簽到海拔
    val pendingEvidenceAchievementId: String? = null,      // 待確認證據的成就 ID
    val pendingEvidencePhotoPath: String? = null,          // 待確認證據的照片路徑
    val analyzedLabels: List<String> = emptyList(),        // 照片分析標籤
    val errorMessage: String? = null,                      // 錯誤訊息
    val walkingMinutes: Int = 0,                           // 步行分鐘數
    val bikingMinutes: Int = 0,                            // 騎行分鐘數
    val bikingKm: Int = 0,                                 // 騎行公里數
    val showActivityPermissionDialog: Boolean = false,     // 是否顯示活動權限對話框
    val activityPermissionGranted: Boolean = false,        // 活動辨識權限是否已授予
    val screenTimeMinutes: Int = 0                         // 今日螢幕使用時間（分鐘）
)

// 儀表板事件密封類別，定義所有使用者操作動作
sealed class DashboardEvent {
    data object CheckInConfirmed : DashboardEvent()        // 確認簽到
    data object CheckInRejected : DashboardEvent()         // 拒絕簽到
    data class ManualConfirm(val achievementId: String) : DashboardEvent()  // 手動確認成就
    data class EvidencePhotoTaken(val achievementId: String, val success: Boolean) : DashboardEvent()  // 已拍攝證據照片
    data object EvidenceConfirmed : DashboardEvent()       // 確認證據照片
    data object EvidenceRejected : DashboardEvent()        // 拒絕證據照片
    data class RenamePet(val newName: String) : DashboardEvent()  // 重新命名寵物
    data class ChangePetEmoji(val emoji: String) : DashboardEvent()  // 更換寵物表情
}
