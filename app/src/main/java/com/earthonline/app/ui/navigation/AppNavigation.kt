package com.earthonline.app.ui.navigation

import android.content.Context
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.earthonline.app.AppConstants
import com.earthonline.app.domain.service.SettingsManager
import com.earthonline.app.ui.screens.dashboard.DashboardScreen
import com.earthonline.app.ui.screens.dashboard.DashboardViewModel
import com.earthonline.app.ui.screens.history.CheckInHistoryScreen
import com.earthonline.app.ui.screens.onboarding.OnboardingScreen
import com.earthonline.app.ui.screens.settings.SettingsScreen
import com.earthonline.app.ui.theme.DeepBlue
import com.earthonline.app.ui.theme.Gold
import com.earthonline.app.ui.theme.TextSecondaryDark

@Composable
fun AppNavigation(
    viewModel: DashboardViewModel,
    settingsManager: SettingsManager,
    onCheckIn: () -> Unit,
    onTakeEvidencePhoto: (String) -> Unit,
    onExportBackup: () -> Unit,
    onImportBackup: () -> Unit
) {
    val context = LocalContext.current
    var showOnboarding by remember {
        val shown = context.getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(AppConstants.KEY_ONBOARDING_SHOWN, false)
        mutableStateOf(!shown)
    }

    if (showOnboarding) {
        OnboardingScreen(onDone = {
            context.getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
                .edit().putBoolean(AppConstants.KEY_ONBOARDING_SHOWN, true).apply()
            showOnboarding = false
        })
        return
    }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Screen.Dashboard.route

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = DeepBlue,
                tonalElevation = 0.dp
            ) {
                val screens = listOf(Screen.Dashboard, Screen.Achievements, Screen.History, Screen.Settings)
                screens.forEach { screen ->
                    val selected = currentRoute == screen.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                screen.icon,
                                contentDescription = screen.label,
                                tint = if (selected) Gold else TextSecondaryDark
                            )
                        },
                        label = {
                            Text(
                                screen.label,
                                fontSize = 10.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) Gold else TextSecondaryDark
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Gold.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onCheckIn = onCheckIn,
                    onTakeEvidencePhoto = onTakeEvidencePhoto,
                    onNavigateToHistory = { navController.navigate(Screen.History.route) }
                )
            }

            composable(Screen.Achievements.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onCheckIn = onCheckIn,
                    onTakeEvidencePhoto = onTakeEvidencePhoto,
                    showOnlyAchievementWall = true
                )
            }

            composable(Screen.History.route) {
                val records = remember { mutableListOf<com.earthonline.app.data.local.entity.CheckInRecord>() }
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    records.clear()
                    records.addAll(viewModel.getAllCheckinRecords())
                }
                CheckInHistoryScreen(
                    records = records,
                    onBack = { navController.navigateUp() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    settingsManager = settingsManager,
                    onBack = { navController.navigateUp() },
                    onExportBackup = onExportBackup,
                    onImportBackup = onImportBackup
                )
            }
        }
    }
}
