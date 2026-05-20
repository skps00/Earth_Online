package com.earthonline.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.earthonline.app.data.ml.ImageAnalyzer
import com.earthonline.app.data.photo.PhotoManager
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

    private lateinit var viewModel: DashboardViewModel

    private var pendingEvidenceUri: android.net.Uri? = null
    private var pendingEvidenceAchievementId: String? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EarthOnlineTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val vm: DashboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                    viewModel = vm

                    DashboardScreen(
                        viewModel = vm,
                        onCheckIn = { requestLocationPermission() },
                        onTakeEvidencePhoto = { id -> handleEvidencePhoto(id) }
                    )
                }
            }
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            handleCheckIn()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun handleCheckIn() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, getString(R.string.location_needed), Toast.LENGTH_SHORT).show()
            return
        }

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        for (provider in providers) {
            if (locationManager.isProviderEnabled(provider)) {
                try {
                    val location = locationManager.getLastKnownLocation(provider)
                    if (location != null) {
                        val (address, country, continent) = photoManager.reverseGeocode(location.latitude, location.longitude)
                        viewModel.setPendingLocation(location.latitude, location.longitude, address, country, continent)
                        return
                    }
                } catch (_: Exception) {
                }
            }
        }

        Toast.makeText(this, getString(R.string.location_unavailable), Toast.LENGTH_SHORT).show()
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
}
