package com.earthonline.app.domain.model

import androidx.compose.ui.graphics.Color
import com.earthonline.app.ui.theme.RarityCommon
import com.earthonline.app.ui.theme.RarityEpic
import com.earthonline.app.ui.theme.RarityLegendary
import com.earthonline.app.ui.theme.RarityRare

enum class Rarity(val label: String, val color: Color, val minPoints: Int) {
    COMMON("普通", RarityCommon, 0),
    RARE("稀有", RarityRare, 50),
    EPIC("史詩", RarityEpic, 200),
    LEGENDARY("傳說", RarityLegendary, 1000);

    companion object {
        fun fromPoints(points: Int): Rarity {
            return entries.lastOrNull { points >= it.minPoints } ?: COMMON
        }
    }
}
