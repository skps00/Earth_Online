package com.earthonline.app.ui.screens.dashboard

import android.graphics.BitmapFactory
import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.earthonline.app.R
import com.earthonline.app.data.local.entity.AchievementDefinitionEntity
import com.earthonline.app.data.local.entity.UserAchievementProgressEntity
import com.earthonline.app.data.local.entity.CheckInRecord
import com.earthonline.app.data.repository.UnlockedAchievementEvent
import com.earthonline.app.domain.model.Rarity
import com.earthonline.app.domain.model.TriggerType
import com.earthonline.app.ui.components.AchievementCard
import com.earthonline.app.ui.components.AchievementDetailDialog
import com.earthonline.app.ui.components.AchievementUnlockDialog
import com.earthonline.app.ui.components.CheckInConfirmDialog
import com.earthonline.app.ui.components.EvidenceConfirmDialog
import com.earthonline.app.ui.screens.history.CheckInHistoryScreen
import com.earthonline.app.ui.share.ShareCardGenerator
import com.earthonline.app.ui.theme.AchievementLocked
import com.earthonline.app.ui.theme.AchievementUnlocked
import com.earthonline.app.ui.theme.AccentOrange
import com.earthonline.app.ui.theme.CardDark
import com.earthonline.app.ui.theme.EmeraldGreen
import com.earthonline.app.ui.theme.Gold
import com.earthonline.app.ui.theme.TextPrimaryDark
import com.earthonline.app.ui.theme.TextSecondaryDark
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onCheckIn: () -> Unit,
    onTakeEvidencePhoto: (String) -> Unit,
    onExportBackup: () -> Unit,
    onImportBackup: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var unlockEvent by remember { mutableStateOf<UnlockedAchievementEvent?>(null) }
    var unlockEventKey by remember { mutableStateOf(0L) }
    var showHistory by remember { mutableStateOf(false) }
    var historyRecords by remember { mutableStateOf<List<CheckInRecord>>(emptyList()) }

    if (showHistory) {
        LaunchedEffect(Unit) { historyRecords = viewModel.getAllCheckinRecords() }
        CheckInHistoryScreen(records = historyRecords, onBack = { showHistory = false })
        return
    }

    LaunchedEffect(Unit) {
        viewModel.unlockEvent.collect { event ->
            unlockEvent = null
            unlockEventKey++
            unlockEvent = event
        }
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.loading),
                style = MaterialTheme.typography.headlineMedium,
                color = TextSecondaryDark
            )
        }
        return
    }

    var selectedAchievement by remember { mutableStateOf<AchievementDisplayItem?>(null) }
    var selectedEvidencePath by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedAchievement) {
        val id = selectedAchievement?.definition?.achievementId
        selectedEvidencePath = if (id != null) viewModel.getEvidencePhoto(id) else null
    }

    val sections = AchievementCategories.getAll(uiState.achievements)

    val pagerState = rememberPagerState(pageCount = { sections.size })
    val selectedTabIndex by remember { derivedStateOf { pagerState.currentPage } }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A1A2E),
                            Color(0xFF16213E),
                            Color(0xFF0F3460)
                        )
                    )
                ),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { DashboardHeader() }

            item { CheckinCounterCard(uiState.totalCheckins) }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onCheckIn,
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Filled.LocationOn, null, tint = Color.White, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.checkin_label), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { showHistory = true },
                        modifier = Modifier.weight(0.4f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CardDark),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("記錄", color = Gold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.achievement_wall),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Gold,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(sections.size) { index ->
                        val isSelected = selectedTabIndex == index
                        val categoryItems = sections[index].second
                        val unlockedCount = categoryItems.count { it.progress.isUnlocked }
                        val totalCount = categoryItems.size

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                coroutineScope.launch { pagerState.animateScrollToPage(index) }
                            }
                        ) {
                            Text(
                                text = sections[index].first,
                                color = if (isSelected) Gold else Color.White.copy(alpha = 0.55f),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "$unlockedCount/$totalCount",
                                color = if (isSelected) Gold.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.35f),
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .width(24.dp)
                                        .height(3.dp)
                                        .background(Gold, RoundedCornerShape(2.dp))
                                )
                            } else {
                                Spacer(modifier = Modifier.height(3.dp))
                            }
                        }
                    }
                }
            }

            item {
                val screenHeightDp = LocalConfiguration.current.screenHeightDp
                val pagerHeight = (screenHeightDp - 320).coerceAtLeast(300)
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(pagerHeight.dp)
                ) { page ->
                    val pageItems = sections[page].second
                    if (pageItems.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.not_unlocked_yet),
                                color = TextSecondaryDark
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            pageItems.forEach { item ->
                                AchievementCard(
                                    item = item,
                                    onClick = { selectedAchievement = item }
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onExportBackup,
                        modifier = Modifier.weight(1f).height(38.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(stringResource(R.string.backup_export), color = EmeraldGreen, fontSize = 12.sp)
                    }
                    Button(
                        onClick = onImportBackup,
                        modifier = Modifier.weight(1f).height(38.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(stringResource(R.string.backup_import), color = AccentOrange, fontSize = 12.sp)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        unlockEvent?.let { event ->
            key(unlockEventKey) {
                AchievementUnlockDialog(
                    event = event,
                    onDismiss = {
                        unlockEvent = null
                        viewModel.onUnlockEventHandled()
                    }
                )
            }
        }

        selectedAchievement?.let { item ->
            AchievementDetailDialog(
                item = item,
                evidencePhotoPath = selectedEvidencePath,
                onDismiss = { selectedAchievement = null; selectedEvidencePath = null },
                onTakeEvidencePhoto = {
                    selectedAchievement = null
                    onTakeEvidencePhoto(item.definition.achievementId)
                },
                onManualConfirm = {
                    viewModel.onEvent(DashboardEvent.ManualConfirm(item.definition.achievementId))
                    selectedAchievement = null
                }
            )
        }

        if (uiState.showCheckinConfirmDialog) {
            CheckInConfirmDialog(
                address = uiState.pendingAddress,
                onConfirm = { viewModel.onEvent(DashboardEvent.CheckInConfirmed) },
                onDismiss = { viewModel.onEvent(DashboardEvent.CheckInRejected) }
            )
        }

        if (uiState.pendingEvidenceAchievementId != null && uiState.pendingEvidencePhotoPath != null) {
            EvidenceConfirmDialog(
                analyzedLabels = uiState.analyzedLabels,
                onConfirm = { viewModel.onEvent(DashboardEvent.EvidenceConfirmed) },
                onDismiss = { viewModel.onEvent(DashboardEvent.EvidenceRejected) }
            )
        }
    }
}


