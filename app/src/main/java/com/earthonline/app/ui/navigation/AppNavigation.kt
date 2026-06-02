package com.earthonline.app.ui.navigation

// 應用主導航：管理 Onboarding 引導、底部頁籤導航、各頁面路由與轉場動畫
import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import com.earthonline.app.ui.theme.Gold

// 應用導航入口：判斷是否顯示 Onboarding、建構 Scaffold + NavHost + BottomBar，管理頁面轉場
@Composable
fun AppNavigation(
    viewModel: DashboardViewModel,
    settingsManager: SettingsManager,
    onCheckIn: () -> Unit,
    onTakeEvidencePhoto: (String) -> Unit,
    onExportBackup: () -> Unit,
    onImportBackup: () -> Unit,
    onToggleDarkMode: (Boolean) -> Unit
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
            AnimatedBottomBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            enterTransition = { slideInHorizontally(tween(250)) { it / 4 } + fadeIn(tween(200)) },
            exitTransition = { slideOutHorizontally(tween(250)) { -it / 4 } + fadeOut(tween(200)) },
            popEnterTransition = { slideInHorizontally(tween(250)) { -it / 4 } + fadeIn(tween(200)) },
            popExitTransition = { slideOutHorizontally(tween(250)) { it / 4 } + fadeOut(tween(200)) }
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onCheckIn = onCheckIn,
                    onTakeEvidencePhoto = onTakeEvidencePhoto
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
                var records by remember { mutableStateOf(emptyList<com.earthonline.app.data.local.entity.CheckInRecord>()) }
                LaunchedEffect(Unit) {
                    records = viewModel.getAllCheckinRecords()
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
                    onImportBackup = onImportBackup,
                    onToggleDarkMode = onToggleDarkMode
                )
            }
        }
    }
}

// 動畫底部導航列：金色滑動指示器 + 選中 icon 縮放彈跳 + Material ripple 點擊效果
@Composable
private fun AnimatedBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val screens = listOf(Screen.Dashboard, Screen.Achievements, Screen.History, Screen.Settings)
    val selectedIndex = screens.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)

    Surface(
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(top = 2.dp)
        ) {
            AnimatedContent(targetState = selectedIndex) { index ->
                Row(modifier = Modifier.fillMaxWidth().height(3.dp)) {
                    repeat(index) {
                        Spacer(Modifier.weight(1f))
                    }
                    Box(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.45f)
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                        )
                    }
                    repeat(screens.size - index - 1) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                screens.forEachIndexed { index, screen ->
                    val isSelected = index == selectedIndex
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.18f else 1f,
                        animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f)
                    )

                    val labelResId = screen.labelResId

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = true, radius = 24.dp)
                            ) { onNavigate(screen.route) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            screen.icon,
                            contentDescription = stringResource(labelResId),
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp).scale(scale)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            stringResource(labelResId),
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
