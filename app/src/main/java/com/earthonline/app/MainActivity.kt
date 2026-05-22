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
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.earthonline.app.data.location.LocationHelper
import com.earthonline.app.data.ml.ImageAnalyzer
import com.earthonline.app.data.photo.PhotoManager
import com.earthonline.app.domain.service.CheckInCoordinator
import com.earthonline.app.ui.screens.dashboard.DashboardEvent
import com.earthonline.app.ui.screens.dashboard.DashboardScreen
import com.earthonline.app.ui.screens.dashboard.DashboardViewModel
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
            viewModel.setEvidencePhotoPath(achievementId, uri.toString())
            viewModel.onEvent(DashboardEvent.EvidencePhotoTaken(achievementId, true))

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val labels = imageAnalyzer.analyze(uri)
                    viewModel.setAnalyzedLabels(labels)
                } catch (_: Exception) {
                    viewModel.setAnalyzedLabels(emptyList())
                }
            }

            Toast.makeText(this, getString(R.string.photo_saved), Toast.LENGTH_SHORT).show()
        }
    }

    private val exportLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
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
            EarthOnlineTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val vm: DashboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                    viewModel = vm

                    DashboardScreen(
                        viewModel = vm,
                        onCheckIn = { requestLocationPermission() },
                        onTakeEvidencePhoto = { id -> handleEvidencePhoto(id) },
                        onExportBackup = { exportLauncher.launch("earth_online_backup.json") },
                        onImportBackup = { importLauncher.launch(arrayOf("application/json")) }
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
