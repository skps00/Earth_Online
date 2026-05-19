package com.earthonline.app.ui.screens.dashboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.earthonline.app.R
import com.earthonline.app.data.repository.UnlockedAchievementEvent
import com.earthonline.app.ui.components.AchievementUnlockDialog
import com.earthonline.app.ui.theme.AchievementLocked
import com.earthonline.app.ui.theme.AchievementUnlocked
import com.earthonline.app.ui.theme.AccentOrange
import com.earthonline.app.ui.theme.CardDark
import com.earthonline.app.ui.theme.EmeraldGreen
import com.earthonline.app.ui.theme.Gold
import com.earthonline.app.ui.theme.TextPrimaryDark
import com.earthonline.app.ui.theme.TextSecondaryDark

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onTakePhoto: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var unlockEvent by remember { mutableStateOf<UnlockedAchievementEvent?>(null) }

    LaunchedEffect(Unit) {
        viewModel.unlockEvent.collect { event ->
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
                            Icons.Filled.PhotoCamera,
                            null,
                            tint = AccentOrange,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${uiState.totalPhotos}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = AccentOrange,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.photos_label),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryDark
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = onTakePhoto,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Filled.PhotoCamera, null, tint = Color.White, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.take_photo),
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

            items(uiState.achievements) { item ->
                AchievementCard(item = item)
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        unlockEvent?.let { event ->
            AchievementUnlockDialog(
                event = event,
                onDismiss = {
                    unlockEvent = null
                    viewModel.onUnlockEventHandled()
                }
            )
        }

        if (uiState.showFoodConfirmDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onEvent(DashboardEvent.FoodRejected) },
                title = {
                    Text(
                        stringResource(R.string.food_confirm_title),
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                },
                text = {
                    Text(
                        stringResource(R.string.food_confirm_message),
                        color = TextSecondaryDark
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.onEvent(DashboardEvent.FoodConfirmed) },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                    ) {
                        Text(stringResource(R.string.food_confirm_yes), color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { viewModel.onEvent(DashboardEvent.FoodRejected) },
                        colors = ButtonDefaults.buttonColors(containerColor = AchievementLocked)
                    ) {
                        Text(stringResource(R.string.food_confirm_no), color = TextSecondaryDark)
                    }
                },
                containerColor = Color(0xFF1E1E3A),
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
private fun AchievementCard(item: AchievementDisplayItem) {
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
            ),
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
