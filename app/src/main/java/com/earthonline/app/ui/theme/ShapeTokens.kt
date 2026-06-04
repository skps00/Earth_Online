package com.earthonline.app.ui.theme

import androidx.compose.ui.unit.dp

data class ShapeTokens(
    val cardRadiusDp: Float = 12f,
    val buttonRadiusDp: Float = 8f,
    val dialogRadiusDp: Float = 16f,
    val chipRadiusDp: Float = 8f
) {
    val cardRadius get() = cardRadiusDp.dp
    val buttonRadius get() = buttonRadiusDp.dp
    val dialogRadius get() = dialogRadiusDp.dp
    val chipRadius get() = chipRadiusDp.dp
}
