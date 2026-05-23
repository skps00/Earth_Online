package com.earthonline.app.ui.screens.settings

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.earthonline.app.domain.service.SettingsManager
import com.earthonline.app.ui.theme.AchievementLocked
import com.earthonline.app.ui.theme.AccentOrange
import com.earthonline.app.ui.theme.CardDark
import com.earthonline.app.ui.theme.EmeraldGreen
import com.earthonline.app.ui.theme.EmeraldGreen
import com.earthonline.app.ui.theme.Gold
import com.earthonline.app.ui.theme.TextSecondaryDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsManager: SettingsManager,
    onBack: () -> Unit,
    onExportBackup: () -> Unit,
    onImportBackup: () -> Unit
) {
    val context = LocalContext.current
    var soundOn by remember { mutableStateOf(settingsManager.soundEnabled) }
    var showClearDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = true) { onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("設定", color = Gold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "返回", tint = Gold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepBlue)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepBlue)
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (soundOn) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                        null,
                        tint = if (soundOn) EmeraldGreen else TextSecondaryDark
                    )
                    Text("音效", color = TextSecondaryDark, fontSize = 14.sp, modifier = Modifier.weight(1f).padding(start = 12.dp))
                    Switch(
                        checked = soundOn,
                        onCheckedChange = {
                            soundOn = it
                            settingsManager.soundEnabled = it
                        },
                        colors = SwitchDefaults.colors(checkedTrackColor = EmeraldGreen)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Settings, null, tint = TextSecondaryDark)
                    Text("應用程式權限", color = TextSecondaryDark, fontSize = 14.sp, modifier = Modifier.weight(1f).padding(start = 12.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onExportBackup, modifier = Modifier.weight(1f).height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("匯出備份", color = EmeraldGreen, fontSize = 13.sp) }
                Button(
                    onClick = onImportBackup, modifier = Modifier.weight(1f).height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("匯入備份", color = AccentOrange, fontSize = 13.sp) }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { showClearDialog = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AchievementLocked),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Delete, null, tint = DestructiveRed)
                Text("清除所有資料", color = DestructiveRed, fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("確認清除", fontWeight = FontWeight.Bold, color = Gold) },
            text = { Text("這將刪除所有打卡記錄和成就進度，無法復原。", color = TextSecondaryDark) },
            confirmButton = {
                Button(
                    onClick = {
                        settingsManager.clearAllData()
                        showClearDialog = false
                        Toast.makeText(context, "資料已清除，請重啟 App", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DestructiveRed)
                ) { Text("確認清除", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("取消", color = TextSecondaryDark) }
            },
            containerColor = DialogDark,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
