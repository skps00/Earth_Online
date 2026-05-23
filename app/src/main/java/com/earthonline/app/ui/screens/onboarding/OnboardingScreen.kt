package com.earthonline.app.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.earthonline.app.ui.theme.DeepBlue
import com.earthonline.app.ui.theme.EmeraldGreen
import com.earthonline.app.ui.theme.Gold
import com.earthonline.app.ui.theme.TextSecondaryDark
import kotlinx.coroutines.launch

data class OnboardingPage(
    val emoji: String,
    val title: String,
    val desc: String
)

@Composable
fun OnboardingScreen(onDone: () -> Unit) {
    val pages = listOf(
        OnboardingPage("🌍", "歡迎來到地球 Online", "你的人生 RPG 正在進行中\n將現實生活遊戲化，自動記錄你的每一個里程碑"),
        OnboardingPage("📍", "打卡記錄生活", "在任何地方打卡\nGPS 自動辨識國家和大洲\n解鎖探索成就"),
        OnboardingPage("🏆", "解鎖成就 + 升級", "129 個成就等你挑戰\n累積點數提升玩家等級\n拍照存證、分享成就")
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF1A1A2E), Color(0xFF16213E)))
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth().weight(1f)) { page ->
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(pages[page].emoji, fontSize = 72.sp)
                Spacer(modifier = Modifier.height(24.dp))
                Text(pages[page].title, color = Gold, fontWeight = FontWeight.Bold, fontSize = 24.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(12.dp))
                Text(pages[page].desc, color = TextSecondaryDark, fontSize = 15.sp, textAlign = TextAlign.Center, lineHeight = 22.sp)
            }
        }

        // Dots
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(pages.size) { index ->
                Box(
                    modifier = Modifier.size(8.dp).clip(CircleShape)
                        .background(if (pagerState.currentPage == index) Gold else Color.White.copy(alpha = 0.3f))
                )
            }
        }

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth().padding(24.dp, bottom = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onDone,
                modifier = Modifier.weight(1f).height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("跳過", color = TextSecondaryDark, fontSize = 14.sp)
            }
            Button(
                onClick = {
                    if (pagerState.currentPage < pages.size - 1) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        onDone()
                    }
                },
                modifier = Modifier.weight(1f).height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    if (pagerState.currentPage == pages.size - 1) "開始" else "下一步",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
