package com.earthonline.app.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.earthonline.app.R
import com.earthonline.app.domain.model.Rarity
import com.earthonline.app.domain.model.TriggerType
import com.earthonline.app.ui.screens.dashboard.AchievementDisplayItem
import com.earthonline.app.ui.share.ShareHelper
import com.earthonline.app.ui.theme.AchievementLocked
import com.earthonline.app.ui.theme.CardDark
import com.earthonline.app.ui.theme.DeepBlue
import com.earthonline.app.ui.theme.DialogDark
import com.earthonline.app.ui.theme.EmeraldGreen
import com.earthonline.app.ui.theme.Gold
import com.earthonline.app.ui.theme.TextPrimaryDark
import com.earthonline.app.ui.theme.TextSecondaryDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AchievementDetailDialog(
    item: AchievementDisplayItem,
    evidencePhotoPath: String?,
    allEvidencePaths: List<String> = emptyList(),
    onDismiss: () -> Unit,
    onTakeEvidencePhoto: () -> Unit,
    onManualConfirm: () -> Unit
) {
    val isUnlocked = item.progress.isUnlocked
    val progress = item.progress.currentProgress
    val goal = item.definition.triggerGoal
    val progressFraction = if (goal > 0) (progress.toFloat() / goal).coerceIn(0f, 1f) else 0f
    val isManual = item.definition.triggerType == TriggerType.MANUAL_CONFIRM.value || item.definition.triggerType == TriggerType.AUTO_TRACK.value
    val isHidden = item.definition.isHidden && !isUnlocked
    var revealed by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var showAllEvidence by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()) }
    val evidenceCount = allEvidencePaths.size

    @Composable
    fun loadBitmap(path: String) = remember(path) {
        try {
            val uri = android.net.Uri.parse(path)
            context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
        } catch (_: Exception) { null }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isHidden && !revealed) "???" else item.definition.title,
                fontWeight = FontWeight.Bold,
                color = if (isUnlocked) Gold else if (isHidden && !revealed) Rarity.LEGENDARY.color.copy(alpha = 0.7f) else TextPrimaryDark
            )
        },
        text = {
            if (isHidden && !revealed) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.hidden_achievement_label), style = MaterialTheme.typography.bodyMedium, color = TextSecondaryDark)
                    if (item.definition.hint.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("💡 ${item.definition.hint}", style = MaterialTheme.typography.bodySmall, color = Rarity.RARE.color)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { revealed = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Rarity.LEGENDARY.color.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.reveal_achievement_btn), color = Rarity.LEGENDARY.color, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Column {
                    Text(item.definition.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondaryDark)
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(progress = progressFraction, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)), color = if (isUnlocked) Gold else EmeraldGreen, trackColor = AchievementLocked.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(if (isUnlocked) stringResource(R.string.unlocked_badge) else "$progress / $goal", style = MaterialTheme.typography.labelSmall, color = if (isUnlocked) Gold else TextSecondaryDark)
                    if (isUnlocked && item.progress.unlockedDate != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("${stringResource(R.string.unlock_time_label)}: ${dateFormat.format(Date(item.progress.unlockedDate))}", style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.7f))
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(stringResource(R.string.not_unlocked_yet), style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(stringResource(R.string.reward_points_format, item.definition.rewardPoints), style = MaterialTheme.typography.titleMedium, color = Gold, fontWeight = FontWeight.SemiBold)

                    if (isUnlocked && evidenceCount > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        if (!showAllEvidence) {
                            val bitmap = evidencePhotoPath?.let { loadBitmap(it) }
                            if (bitmap != null) {
                                Image(bitmap = bitmap.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)))
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            if (evidenceCount > 1) {
                                Button(
                                    onClick = { showAllEvidence = true },
                                    modifier = Modifier.fillMaxWidth().height(32.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CardDark),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("查看全部 ${evidenceCount} 張證據照", color = Gold, fontSize = 11.sp)
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier.heightIn(max = 300.dp).verticalScroll(rememberScrollState())
                            ) {
                                allEvidencePaths.forEachIndexed { i, path ->
                                val bitmap = loadBitmap(path)
                                if (bitmap != null) {
                                    Text("#${i + 1}", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Image(bitmap = bitmap.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(10.dp)))
                                    Spacer(modifier = Modifier.height(6.dp))
                                }
                            }
                            }  // close scrollable Column
                            Button(
                                onClick = { showAllEvidence = false },
                                modifier = Modifier.fillMaxWidth().height(32.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CardDark),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("收起", color = TextSecondaryDark, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { ShareHelper.shareAchievement(context, item.definition.title, item.definition.description, item.definition.rewardPoints) },
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Gold.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Filled.Share, null, tint = Gold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.share_achievement), color = Gold, fontSize = 13.sp)
                    }
                }
            }
        },
        dismissButton = {
            if (isManual && !isUnlocked && (!isHidden || revealed)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = onTakeEvidencePhoto, modifier = Modifier.weight(1f).height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = Gold), shape = RoundedCornerShape(12.dp)) { Text(stringResource(R.string.evidence_take_photo), color = DeepBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                    Button(onClick = onManualConfirm, modifier = Modifier.weight(1f).height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen), shape = RoundedCornerShape(12.dp)) { Text(stringResource(R.string.manual_confirm_btn), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().height(44.dp), colors = ButtonDefaults.buttonColors(containerColor = CardDark), shape = RoundedCornerShape(12.dp)) { Text(stringResource(R.string.food_confirm_no), color = TextSecondaryDark, fontSize = 14.sp) }
        },
        containerColor = DialogDark,
        shape = RoundedCornerShape(16.dp)
    )
}
