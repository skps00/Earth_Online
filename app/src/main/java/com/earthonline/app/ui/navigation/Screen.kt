package com.earthonline.app.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.earthonline.app.R

sealed class Screen(
    val route: String,
    @StringRes val labelResId: Int,
    val icon: ImageVector
) {
    data object Dashboard : Screen("dashboard", R.string.nav_dashboard, Icons.Filled.Home)
    data object Achievements : Screen("achievements", R.string.nav_achievements, Icons.Filled.Star)
    data object History : Screen("history", R.string.nav_history, Icons.AutoMirrored.Filled.List)
    data object Settings : Screen("settings", R.string.nav_settings, Icons.Filled.Settings)
}
