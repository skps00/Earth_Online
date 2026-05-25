package com.earthonline.app.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.earthonline.app.ui.theme.AccentOrange
import com.earthonline.app.ui.theme.CardDark
import com.earthonline.app.ui.theme.EmeraldGreen
import com.earthonline.app.ui.theme.Gold
import com.earthonline.app.ui.theme.TextSecondaryDark
import java.util.Random

@Composable
fun PetCard(
    pet: PetUiState,
    onRename: (String) -> Unit
) {
    val petEmojis = listOf("🐉", "🦊", "🐱", "🐶", "🦄", "🐲", "🐰", "🐼", "🦋", "🐙")
    val emoji = remember { petEmojis[Random().nextInt(petEmojis.size)] }

    var showRenameDialog by remember { mutableStateOf(false) }
    var renameText by remember { mutableStateOf(pet.name) }

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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(emoji, fontSize = 40.sp, modifier = Modifier.scale(scaleX = -1f, scaleY = 1f))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            pet.name,
                            color = Gold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.clickable {
                                renameText = pet.name
                                showRenameDialog = true
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
                    Spacer(modifier = Modifier.height(8.dp))
                    StatBar("💪 力量", pet.strength, EmeraldGreen)
                    StatBar("⚡ 敏捷", pet.agility, AccentOrange)
                    StatBar("🧠 智力", pet.intelligence, Gold)
                    StatBar("💬 魅力", pet.charisma, EmeraldGreen)
                    StatBar("❤️ 體力", pet.vitality, AccentOrange)
                }
            }
        }
    }

    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("為你的寵物命名", fontWeight = FontWeight.Bold, color = Gold) },
            text = {
                Column {
                    Text("輸入新名字", color = TextSecondaryDark)
                    Spacer(modifier = Modifier.height(8.dp))
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
                }
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    if (renameText.isNotBlank()) onRename(renameText.trim())
                    showRenameDialog = false
                }) {
                    Text("確定", color = Gold)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showRenameDialog = false }) {
                    Text("取消", color = TextSecondaryDark)
                }
            },
            containerColor = com.earthonline.app.ui.theme.DialogDark,
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
