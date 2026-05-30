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
import com.earthonline.app.ui.components.DashboardShimmer
import com.earthonline.app.ui.components.EvidenceConfirmDialog
import com.earthonline.app.ui.share.ShareCardGenerator
import com.earthonline.app.ui.theme.AchievementUnlocked
import com.earthonline.app.ui.theme.AccentOrange
import com.earthonline.app.ui.theme.EmeraldGreen
import com.earthonline.app.ui.theme.Gold
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
    showOnlyAchievementWall: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var unlockEvent by remember { mutableStateOf<UnlockedAchievementEvent?>(null) }
    var unlockEventKey by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        viewModel.unlockEvent.collect { event ->
            unlockEvent = null
            unlockEventKey++
            unlockEvent = event
        }
    }

    if (uiState.isLoading) {
        DashboardShimmer(modifier = Modifier.fillMaxSize())
        return
    }

    var selectedAchievement by remember { mutableStateOf<AchievementDisplayItem?>(null) }
    var selectedEvidencePath by remember { mutableStateOf<String?>(null) }
    var selectedAllEvidencePaths by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(selectedAchievement) {
        val id = selectedAchievement?.definition?.achievementId
        if (id != null) {
            selectedEvidencePath = viewModel.getEvidencePhoto(id)
            selectedAllEvidencePaths = viewModel.getAllEvidencePhotos(id)
        } else {
            selectedEvidencePath = null
            selectedAllEvidencePaths = emptyList()
        }
    }

    val sections = AchievementCategories.getAll(uiState.achievements)

    val pagerState = rememberPagerState(pageCount = { sections.size })
    val selectedTabIndex by remember { derivedStateOf { pagerState.currentPage } }
    val coroutineScope = rememberCoroutineScope()

    val nextMilestone = remember(uiState.achievements) {
        uiState.achievements
            .filter { !it.progress.isUnlocked && it.definition.triggerGoal > 0 }
            .maxByOrNull { it.progress.currentProgress.toFloat() / it.definition.triggerGoal }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                ),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!showOnlyAchievementWall) {
            item {
                DashboardHeader()
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(R.string.level_format, uiState.playerLevel), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                LinearProgressIndicator(
                                    progress = uiState.levelProgress,
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                    color = Gold,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(stringResource(R.string.xp_to_next, uiState.xpToNext, uiState.playerLevel + 1), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🏆", fontSize = 18.sp)
                                Text("${uiState.totalPoints}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(stringResource(R.string.total_points_label), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("✅", fontSize = 18.sp)
                                Text("${uiState.unlockedCount}/${uiState.totalAchievements}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(stringResource(R.string.achievements_label), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📍", fontSize = 18.sp)
                                Text("${uiState.totalCheckins}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(stringResource(R.string.checkin_short_label), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("\uD83D\uDEB6", fontSize = 18.sp)
                                Text("${uiState.walkingMinutes}min", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Walking", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("\uD83D\uDEB4", fontSize = 18.sp)
                                Text("${uiState.bikingMinutes}min", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Biking", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("\uD83D\uDEDE", fontSize = 18.sp)
                                Text("${uiState.bikingKm}km", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Distance", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            item {
                PetCard(
                    pet = uiState.pet,
                    onRename = { newName -> viewModel.onEvent(DashboardEvent.RenamePet(newName)) },
                    onChangeEmoji = { emoji -> viewModel.onEvent(DashboardEvent.ChangePetEmoji(emoji)) }
                )
            }

            if (uiState.totalCheckins == 0L) {
                item {
                    Text(
                        stringResource(R.string.empty_checkin_hint),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                Button(
                    onClick = onCheckIn,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Filled.LocationOn, null, tint = Color.White, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.checkin_label), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (nextMilestone != null && uiState.totalCheckins > 0L) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(stringResource(R.string.milestone_hint), color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                nextMilestone.definition.title,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "${nextMilestone.progress.currentProgress} / ${nextMilestone.definition.triggerGoal}",
                                color = AccentOrange,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
            }

            if (showOnlyAchievementWall) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (showOnlyAchievementWall) {
            item {
                Text(
                    text = stringResource(R.string.achievement_wall),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
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
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "$unlockedCount/$totalCount",
                                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                Spacer(modifier = Modifier.height(16.dp))
            }
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
                allEvidencePaths = selectedAllEvidencePaths,
                onDismiss = { selectedAchievement = null; selectedEvidencePath = null; selectedAllEvidencePaths = emptyList() },
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
                photoUri = uiState.pendingEvidencePhotoPath,
                analyzedLabels = uiState.analyzedLabels,
                onConfirm = { viewModel.onEvent(DashboardEvent.EvidenceConfirmed) },
                onDismiss = { viewModel.onEvent(DashboardEvent.EvidenceRejected) }
            )
        }
                        }
                    }


