package com.earthonline.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.earthonline.app.R
import com.earthonline.app.ui.theme.AchievementLocked
import com.earthonline.app.ui.theme.EmeraldGreen
import com.earthonline.app.ui.theme.Gold
import com.earthonline.app.ui.theme.TextSecondaryDark

@Composable
fun EvidenceConfirmDialog(
    analyzedLabels: List<String>,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.evidence_confirm_title), fontWeight = FontWeight.Bold, color = Gold) },
        text = {
            Column {
                Text(stringResource(R.string.evidence_confirm_message), color = TextSecondaryDark)
                if (analyzedLabels.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    analyzedLabels.forEach { label ->
                        Text("• $label", color = TextSecondaryDark, fontSize = 13.sp)
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
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = AchievementLocked)) {
                Text(stringResource(R.string.evidence_retry_btn), color = TextSecondaryDark)
            }
        },
        containerColor = DialogDark,
        shape = RoundedCornerShape(16.dp)
    )
}
