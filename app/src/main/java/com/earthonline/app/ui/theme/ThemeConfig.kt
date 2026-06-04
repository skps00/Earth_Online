package com.earthonline.app.ui.theme

import androidx.compose.runtime.Immutable

enum class ThemeCategory { GAME, PROFESSIONAL, COLOR_PACK }

@Immutable
data class ThemeConfig(
    val id: String,
    val name: String,
    val description: String,
    val category: ThemeCategory,
    val isFree: Boolean = true,
    val isDark: Boolean,
    val colors: ColorTokens,
    val typography: TypographyTokens,
    val shapes: ShapeTokens
) {
    companion object {
        val rpgDark = ThemeConfig(
            id = "rpg_dark",
            name = "RPG Dark",
            description = "Deep blue dark theme with gold and emerald accents",
            category = ThemeCategory.GAME,
            isDark = true,
            colors = ColorTokens(
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
                onSurfaceVariant = TextSecondaryDark,
                destructive = DestructiveRed,
                rarityCommon = RarityCommon,
                rarityRare = RarityRare,
                rarityEpic = RarityEpic,
                rarityLegendary = RarityLegendary,
                achievementLocked = AchievementLocked,
                achievementUnlocked = AchievementUnlocked,
                shimmerBase = ShimmerBase,
                shimmerHighlight = ShimmerHighlight,
                dialogBackground = DialogDark,
                gradientTop = BackgroundGradientTop,
                gradientMid = BackgroundGradientMid,
                gradientBottom = BackgroundGradientBottom
            ),
            typography = TypographyTokens(),
            shapes = ShapeTokens()
        )

        val rpgLight = ThemeConfig(
            id = "rpg_light",
            name = "RPG Light",
            description = "Light modern theme with gold accents",
            category = ThemeCategory.GAME,
            isDark = false,
            colors = ColorTokens(
                primary = GoldLight,
                secondary = EmeraldGreen,
                tertiary = AccentOrange,
                background = BackgroundLight,
                surface = androidx.compose.ui.graphics.Color.White,
                surfaceVariant = SurfaceVariantLight,
                onPrimary = DeepBlue,
                onSecondary = DeepBlue,
                onTertiary = DeepBlue,
                onBackground = OnBackgroundLight,
                onSurface = OnBackgroundLight,
                onSurfaceVariant = OnSurfaceVariantLight,
                destructive = DestructiveRed,
                rarityCommon = RarityCommon,
                rarityRare = RarityRare,
                rarityEpic = RarityEpic,
                rarityLegendary = RarityLegendary,
                achievementLocked = AchievementLocked,
                achievementUnlocked = AchievementUnlocked,
                shimmerBase = ShimmerBase,
                shimmerHighlight = ShimmerHighlight,
                dialogBackground = DialogDark,
                gradientTop = BackgroundGradientTop,
                gradientMid = BackgroundGradientMid,
                gradientBottom = BackgroundGradientBottom
            ),
            typography = TypographyTokens(),
            shapes = ShapeTokens()
        )

        val allThemes = listOf(rpgDark, rpgLight)

        fun findById(id: String): ThemeConfig =
            allThemes.firstOrNull { it.id == id } ?: rpgDark

        val default = rpgDark
    }
}
