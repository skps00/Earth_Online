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
import com.earthonline.app.R
import com.earthonline.app.ui.theme.AchievementLocked
import com.earthonline.app.ui.theme.DialogDark
import com.earthonline.app.ui.theme.EmeraldGreen
import com.earthonline.app.ui.theme.Gold
import com.earthonline.app.ui.theme.TextSecondaryDark

@Composable
fun CheckInConfirmDialog(
    address: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.checkin_confirm_title), fontWeight = FontWeight.Bold, color = Gold) },
        text = {
            Column {
                Text(address, color = TextSecondaryDark)
                Spacer(modifier = Modifier.height(8.dp))
                Text("${stringResource(R.string.checkin_action)}?", color = TextSecondaryDark)
            }
        },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)) {
                Text(stringResource(R.string.checkin_confirm_yes), color = Color.White)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = AchievementLocked)) {
                Text(stringResource(R.string.food_confirm_no), color = TextSecondaryDark)
            }
        },
        containerColor = DialogDark,
        shape = RoundedCornerShape(16.dp)
    )
}
