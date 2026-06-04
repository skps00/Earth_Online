package com.earthonline.app.ui.theme

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

data class TypographyTokens(
    val displayLargeWeight: FontWeight = FontWeight.Bold,
    val displayLargeLetterSpacing: Float = -0.5f,
    val headlineLargeWeight: FontWeight = FontWeight.Bold,
    val headlineMediumWeight: FontWeight = FontWeight.Bold,
    val titleLargeWeight: FontWeight = FontWeight.Bold,
    val titleLargeSize: Float = 22f,
    val titleMediumWeight: FontWeight = FontWeight.SemiBold,
    val titleMediumSize: Float = 16f,
    val bodyLargeSize: Float = 16f,
    val bodyMediumSize: Float = 14f,
    val bodySmallSize: Float = 12f,
    val labelLargeWeight: FontWeight = FontWeight.SemiBold,
    val labelSmallSize: Float = 10f
)
