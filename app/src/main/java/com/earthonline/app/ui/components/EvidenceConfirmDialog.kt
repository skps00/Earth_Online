package com.earthonline.app.ui.components

// 證據照片確認對話框：顯示拍攝的照片預覽與 AI 分析標籤，供使用者確認或重拍
import android.graphics.BitmapFactory
import android.util.Log
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
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
import com.earthonline.app.ui.theme.EmeraldGreen

private const val TAG = "EvidenceConfirmDialog"

// 顯示證據照片確認對話框：預覽照片與 AI 分析標籤，供確認上傳或重拍
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EvidenceConfirmDialog(
    photoUri: String?,
    analyzedLabels: List<String>,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.evidence_confirm_title), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
        text = {
            Column {
                Text(stringResource(R.string.evidence_confirm_message), color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (photoUri != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    val bitmap = remember(photoUri) {
                        try {
                            context.contentResolver.openInputStream(Uri.parse(photoUri))?.use { BitmapFactory.decodeStream(it) }
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to load evidence photo", e)
                            null
                        }
                    }
                    if (bitmap != null) {
                        Image(bitmap = bitmap.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)))
                    }
                    DisposableEffect(Unit) {
                        onDispose { bitmap?.recycle() }
                    }
                }
                if (analyzedLabels.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow {
                        analyzedLabels.forEach { label ->
                            Text(stringResource(R.string.analyzed_label_format, label), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp, modifier = Modifier.padding(end = 8.dp, bottom = 4.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)) {
                Text(stringResource(R.string.evidence_confirm_btn), color = Color.White)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Text(stringResource(R.string.evidence_retry_btn), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}
