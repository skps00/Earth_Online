package com.earthonline.app.domain.service

// 打卡協調器 — 協調位置取得、地理編碼與 ViewModel 的打卡流程

import com.earthonline.app.data.location.LocationHelper
import com.earthonline.app.ui.screens.dashboard.DashboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckInCoordinator @Inject constructor(
    private val locationHelper: LocationHelper
) {
    suspend fun performCheckIn(viewModel: DashboardViewModel): Boolean = withContext(Dispatchers.IO) {
        val location = locationHelper.getLastLocation() ?: return@withContext false
        val (address, country, continent) = locationHelper.reverseGeocode(location.latitude, location.longitude)
        viewModel.setPendingLocation(location.latitude, location.longitude, address, country, continent, location.altitude)
        true
    }
}
