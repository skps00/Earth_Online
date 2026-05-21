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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.earthonline.app.data.repository.UnlockedAchievementEvent
import com.earthonline.app.domain.model.TriggerType
import com.earthonline.app.ui.components.AchievementUnlockDialog
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
    onTakeEvidencePhoto: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

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

    val tabTitles = listOf(
        "📍 打卡", "🗺️ 探索", "🎓 職涯", "🎭 日常", "🏆 史詩", "🩺 健康", "🚗 交通"
    )

    val checkinItems = uiState.achievements.filter {
        it.definition.triggerType == TriggerType.LOCATION_CHECKIN_COUNT.value
    }
    val exploreItems = uiState.achievements.filter {
        (it.definition.triggerType == TriggerType.MANUAL_CONFIRM.value || it.definition.triggerType == TriggerType.AUTO_TRACK.value) && it.definition.achievementId.startsWith("explore_")
    }
    val careerItems = uiState.achievements.filter {
        it.definition.triggerType == TriggerType.MANUAL_CONFIRM.value && it.definition.achievementId.startsWith("career_")
    }
    val dailyItems = uiState.achievements.filter {
        it.definition.triggerType == TriggerType.MANUAL_CONFIRM.value && it.definition.achievementId.startsWith("daily_")
    }
    val epicItems = uiState.achievements.filter {
        it.definition.triggerType == TriggerType.MANUAL_CONFIRM.value && it.definition.achievementId.startsWith("epic_")
    }
    val healthItems = uiState.achievements.filter {
        it.definition.triggerType == TriggerType.MANUAL_CONFIRM.value && it.definition.achievementId.startsWith("health_")
    }
    val transportItems = uiState.achievements.filter {
        it.definition.triggerType == TriggerType.MANUAL_CONFIRM.value && it.definition.achievementId.startsWith("transport_")
    }

    val pagerItems = listOf(checkinItems, exploreItems, careerItems, dailyItems, epicItems, healthItems, transportItems)

    val pagerState = rememberPagerState(pageCount = { pagerItems.size })
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
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.dashboard_title),
                        style = MaterialTheme.typography.headlineLarge,
                        color = Gold,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.life_rpg_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryDark,
                        textAlign = TextAlign.Center
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardDark),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.LocationOn,
                            null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${uiState.totalCheckins}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = EmeraldGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.checkin_label),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryDark
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = onCheckIn,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Filled.LocationOn, null, tint = Color.White, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.checkin_label),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
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
                    items(tabTitles.size) { index ->
                        val isSelected = selectedTabIndex == index
                        val categoryItems = pagerItems[index]
                        val unlockedCount = categoryItems.count { it.progress.isUnlocked }
                        val totalCount = categoryItems.size

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                coroutineScope.launch { pagerState.animateScrollToPage(index) }
                            }
                        ) {
                            Text(
                                text = tabTitles[index],
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
                    val pageItems = pagerItems[page]
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
            AlertDialog(
                onDismissRequest = { viewModel.onEvent(DashboardEvent.CheckInRejected) },
                title = {
                    Text(
                        stringResource(R.string.checkin_confirm_title),
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                },
                text = {
                    Column {
                        Text(
                            text = uiState.pendingAddress,
                            color = TextSecondaryDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${getString(R.string.checkin_action)}?",
                            color = TextSecondaryDark
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.onEvent(DashboardEvent.CheckInConfirmed) },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                    ) {
                        Text(stringResource(R.string.checkin_confirm_yes), color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { viewModel.onEvent(DashboardEvent.CheckInRejected) },
                        colors = ButtonDefaults.buttonColors(containerColor = AchievementLocked)
                    ) {
                        Text(stringResource(R.string.food_confirm_no), color = TextSecondaryDark)
                    }
                },
                containerColor = Color(0xFF1E1E3A),
                shape = RoundedCornerShape(16.dp)
            )
        }

        if (uiState.pendingEvidenceAchievementId != null && uiState.pendingEvidencePhotoPath != null) {
            AlertDialog(
                onDismissRequest = { viewModel.onEvent(DashboardEvent.EvidenceRejected) },
                title = {
                    Text(
                        stringResource(R.string.evidence_confirm_title),
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                },
                text = {
                    Column {
                        Text(
                            stringResource(R.string.evidence_confirm_message),
                            color = TextSecondaryDark
                        )
                        if (uiState.analyzedLabels.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            uiState.analyzedLabels.forEach { label ->
                                Text(
                                    text = "• $label",
                                    color = TextSecondaryDark,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.onEvent(DashboardEvent.EvidenceConfirmed) },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                    ) {
                        Text(stringResource(R.string.evidence_confirm_btn), color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { viewModel.onEvent(DashboardEvent.EvidenceRejected) },
                        colors = ButtonDefaults.buttonColors(containerColor = AchievementLocked)
                    ) {
                        Text(stringResource(R.string.evidence_retry_btn), color = TextSecondaryDark)
                    }
                },
                containerColor = Color(0xFF1E1E3A),
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
private fun AchievementCard(
    item: AchievementDisplayItem,
    onClick: () -> Unit
) {
    val isUnlocked = item.progress.isUnlocked
    val progress = item.progress.currentProgress
    val goal = item.definition.triggerGoal
    val progressFraction = if (goal > 0) (progress.toFloat() / goal).coerceIn(0f, 1f) else 0f

    val cardColor by animateColorAsState(
        targetValue = if (isUnlocked) AchievementUnlocked.copy(alpha = 0.15f) else AchievementLocked,
        animationSpec = tween(600),
        label = "cardColor"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isUnlocked) AchievementUnlocked else Color.Transparent,
        animationSpec = tween(600),
        label = "borderColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isUnlocked) Modifier.border(1.dp, borderColor, RoundedCornerShape(12.dp))
                else Modifier
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) Gold.copy(alpha = 0.3f) else AchievementLocked.copy(alpha = 0.5f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isUnlocked) "★" else "?",
                    color = if (isUnlocked) Gold else TextSecondaryDark,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.definition.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isUnlocked) Gold else TextPrimaryDark,
                    fontWeight = if (isUnlocked) FontWeight.Bold else FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.definition.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = progressFraction,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (isUnlocked) Gold else EmeraldGreen,
                    trackColor = AchievementLocked.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isUnlocked) stringResource(R.string.unlocked_badge) else "$progress / $goal",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isUnlocked) Gold else TextSecondaryDark
                )
            }
        }
    }
}

@Composable
private fun AchievementDetailDialog(
    item: AchievementDisplayItem,
    evidencePhotoPath: String?,
    onDismiss: () -> Unit,
    onTakeEvidencePhoto: () -> Unit,
    onManualConfirm: () -> Unit
) {
    val isUnlocked = item.progress.isUnlocked
    val progress = item.progress.currentProgress
    val goal = item.definition.triggerGoal
    val progressFraction = if (goal > 0) (progress.toFloat() / goal).coerceIn(0f, 1f) else 0f
    val isManual = item.definition.triggerType == TriggerType.MANUAL_CONFIRM.value || item.definition.triggerType == TriggerType.AUTO_TRACK.value
    val context = LocalContext.current

    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = item.definition.title,
                fontWeight = FontWeight.Bold,
                color = if (isUnlocked) Gold else TextPrimaryDark
            )
        },
        text = {
            Column {
                Text(
                    text = item.definition.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryDark
                )
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = progressFraction,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (isUnlocked) Gold else EmeraldGreen,
                    trackColor = AchievementLocked.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isUnlocked) stringResource(R.string.unlocked_badge) else "$progress / $goal",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isUnlocked) Gold else TextSecondaryDark
                )
                if (isUnlocked && item.progress.unlockedDate != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${stringResource(R.string.unlock_time_label)}: ${dateFormat.format(Date(item.progress.unlockedDate))}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Gold.copy(alpha = 0.7f)
                    )
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.not_unlocked_yet),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryDark
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.reward_points_format, item.definition.rewardPoints),
                    style = MaterialTheme.typography.titleMedium,
                    color = Gold,
                    fontWeight = FontWeight.SemiBold
                )

                if (isUnlocked) {
                    if (evidencePhotoPath != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        val bitmap = remember(evidencePhotoPath) {
                            try { BitmapFactory.decodeFile(evidencePhotoPath) } catch (_: Exception) { null }
                        }
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    val shareText = "我在「地球 Online」解鎖了成就：${item.definition.title}！\n${item.definition.description}\n+${item.definition.rewardPoints} 點數"
                    Button(
                        onClick = {
                            val uri = ShareCardGenerator.generate(context, item.definition.title, item.definition.description, item.definition.rewardPoints)
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "image/png"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                if (uri != null) putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(intent, null))
                        },
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Gold.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Filled.Share, null, tint = Gold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.share_achievement), color = Gold, fontSize = 13.sp)
                    }
                }
            }
        },
        dismissButton = {
            if (isManual && !isUnlocked) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onTakeEvidencePhoto,
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Gold),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.evidence_take_photo), color = Color(0xFF1A1A2E), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Button(
                        onClick = onManualConfirm,
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.manual_confirm_btn), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CardDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.food_confirm_no), color = TextSecondaryDark, fontSize = 14.sp)
            }
        },
        containerColor = Color(0xFF1E1E3A),
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun getString(resId: Int): String {
    return stringResource(resId)
}
