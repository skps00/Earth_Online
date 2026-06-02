package com.earthonline.app.ui.screens.onboarding

// 首次引導畫面，以水平分頁展示應用功能介紹

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.earthonline.app.ui.theme.EmeraldGreen
import com.earthonline.app.ui.theme.Gold
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// 渲染首次引導畫面，包含三個介紹頁面、進度指示器與跳過/下一步按鈕
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onDone: () -> Unit) {
    val pages = remember {
        listOf(
            OnboardingPage("🌍", R.string.onboarding_title_1, R.string.onboarding_desc_1),
            OnboardingPage("📍", R.string.onboarding_title_2, R.string.onboarding_desc_2),
            OnboardingPage("🏆", R.string.onboarding_title_3, R.string.onboarding_desc_3)
        )
    }

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val skipLabel = stringResource(R.string.skip_label)
    val startLabel = stringResource(R.string.start_label)
    val nextLabel = stringResource(R.string.next_label)

    Column(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.surface
            ))
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth().weight(1f)) { page ->
            OnboardingPageContent(
                emoji = pages[page].emoji,
                title = stringResource(pages[page].titleRes),
                description = stringResource(pages[page].descRes)
            )
        }

        OnboardingPageIndicator(pagerState = pagerState, pageCount = pages.size)

        OnboardingBottomButtons(
            pagerState = pagerState,
            pageCount = pages.size,
            scope = scope,
            skipLabel = skipLabel,
            startLabel = startLabel,
            nextLabel = nextLabel,
            onDone = onDone
        )
    }
}

// 渲染單一引導頁內容：emoji 圖示、標題與說明文字
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnboardingPageContent(emoji: String, title: String, description: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(emoji, fontSize = 72.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            title,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            description,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

// 渲染頁面進度指示器：以圓點表示當前頁面位置
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnboardingPageIndicator(pagerState: PagerState, pageCount: Int) {
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (pagerState.currentPage == index) Gold
                        else Color.White.copy(alpha = 0.3f)
                    )
            )
        }
    }
}

// 渲染底部按鈕區：跳過按鈕與下一步／開始按鈕
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnboardingBottomButtons(
    pagerState: PagerState, pageCount: Int, scope: CoroutineScope,
    skipLabel: String, startLabel: String, nextLabel: String, onDone: () -> Unit
) {
    Row(Modifier.fillMaxWidth().padding(24.dp, bottom = 48.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(onClick = onDone, modifier = Modifier.weight(1f).height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(12.dp)) {
            Text(skipLabel, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        }
        Button(onClick = {
            if (pagerState.currentPage < pageCount - 1) {
                scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
            } else { onDone() }
        }, modifier = Modifier.weight(1f).height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
            shape = RoundedCornerShape(12.dp)) {
            Text(if (pagerState.currentPage == pageCount - 1) startLabel else nextLabel,
                color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

private data class OnboardingPage(val emoji: String, @androidx.annotation.StringRes val titleRes: Int, @androidx.annotation.StringRes val descRes: Int)
