package com.earthonline.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.earthonline.app.data.repository.UnlockedAchievementEvent
import com.earthonline.app.R
import com.earthonline.app.ui.theme.Gold
import com.earthonline.app.ui.theme.GoldDark
import kotlinx.coroutines.delay

@Composable
fun AchievementUnlockDialog(
    event: UnlockedAchievementEvent,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scaleAnim by animateFloatAsState(
        targetValue = if (visible) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scaleAnim"
    )

    LaunchedEffect(Unit) {
        try {
            val sp = android.media.SoundPool.Builder()
                .setMaxStreams(1)
                .build()
            val soundId = sp.load(context, com.earthonline.app.R.raw.achievement_unlock, 1)
            sp.setOnLoadCompleteListener { _, _, status ->
                if (status == 0) sp.play(soundId, 1f, 1f, 1, 0, 1f)
            }
        } catch (_: Exception) { }
        visible = true
        delay(3500)
        visible = false
        delay(500)
        onDismiss()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(600)
            ) + fadeIn(tween(400)) + scaleIn(
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            ),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(400)
            ) + fadeOut(tween(300))
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(top = 60.dp)
                    .scale(scaleAnim),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A2E)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    GoldDark.copy(alpha = 0.3f),
                                    Color(0xFF1A1A2E),
                                    Color(0xFF1A1A2E)
                                )
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Title bar
                        Text(
                            text = stringResource(R.string.achievement_unlocked),
                            color = Gold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Achievement icon (gold circle with star)
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Gold,
                                            GoldDark,
                                            GoldDark.copy(alpha = 0.3f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            // Particle effect simulation with shimmer animation
                            val shimmer by animateFloatAsState(
                                targetValue = if (visible) 1f else 0f,
                                animationSpec = tween(800),
                                label = "shimmer"
                            )
                            Text(
                                text = "★",
                                fontSize = 36.sp,
                                color = Color(0xFF1A1A2E),
                                modifier = Modifier.alpha(shimmer)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Achievement title
                        Text(
                            text = event.achievement.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = Gold,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Achievement description
                        Text(
                            text = event.achievement.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFB0B0B0),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Reward points
                        Text(
                            text = stringResource(R.string.reward_points_format, event.achievement.rewardPoints),
                            style = MaterialTheme.typography.titleMedium,
                            color = Gold,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Decorative line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .height(2.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Gold,
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}
