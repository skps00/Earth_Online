package com.earthonline.app.domain.service

// 打卡協調器 — 協調位置取得、地理編碼與 ViewModel 的打卡流程

import com.earthonline.app.data.location.LocationHelper
import com.earthonline.app.ui.screens.dashboard.DashboardViewModel
import javax.inject.Inject
import javax.inject.Singleton

// 打卡協調器 — 封裝位置取得與地理編碼的完整流程
@Singleton
class CheckInCoordinator @Inject constructor(
    private val locationHelper: LocationHelper
) {
    // 執行打卡流程 — 取最後位置 → 反向地理編碼 → 設定 ViewModel 待確認位置
    fun performCheckIn(viewModel: DashboardViewModel): Boolean {
        val location = locationHelper.getLastLocation() ?: return false
        val (address, country, continent) = locationHelper.reverseGeocode(location.latitude, location.longitude)
        viewModel.setPendingLocation(location.latitude, location.longitude, address, country, continent)
        return true
    }
}
