package com.earthonline.app.ui.screens.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.earthonline.app.ui.theme.AccentOrange
import com.earthonline.app.ui.theme.CardDark
import com.earthonline.app.ui.theme.DialogDark
import com.earthonline.app.ui.theme.EmeraldGreen
import com.earthonline.app.ui.theme.Gold
import com.earthonline.app.ui.theme.TextSecondaryDark

private val ALL_PETS = listOf(
    "🐉" to "龍",
    "🦊" to "狐狸",
    "🐱" to "貓咪",
    "🐶" to "小狗",
    "🦄" to "獨角獸",
    "🐲" to "青龍",
    "🐰" to "兔子",
    "🐼" to "熊貓",
    "🦋" to "蝴蝶",
    "🐙" to "章魚",
    "🐢" to "烏龜",
    "🦅" to "老鷹"
)

private val STAT_EXPLANATIONS = mapOf(
    "💪 力量" to "解鎖 史詩/隱藏 成就",
    "⚡ 敏捷" to "解鎖 探索/旅遊 成就",
    "🧠 智力" to "解鎖 職場/學業 成就",
    "💬 魅力" to "解鎖 日常/社交 成就",
    "❤️ 體力" to "解鎖 健康/交通 成就"
)

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

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(56.dp)
                ) {
                    Text(
                        pet.emoji,
                        fontSize = 40.sp,
                        modifier = Modifier.clickable {
                            selectedEmoji = pet.emoji
                            renameText = pet.name
                            showDialog = true
                        }
                    )
                    Text(
                        "點擊更換",
                        color = TextSecondaryDark,
                        fontSize = 9.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            pet.name,
                            color = Gold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.clickable {
                                selectedEmoji = pet.emoji
                                renameText = pet.name
                                showDialog = true
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Lv.${pet.level}",
                            color = AccentOrange,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    StatBar("💪 力量", pet.strength, EmeraldGreen)
                    StatBar("⚡ 敏捷", pet.agility, AccentOrange)
                    StatBar("🧠 智力", pet.intelligence, Gold)
                    StatBar("💬 魅力", pet.charisma, EmeraldGreen)
                    StatBar("❤️ 體力", pet.vitality, AccentOrange)
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("自訂寵物", fontWeight = FontWeight.Bold, color = Gold) },
            text = {
                Column {
                    Text("選擇寵物：", color = TextSecondaryDark, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ALL_PETS.forEach { (emoji, label) ->
                            val isSelected = emoji == selectedEmoji
                            Card(
                                modifier = Modifier.size(52.dp).clickable { selectedEmoji = emoji },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Gold.copy(alpha = 0.3f) else CardDark
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Text(emoji, fontSize = 22.sp)
                                    Text(label, color = TextSecondaryDark, fontSize = 8.sp)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("寵物名字：", color = TextSecondaryDark, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(
                        value = renameText,
                        onValueChange = { renameText = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Gold,
                            unfocusedTextColor = TextSecondaryDark,
                            focusedContainerColor = CardDark,
                            unfocusedContainerColor = CardDark,
                            cursorColor = Gold
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("屬性說明：", color = TextSecondaryDark, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    STAT_EXPLANATIONS.forEach { (label, desc) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(label, fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("←", color = TextSecondaryDark, fontSize = 9.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(desc, color = TextSecondaryDark, fontSize = 10.sp)
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
                    Text("確定", color = Gold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("取消", color = TextSecondaryDark)
                }
            },
            containerColor = DialogDark,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun StatBar(label: String, value: Int, color: androidx.compose.ui.graphics.Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = TextSecondaryDark, fontSize = 11.sp)
            Text("$value", color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = (value / 10f).coerceIn(0f, 1f),
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}
