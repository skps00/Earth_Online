package com.earthonline.app.ui.screens.map

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.earthonline.app.data.local.entity.CheckInRecord
import com.earthonline.app.ui.theme.Gold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInMapScreen(
    records: List<CheckInRecord>,
    onBack: () -> Unit
) {
    val markerScript = remember(records) {
        records.joinToString("\n") { record ->
            "addMarker(${record.latitude}, ${record.longitude}, '${record.country.ifBlank { "打卡點" }}');"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("打卡地圖", color = Gold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "返回", tint = Gold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A1A2E))
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                if (view != null && markerScript.isNotBlank()) {
                                    view.evaluateJavascript(markerScript, null)
                                }
                            }
                        }
                        loadUrl("file:///android_asset/map.html")
                    }
                }
            )
        }
    }
}

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("打卡地圖", color = Gold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "返回", tint = Gold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A1A2E))
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        settings.allowFileAccess = true
                        webViewClient = WebViewClient()
                        loadDataWithBaseURL("https://unpkg.com/", html, "text/html", "UTF-8", null)
                    }
                }
            )
        }
    }
}
