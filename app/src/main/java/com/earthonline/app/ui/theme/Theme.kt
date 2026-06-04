package com.earthonline.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

private fun buildTypography(tokens: TypographyTokens) = Typography(
    displayLarge = Typography().displayLarge.copy(
        fontWeight = tokens.displayLargeWeight,
        letterSpacing = tokens.displayLargeLetterSpacing.sp
    ),
    headlineLarge = Typography().headlineLarge.copy(fontWeight = tokens.headlineLargeWeight),
    headlineMedium = Typography().headlineMedium.copy(fontWeight = tokens.headlineMediumWeight),
    titleLarge = Typography().titleLarge.copy(
        fontWeight = tokens.titleLargeWeight,
        fontSize = tokens.titleLargeSize.sp
    ),
    titleMedium = Typography().titleMedium.copy(
        fontWeight = tokens.titleMediumWeight,
        fontSize = tokens.titleMediumSize.sp
    ),
    bodyLarge = Typography().bodyLarge.copy(fontSize = tokens.bodyLargeSize.sp),
    bodyMedium = Typography().bodyMedium.copy(fontSize = tokens.bodyMediumSize.sp),
    bodySmall = Typography().bodySmall.copy(fontSize = tokens.bodySmallSize.sp),
    labelLarge = Typography().labelLarge.copy(fontWeight = tokens.labelLargeWeight),
    labelSmall = Typography().labelSmall.copy(fontSize = tokens.labelSmallSize.sp)
)

@Composable
fun EarthOnlineTheme(
    themeConfig: ThemeConfig = ThemeConfig.default,
    content: @Composable () -> Unit
) {
    val colorScheme = remember(themeConfig) {
        if (themeConfig.isDark) {
            darkColorScheme(
                primary = themeConfig.colors.primary,
                secondary = themeConfig.colors.secondary,
                tertiary = themeConfig.colors.tertiary,
                background = themeConfig.colors.background,
                surface = themeConfig.colors.surface,
                surfaceVariant = themeConfig.colors.surfaceVariant,
                onPrimary = themeConfig.colors.onPrimary,
                onSecondary = themeConfig.colors.onSecondary,
                onTertiary = themeConfig.colors.onTertiary,
                onBackground = themeConfig.colors.onBackground,
                onSurface = themeConfig.colors.onSurface,
                onSurfaceVariant = themeConfig.colors.onSurfaceVariant
            )
        } else {
            lightColorScheme(
                primary = themeConfig.colors.primary,
                secondary = themeConfig.colors.secondary,
                tertiary = themeConfig.colors.tertiary,
                background = themeConfig.colors.background,
                surface = themeConfig.colors.surface,
                surfaceVariant = themeConfig.colors.surfaceVariant,
                onPrimary = themeConfig.colors.onPrimary,
                onSecondary = themeConfig.colors.onSecondary,
                onTertiary = themeConfig.colors.onTertiary,
                onBackground = themeConfig.colors.onBackground,
                onSurface = themeConfig.colors.onSurface,
                onSurfaceVariant = themeConfig.colors.onSurfaceVariant
            )
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity ?: return@SideEffect
            val window = activity.window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !themeConfig.isDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = buildTypography(themeConfig.typography),
        content = content
    )
}
