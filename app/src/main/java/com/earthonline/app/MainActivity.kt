package com.earthonline.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.earthonline.app.AppConstants
import com.earthonline.app.R
import com.earthonline.app.data.ml.ImageAnalyzer
import com.earthonline.app.data.photo.PhotoManager
import com.earthonline.app.data.activity.ActivityRecognitionManager
import com.earthonline.app.domain.service.CheckInCoordinator
import com.earthonline.app.domain.service.SettingsManager
import com.earthonline.app.ui.screens.dashboard.DashboardEvent
import com.earthonline.app.ui.screens.dashboard.DashboardViewModel
import com.earthonline.app.ui.navigation.AppNavigation
import com.earthonline.app.ui.theme.EarthOnlineTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

// 應用程式主入口 Activity，管理權限請求、拍照證據、備份匯入匯出等啟動器
private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var photoManager: PhotoManager

    @Inject
    lateinit var imageAnalyzer: ImageAnalyzer

    @Inject
    lateinit var checkInCoordinator: CheckInCoordinator

    @Inject
    lateinit var settingsManager: SettingsManager

    @Inject
    lateinit var activityRecognitionManager: ActivityRecognitionManager

    private lateinit var viewModel: DashboardViewModel

    private var pendingEvidenceUri: android.net.Uri? = null
    private var pendingEvidenceAchievementId: String? = null
    private var hasLocationPermission = false
    private var hasActivityPermission = false
    private var showLocationRationale by mutableStateOf(false)
    private var showCameraRationale by mutableStateOf(false)
    private var locationRationaleShown = false
    private var cameraRationaleShown = false

    // 活動識別權限請求，授權後啟動活動追蹤
    private val activityPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasActivityPermission = granted
        if (granted) activityRecognitionManager.startTracking()
    }

    // 相機權限請求，授權後啟動證據拍照
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            pendingEvidenceAchievementId?.let { launchEvidenceCapture(it) }
        } else if (!cameraRationaleShown) {
            showCameraRationale = true
            cameraRationaleShown = true
        }
    }

    // 位置權限請求，授權後執行打卡
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            hasLocationPermission = true
            handleCheckIn()
        } else if (!locationRationaleShown) {
            showLocationRationale = true
            locationRationaleShown = true
        }
    }

    // 拍照完成後壓縮照片、AI 分析標籤並儲存證據路徑
    private val evidenceCaptureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        val uri = pendingEvidenceUri
        val achievementId = pendingEvidenceAchievementId
        pendingEvidenceUri = null
        pendingEvidenceAchievementId = null

        if (success && uri != null && achievementId != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                val compressedUri = photoManager.compressPhoto(uri)
                viewModel.setEvidencePhotoPath(achievementId, compressedUri.toString())
                viewModel.onEvent(DashboardEvent.EvidencePhotoTaken(achievementId, true))

                try {
                    val labels = imageAnalyzer.analyze(compressedUri)
                    viewModel.setAnalyzedLabels(labels)
                } catch (e: Exception) {
                    Log.e(TAG, "AI image analysis failed", e)
                    viewModel.setAnalyzedLabels(emptyList())
                }
            }

            Toast.makeText(this, getString(R.string.photo_saved), Toast.LENGTH_SHORT).show()
        }
    }

    // 備份匯出：建立 JSON 文件供 SAF 選擇儲存位置
    private val exportLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument(AppConstants.MIME_JSON)
    ) { uri ->
        if (uri != null) {
            lifecycleScope.launch {
                viewModel.exportBackup(uri)
                Toast.makeText(this@MainActivity, getString(R.string.backup_exported), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 備份匯入：開啟文件選擇器選取 JSON 備份檔
    private val importLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            lifecycleScope.launch {
                viewModel.importBackup(uri)
                Toast.makeText(this@MainActivity, getString(R.string.backup_imported), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 初始化：檢查權限狀態，設定 Compose UI、主題與導航回調
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkLocationPermission()
        checkActivityPermission()

        setContent {
            var darkMode by remember { mutableStateOf(settingsManager.darkModeEnabled) }
            EarthOnlineTheme(darkTheme = darkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val vm: DashboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                    viewModel = vm

                        AppNavigation(
                            viewModel = vm,
                            settingsManager = settingsManager,
                            onCheckIn = { requestLocationPermission() },
                            onTakeEvidencePhoto = { id -> handleEvidencePhoto(id) },
                            onExportBackup = { exportLauncher.launch(AppConstants.DEFAULT_BACKUP_FILENAME) },
                            onImportBackup = { importLauncher.launch(arrayOf(AppConstants.MIME_JSON)) },
                            onToggleDarkMode = { enabled ->
                                settingsManager.darkModeEnabled = enabled
                                darkMode = enabled
                            },
                            onRequestActivityPermission = { requestActivityPermission() }
                        )
                    if (showLocationRationale) {
                        PermissionRationaleDialog(
                            title = getString(R.string.location_rationale_title),
                            message = getString(R.string.location_rationale_message),
                            tryAgainText = getString(R.string.error_retry),
                            dismissText = getString(R.string.cancel_label),
                            onTryAgain = {
                                showLocationRationale = false
                                requestLocationPermission()
                            },
                            onDismiss = { showLocationRationale = false }
                        )
                    }
                    if (showCameraRationale) {
                        PermissionRationaleDialog(
                            title = getString(R.string.camera_rationale_title),
                            message = getString(R.string.camera_rationale_message),
                            tryAgainText = getString(R.string.error_retry),
                            dismissText = getString(R.string.cancel_label),
                            onTryAgain = {
                                showCameraRationale = false
                                pendingEvidenceAchievementId?.let { handleEvidencePhoto(it) }
                            },
                            onDismiss = { showCameraRationale = false }
                        )
                    }
                }
            }
        }
    }

    // 執行打卡：檢查位置權限後委託 CheckInCoordinator 處理
    private fun handleCheckIn() {
        if (!hasLocationPermission) { requestLocationPermission(); return }
        lifecycleScope.launch(Dispatchers.IO) {
            if (!checkInCoordinator.performCheckIn(viewModel)) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, getString(R.string.location_gps_unavailable), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 請求精確位置權限
    private fun requestLocationPermission() {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // 處理證據拍照：檢查相機權限，已授權則直接啟動
    private fun handleEvidencePhoto(achievementId: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            launchEvidenceCapture(achievementId)
        } else {
            pendingEvidenceAchievementId = achievementId
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // 建立照片 URI 並啟動相機應用
    private fun launchEvidenceCapture(achievementId: String) {
        val uri = photoManager.createPhotoUri()
        pendingEvidenceUri = uri
        pendingEvidenceAchievementId = achievementId
        evidenceCaptureLauncher.launch(uri)
    }

    // 檢查精確或粗略位置權限是否已授權
    private fun checkLocationPermission() {
        hasLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // 請求 Google Play 服務的活動識別權限
    private fun requestActivityPermission() {
        activityPermissionLauncher.launch("com.google.android.gms.permission.ACTIVITY_RECOGNITION")
    }

    // 檢查活動識別權限，已授權則啟動追蹤
    private fun checkActivityPermission() {
        hasActivityPermission = ContextCompat.checkSelfPermission(this, "com.google.android.gms.permission.ACTIVITY_RECOGNITION") == PackageManager.PERMISSION_GRANTED
        if (hasActivityPermission) {
            activityRecognitionManager.startTracking()
        }
    }
}

@Composable
private fun PermissionRationaleDialog(
    title: String,
    message: String,
    tryAgainText: String,
    dismissText: String,
    onTryAgain: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        },
        text = {
            Text(text = message, color = MaterialTheme.colorScheme.onSurface)
        },
        confirmButton = {
            Button(
                onClick = onTryAgain,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(tryAgainText)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(dismissText)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}
