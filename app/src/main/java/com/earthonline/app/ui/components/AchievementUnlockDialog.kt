package com.earthonline.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.earthonline.app.R
import com.earthonline.app.data.media.SoundPlayer
import com.earthonline.app.data.repository.UnlockedAchievementEvent
import com.earthonline.app.ui.theme.Gold
import com.earthonline.app.ui.theme.GoldDark
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun AchievementUnlockDialog(
    event: UnlockedAchievementEvent,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val context = LocalContext.current
    val density = LocalDensity.current

    LaunchedEffect(Unit) {
        SoundPlayer.play(context, "achievement_unlock")
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
            enter = slideInVertically(initialOffsetY = { -it }, animationSpec = tween(500)) + fadeIn(tween(400)),
            exit = slideOutVertically(targetOffsetY = { -it }, animationSpec = tween(400)) + fadeOut(tween(300))
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(top = 40.dp)
                    .offset { IntOffset(0, dragOffset.roundToInt()) }
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragEnd = {
                                if (dragOffset < -with(density) { 60.dp.toPx() }) {
                                    visible = false
                                    onDismiss()
                                }
                                dragOffset = 0f
                            },
                            onDragCancel = { dragOffset = 0f }
                        ) { _, dragAmount ->
                            dragOffset = (dragOffset + dragAmount).coerceAtMost(0f)
                        }
                    },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(GoldDark.copy(alpha = 0.3f), Color(0xFF1A1A2E))
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("成就解鎖！", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                        Spacer(modifier = Modifier.height(6.dp))

                        // Achievement icon
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape)
                                .background(Brush.radialGradient(listOf(Gold, GoldDark, GoldDark.copy(alpha = 0.3f)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("★", fontSize = 22.sp, color = Color(0xFF1A1A2E))
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(event.achievement.title, style = MaterialTheme.typography.titleMedium, color = Gold, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Text(event.achievement.description, style = MaterialTheme.typography.bodySmall, color = Color(0xFFB0B0B0), textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(stringResource(R.string.reward_points_format, event.achievement.rewardPoints), fontSize = 16.sp, color = Gold, fontWeight = FontWeight.SemiBold)

                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier.width(32.dp).height(3.dp)
                                .background(Gold.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                        )
                    }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(event.achievement.title, style = MaterialTheme.typography.titleLarge, color = Gold, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Text(event.achievement.description, style = MaterialTheme.typography.bodySmall, color = Color(0xFFB0B0B0), textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(stringResource(R.string.reward_points_format, event.achievement.rewardPoints), style = MaterialTheme.typography.titleMedium, color = Gold, fontWeight = FontWeight.SemiBold)

                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier.width(40.dp).height(4.dp)
                                .background(Gold.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                        )
                    }
                }
            }
        }
    }
}
