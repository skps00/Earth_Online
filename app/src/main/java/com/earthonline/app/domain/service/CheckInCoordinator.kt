package com.earthonline.app.domain.service

import com.earthonline.app.data.location.LocationHelper
import com.earthonline.app.ui.screens.dashboard.DashboardViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckInCoordinator @Inject constructor(
    private val locationHelper: LocationHelper
) {
    fun performCheckIn(viewModel: DashboardViewModel): Boolean {
        val location = locationHelper.getLastLocation() ?: return false
        val (address, country, continent) = locationHelper.reverseGeocode(location.latitude, location.longitude)
        viewModel.setPendingLocation(location.latitude, location.longitude, address, country, continent)
        return true
    }
}
