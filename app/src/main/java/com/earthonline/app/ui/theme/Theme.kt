package com.earthonline.app.ui.theme

// Material3 主題配置：深色/淺色雙方案色系選用、自訂字型層級、狀態欄顏色聯動
import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

// 深色主題色系：金色為主色、翡翠綠為次要、深海藍為背景
private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    secondary = EmeraldGreen,
    tertiary = AccentOrange,
    background = DeepBlue,
    surface = SurfaceDark,
    surfaceVariant = CardDark,
    onPrimary = DeepBlue,
    onSecondary = DeepBlue,
    onTertiary = DeepBlue,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark
)

// 客製字型系統：定義 Display 至 Label 各層級的字重、字級與字距
private val AppTypography = Typography(
    displayLarge = Typography().displayLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp),
    headlineLarge = Typography().headlineLarge.copy(fontWeight = FontWeight.Bold),
    headlineMedium = Typography().headlineMedium.copy(fontWeight = FontWeight.Bold),
    titleLarge = Typography().titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp),
    titleMedium = Typography().titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    bodyLarge = Typography().bodyLarge.copy(fontSize = 16.sp),
    bodyMedium = Typography().bodyMedium.copy(fontSize = 14.sp),
    bodySmall = Typography().bodySmall.copy(fontSize = 12.sp),
    labelLarge = Typography().labelLarge.copy(fontWeight = FontWeight.SemiBold),
    labelSmall = Typography().labelSmall.copy(fontSize = 10.sp)
)

// 淺色主題色系：深金為主色、淺灰藍為背景
private val LightColorScheme = lightColorScheme(
    primary = GoldLight,
    secondary = EmeraldGreen,
    tertiary = AccentOrange,
    background = BackgroundLight,
    surface = Color.White,
    surfaceVariant = SurfaceVariantLight,
    onPrimary = DeepBlue,
    onSecondary = DeepBlue,
    onTertiary = DeepBlue,
    onBackground = OnBackgroundLight,
    onSurface = OnBackgroundLight,
    onSurfaceVariant = OnSurfaceVariantLight
)

// 應用主題包裝器：根據 darkTheme 參數選用深/淺色系，設定狀態欄顏色與 MaterialTheme
@Composable
fun EarthOnlineTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity ?: return@SideEffect
            val window = activity.window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
