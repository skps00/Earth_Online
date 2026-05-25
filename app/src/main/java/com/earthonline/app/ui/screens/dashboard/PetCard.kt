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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.earthonline.app.R
import com.earthonline.app.ui.theme.AccentOrange
import com.earthonline.app.ui.theme.CardDark
import com.earthonline.app.ui.theme.DialogDark
import com.earthonline.app.ui.theme.EmeraldGreen
import com.earthonline.app.ui.theme.Gold
import com.earthonline.app.ui.theme.TextSecondaryDark

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

    val clickToChange = stringResource(R.string.click_to_change)
    val levelLabel = stringResource(R.string.level_format, pet.level)

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
                    Text(clickToChange, color = TextSecondaryDark, fontSize = 9.sp)
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
            title = { Text(customizeTitle, fontWeight = FontWeight.Bold, color = Gold) },
            text = {
                Column {
                    Text(choosePetLabel, color = TextSecondaryDark, fontSize = 13.sp)
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
                                    containerColor = if (isSelected) Gold.copy(alpha = 0.3f) else CardDark
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
                    Text(petNameLabel, color = TextSecondaryDark, fontSize = 13.sp)
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
                    Text(statDescLabel, color = TextSecondaryDark, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    STAT_KEYS.forEach { (labelRes, descRes, _) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(labelRes), fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("←", color = TextSecondaryDark, fontSize = 9.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(descRes), color = TextSecondaryDark, fontSize = 10.sp)
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
                    Text(confirmLabel, color = Gold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(cancelLabel, color = TextSecondaryDark)
                }
            },
            containerColor = DialogDark,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun StatBar(label: String, value: Int, max: Int, color: androidx.compose.ui.graphics.Color) {
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
            progress = (value.toFloat() / max).coerceIn(0f, 1f),
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}
