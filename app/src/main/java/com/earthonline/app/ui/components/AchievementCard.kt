package com.earthonline.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.earthonline.app.R
import com.earthonline.app.domain.model.Rarity
import com.earthonline.app.ui.screens.dashboard.AchievementDisplayItem
import com.earthonline.app.ui.theme.EmeraldGreen
import com.earthonline.app.ui.theme.Gold

@Composable
fun AchievementCard(
    item: AchievementDisplayItem,
    onClick: () -> Unit
) {
    val isUnlocked = item.progress.isUnlocked
    val isHidden = item.definition.isHidden && !isUnlocked
    val progress = item.progress.currentProgress
    val goal = item.definition.triggerGoal
    val progressFraction = if (goal > 0) (progress.toFloat() / goal).coerceIn(0f, 1f) else 0f

    val rarity = Rarity.fromPoints(item.definition.rewardPoints)

    val cardColor by animateColorAsState(
        targetValue = when {
            isUnlocked -> Gold.copy(alpha = 0.15f)
            isHidden -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(600),
        label = "cardColor"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isUnlocked) rarity.color else Color.Transparent,
        animationSpec = tween(600),
        label = "borderColor"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (isUnlocked && (rarity == Rarity.EPIC || rarity == Rarity.LEGENDARY)) 0.3f else 0f,
        animationSpec = tween(800),
        label = "glowAlpha"
    )

    Box {
        if (isUnlocked && (rarity == Rarity.EPIC || rarity == Rarity.LEGENDARY) && glowAlpha > 0.01f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(y = 2.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                rarity.color.copy(alpha = glowAlpha),
                                rarity.color.copy(alpha = glowAlpha * 0.3f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

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
                            Brush.radialGradient(
                                colors = if (isUnlocked)
                                    listOf(Gold.copy(alpha = 0.5f), Gold.copy(alpha = 0.1f))
                                else
                                    listOf(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f), MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when { isUnlocked -> "\u2605"; isHidden -> "\uD83D\uDD12"; else -> "?" },
                        color = when { isUnlocked -> MaterialTheme.colorScheme.primary; isHidden -> MaterialTheme.colorScheme.onSurfaceVariant; else -> MaterialTheme.colorScheme.onSurfaceVariant },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isHidden) "???" else item.definition.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isUnlocked) MaterialTheme.colorScheme.primary else if (isHidden) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (isUnlocked) FontWeight.Bold else FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isHidden) stringResource(R.string.hidden_unlock_hint) else item.definition.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = progressFraction,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (isUnlocked) Gold else EmeraldGreen,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isUnlocked) stringResource(R.string.unlocked_badge) else "$progress / $goal",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!isHidden || isUnlocked) {
                        Text(
                            text = rarity.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = rarity.color.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}
