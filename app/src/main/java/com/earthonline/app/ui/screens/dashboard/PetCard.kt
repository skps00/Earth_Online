package com.earthonline.app.ui.screens.dashboard

// 寵物卡片元件，顯示寵物表情、名稱、等級與屬性，支援更名與換裝

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.earthonline.app.AppConstants
import com.earthonline.app.R
import com.earthonline.app.ui.theme.AccentOrange
import com.earthonline.app.ui.theme.DeepBlue
import com.earthonline.app.ui.theme.EmeraldGreen
import com.earthonline.app.ui.theme.Gold
import kotlinx.coroutines.delay
import kotlin.random.Random

private val ALL_PET_EMOJIS = listOf(
    "🐉" to R.string.stat_strength,
    "🦊" to R.string.stat_agility,
    "🐱" to R.string.stat_intelligence,
    "🐶" to R.string.stat_charisma,
    "🦄" to R.string.stat_vitality,
    "🐲" to R.string.stat_strength,
    "🐰" to R.string.stat_agility,
    "🐼" to R.string.stat_charisma,
    "🦋" to R.string.stat_charisma,
    "🐙" to R.string.stat_intelligence,
    "🐢" to R.string.stat_vitality,
    "🦅" to R.string.stat_agility
)

private val STAT_KEYS = listOf(
    Triple(R.string.stat_strength, R.string.stat_strength_desc, EmeraldGreen),
    Triple(R.string.stat_agility, R.string.stat_agility_desc, AccentOrange),
    Triple(R.string.stat_intelligence, R.string.stat_intelligence_desc, Gold),
    Triple(R.string.stat_charisma, R.string.stat_charisma_desc, EmeraldGreen),
    Triple(R.string.stat_vitality, R.string.stat_vitality_desc, AccentOrange)
)

// 渲染寵物卡片，包含表情動畫、對話泡泡、屬性條與自訂對話框
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PetCard(
    pet: PetUiState,
    onRename: (String) -> Unit,
    onChangeEmoji: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var renameText by remember { mutableStateOf(pet.name) }
    var selectedEmoji by remember { mutableStateOf(pet.emoji) }
    var bounceTrigger by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val speechBubbles = remember { context.resources.getStringArray(R.array.speech_bubbles).toList() }
    val bounceScale by animateFloatAsState(
        targetValue = if (bounceTrigger > 0) 1.3f else 1f,
        animationSpec = spring(dampingRatio = AppConstants.BOUNCE_DAMPING_RATIO, stiffness = AppConstants.BOUNCE_STIFFNESS),
        finishedListener = { if (bounceTrigger > 0) bounceTrigger = 0 }
    )

    var currentBubble by remember { mutableStateOf<String?>(null) }
    var displayedBubble by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(pet.dialogueTrigger) {
        val trigger = pet.dialogueTrigger ?: return@LaunchedEffect
        val lines = scriptedDialogueLines(trigger)
        for (line in lines) {
            currentBubble = line
            displayedBubble = line
            delay(2000)
        }
        currentBubble = null
        delay(200)
        displayedBubble = null
    }

    // Random dialogue loop
    LaunchedEffect(Unit) {
        while (true) {
            delay(AppConstants.SPEECH_BUBBLE_MIN_INTERVAL_MS + Random.nextLong(AppConstants.SPEECH_BUBBLE_MAX_EXTRA_MS))
            if (pet.dialogueTrigger != null) continue
            val text = speechBubbles[Random.nextInt(speechBubbles.size)]
            currentBubble = text
            displayedBubble = text
            delay(AppConstants.SPEECH_BUBBLE_DISPLAY_MS)
            currentBubble = null
            delay(200)
            displayedBubble = null
        }
    }

    val clickToChange = stringResource(R.string.click_to_change)
    val levelLabel = stringResource(R.string.level_format, pet.level)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.width(56.dp).height(80.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text(
                            pet.emoji,
                            fontSize = 40.sp,
                            modifier = Modifier
                                .scale(bounceScale)
                                .clickable {
                                    bounceTrigger++
                                    selectedEmoji = pet.emoji
                                    renameText = pet.name
                                    showDialog = true
                                }
                        )
                        Text(clickToChange, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
                    }
                    androidx.compose.animation.AnimatedVisibility(
                        visible = displayedBubble != null,
                        enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 2 },
                        exit = fadeOut(tween(200)) + slideOutVertically(tween(200)) { it / 2 },
                        modifier = Modifier.align(Alignment.TopCenter).offset(y = (-28).dp).zIndex(1f)
                    ) {
                        displayedBubble?.let { bubble ->
                            Text(
                                bubble,
                                color = DeepBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(Gold.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            pet.name,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.clickable {
                                bounceTrigger++
                                selectedEmoji = pet.emoji
                                renameText = pet.name
                                showDialog = true
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(levelLabel, color = AccentOrange, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    val statMax = (pet.level * 2 + 10).coerceAtLeast(1)
                    val stats = listOf(pet.strength, pet.agility, pet.intelligence, pet.charisma, pet.vitality)
                    STAT_KEYS.forEachIndexed { i, (labelRes, _, color) ->
                        StatBar(stringResource(labelRes), stats[i], statMax, color)
                    }
                }
            }
        }
    }

    if (showDialog) {
        val customizeTitle = stringResource(R.string.customize_pet_title)
        val choosePetLabel = stringResource(R.string.choose_pet_label)
        val petNameLabel = stringResource(R.string.pet_name_label)
        val statDescLabel = stringResource(R.string.stat_description_label)
        val confirmLabel = stringResource(R.string.confirm_label)
        val cancelLabel = stringResource(R.string.cancel_label)

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(customizeTitle, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
            text = { 
                Column {
                    Text(choosePetLabel, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ALL_PET_EMOJIS.forEach { (emoji, _) ->
                            val isSelected = emoji == selectedEmoji
                            Card(
                                modifier = Modifier.size(52.dp).clickable { selectedEmoji = emoji },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Gold.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Text(emoji, fontSize = 22.sp)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(petNameLabel, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(
                        value = renameText,
                        onValueChange = { renameText = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Gold,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            cursorColor = Gold
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(statDescLabel, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    STAT_KEYS.forEach { (labelRes, descRes, _) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(labelRes), fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("←", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(descRes), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (renameText.isNotBlank()) onRename(renameText.trim())
                    onChangeEmoji(selectedEmoji)
                    showDialog = false
                }) {
                    Text(confirmLabel, color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(cancelLabel, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

// 渲染單條屬性進度條，顯示標籤、數值與彩色進度指示器
@Composable
private fun StatBar(label: String, value: Int, max: Int, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
            Text("$value", color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = (value.toFloat() / max).coerceIn(0f, 1f),
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}

private fun scriptedDialogueLines(trigger: String): List<String> {
    return when (trigger) {
        "earthquake" -> listOf("剛剛有地震？！", "我在這裡陪你", "不用擔心，我們很安全")
        "storm" -> listOf("暴風雨來了...", "躲在家裡最安全！", "記得關好窗戶喔")
        "rain" -> listOf("下雨了耶～", "我喜歡雨天的味道", "記得帶傘！")
        "extreme_heat" -> listOf("熱死了！！！", "快喝水！", "冷氣開到最強～")
        "lightning" -> listOf("打雷了！", "轟隆隆——", "我在這不怕")
        "level_up" -> listOf("升級了！", "我們一起變強吧！", "繼續加油～")
        "explore_japan" -> listOf("日本耶！", "帶我去吃拉麵！", "幫我拍富士山～")
        "earlybird" -> listOf("早起的鳥兒～", "你今天好早起！", "充滿活力！")
        "allnighter" -> listOf("通宵了？", "要注意身體喔", "早點休息～")
        "no_phone" -> listOf("一整天沒用手機？", "好厲害的數位排毒！", "繼續保持！")
        "biking" -> listOf("風在呼嘯！", "你騎得好快！", "注意安全喔～")
        else -> listOf("哇！新成就！", "太厲害了！", "我就知道你辦得到")
    }
}
