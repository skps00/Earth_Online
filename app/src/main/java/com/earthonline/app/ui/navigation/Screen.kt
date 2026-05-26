package com.earthonline.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Dashboard : Screen("dashboard", "狀態", Icons.Filled.Home)
    data object Achievements : Screen("achievements", "成就", Icons.Filled.Star)
    data object History : Screen("history", "歷史", Icons.Filled.List)
    data object Settings : Screen("settings", "設定", Icons.Filled.Settings)
}
