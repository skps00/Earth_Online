package com.earthonline.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.earthonline.app.data.photo.PhotoManager
import com.earthonline.app.ui.screens.dashboard.DashboardScreen
import com.earthonline.app.ui.screens.dashboard.DashboardViewModel
import com.earthonline.app.ui.theme.EarthOnlineTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var photoManager: PhotoManager

    private lateinit var viewModel: DashboardViewModel
    private var pendingPhotoUri: android.net.Uri? = null

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchCamera()
        }
    }

    private val cameraCaptureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        pendingPhotoUri?.let {
            if (success) {
                viewModel.onEvent(
                    com.earthonline.app.ui.screens.dashboard.DashboardEvent.PhotoTaken(true)
                )
                Toast.makeText(this, getString(R.string.photo_saved), Toast.LENGTH_SHORT).show()
            }
            pendingPhotoUri = null
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
                        onTakePhoto = { handleTakePhoto() }
                    )
                }
            }
        }
    }

    private fun handleTakePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            launchCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchCamera() {
        val uri = photoManager.createPhotoUri()
        pendingPhotoUri = uri
        cameraCaptureLauncher.launch(uri)
    }
}
