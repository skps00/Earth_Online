package com.earthonline.app.ui.screens.settings

// 設定畫面，包含音效、主題、活動追蹤、備份與資料清除功能

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.earthonline.app.R
import com.earthonline.app.AppConstants
import com.earthonline.app.domain.service.SettingsManager
import com.earthonline.app.ui.theme.AccentOrange
import com.earthonline.app.ui.theme.DestructiveRed
import com.earthonline.app.ui.theme.EmeraldGreen

private const val TAG = "SettingsScreen"

// 渲染設定畫面，提供音效開關、主題切換、活動追蹤、隱私政策、備份還原與清除資料
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsManager: SettingsManager,
    onBack: () -> Unit,
    onExportBackup: () -> Unit,
    onImportBackup: () -> Unit,
    onToggleDarkMode: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var soundOn by remember { mutableStateOf(settingsManager.soundEnabled) }
    var darkMode by remember { mutableStateOf(settingsManager.darkModeEnabled) }
    var showClearDialog by remember { mutableStateOf(false) }
    var activityTracking by remember { mutableStateOf(settingsManager.activityTrackingEnabled) }

    BackHandler(enabled = true) { onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title), color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back_label), tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (soundOn) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                        null,
                        tint = if (soundOn) EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(stringResource(R.string.sound_label), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, modifier = Modifier.weight(1f).padding(start = 12.dp))
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(if (darkMode) Icons.Filled.DarkMode else Icons.Filled.LightMode, null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            if (darkMode) stringResource(R.string.theme_dark_mode) else stringResource(R.string.theme_light_mode),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f).padding(start = 12.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.DarkMode, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(stringResource(R.string.theme_dark_mode), color = MaterialTheme.colorScheme.onSurface)
                                }
                            },
                            onClick = {
                                darkMode = true
                                settingsManager.darkModeEnabled = true
                                onToggleDarkMode(true)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.LightMode, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(stringResource(R.string.theme_light_mode), color = MaterialTheme.colorScheme.onSurface)
                                }
                            },
                            onClick = {
                                darkMode = false
                                settingsManager.darkModeEnabled = false
                                onToggleDarkMode(false)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.DirectionsRun, null,
                        tint = if (activityTracking) EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(stringResource(R.string.activity_tracking_label), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, modifier = Modifier.weight(1f).padding(start = 12.dp))
                    Switch(
                        checked = activityTracking,
                        onCheckedChange = {
                            activityTracking = it
                            settingsManager.activityTrackingEnabled = it
                        },
                        colors = SwitchDefaults.colors(checkedTrackColor = EmeraldGreen)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(AppConstants.PRIVACY_POLICY_URL))
                        try { context.startActivity(intent) } catch (e: Exception) { Log.e(TAG, "Failed to open privacy policy URL", e) }
                    },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Shield, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(stringResource(R.string.privacy_policy_label), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, modifier = Modifier.weight(1f).padding(start = 12.dp))
                    Text("\u2197", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onExportBackup, modifier = Modifier.weight(1f).height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(8.dp)
                ) { Text(stringResource(R.string.export_backup_label), color = EmeraldGreen, fontSize = 13.sp) }
                Button(
                    onClick = onImportBackup, modifier = Modifier.weight(1f).height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(8.dp)
                ) { Text(stringResource(R.string.import_backup_label), color = AccentOrange, fontSize = 13.sp) }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { showClearDialog = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Delete, null, tint = DestructiveRed)
                Text(stringResource(R.string.clear_all_data_label), color = DestructiveRed, fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(stringResource(R.string.confirm_clear_label), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
            text = { Text(stringResource(R.string.clear_data_warning), color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                Button(
                    onClick = {
                        settingsManager.clearAllData()
                        showClearDialog = false
                        Toast.makeText(context, context.getString(R.string.data_cleared_toast), Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DestructiveRed)
                ) { Text(stringResource(R.string.confirm_clear_label), color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text(stringResource(R.string.cancel_label), color = MaterialTheme.colorScheme.onSurfaceVariant) }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
