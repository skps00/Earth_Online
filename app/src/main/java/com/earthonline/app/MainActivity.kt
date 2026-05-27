package com.earthonline.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.earthonline.app.AppConstants
import com.earthonline.app.R
import com.earthonline.app.data.ml.ImageAnalyzer
import com.earthonline.app.data.photo.PhotoManager
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
import javax.inject.Inject

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

    private lateinit var viewModel: DashboardViewModel

    private var pendingEvidenceUri: android.net.Uri? = null
    private var pendingEvidenceAchievementId: String? = null
    private var hasLocationPermission = false

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            pendingEvidenceAchievementId?.let { launchEvidenceCapture(it) }
        }
    }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            hasLocationPermission = true
            handleCheckIn()
        }
    }

    private val evidenceCaptureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        val uri = pendingEvidenceUri
        val achievementId = pendingEvidenceAchievementId
        pendingEvidenceUri = null
        pendingEvidenceAchievementId = null

        if (success && uri != null && achievementId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val compressedUri = photoManager.compressPhoto(uri)
                viewModel.setEvidencePhotoPath(achievementId, compressedUri.toString())
                viewModel.onEvent(DashboardEvent.EvidencePhotoTaken(achievementId, true))

                try {
                    val labels = imageAnalyzer.analyze(compressedUri)
                    viewModel.setAnalyzedLabels(labels)
                } catch (_: Exception) {
                    viewModel.setAnalyzedLabels(emptyList())
                }
            }

            Toast.makeText(this, getString(R.string.photo_saved), Toast.LENGTH_SHORT).show()
        }
    }

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkLocationPermission()

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
                        }
                    )
                }
            }
        }
    }

    private fun handleCheckIn() {
        if (!hasLocationPermission) { requestLocationPermission(); return }
        if (!checkInCoordinator.performCheckIn(viewModel)) {
            Toast.makeText(this, getString(R.string.location_unavailable), Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestLocationPermission() {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

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

    private fun launchEvidenceCapture(achievementId: String) {
        val uri = photoManager.createPhotoUri()
        pendingEvidenceUri = uri
        pendingEvidenceAchievementId = achievementId
        evidenceCaptureLauncher.launch(uri)
    }

    private fun checkLocationPermission() {
        hasLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
}
