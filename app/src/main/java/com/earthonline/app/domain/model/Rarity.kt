package com.earthonline.app.domain.model

// 稀有度枚舉 — 定義成就的四種稀有度等級及對應顏色、最低分數門檻
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.earthonline.app.R
import com.earthonline.app.ui.theme.RarityCommon
import com.earthonline.app.ui.theme.RarityEpic
import com.earthonline.app.ui.theme.RarityLegendary
import com.earthonline.app.ui.theme.RarityRare

// 普通 → 稀有 → 史詩 → 傳說，獎勵分數門檻遞增
enum class Rarity(@StringRes val labelResId: Int, val color: Color, val minPoints: Int) {
    COMMON(R.string.rarity_common, RarityCommon, 0),
    RARE(R.string.rarity_rare, RarityRare, 50),
    EPIC(R.string.rarity_epic, RarityEpic, 200),
    LEGENDARY(R.string.rarity_legendary, RarityLegendary, 1000);

    companion object {
        // 根據成就獎勵分數決定稀有度等級 — 取最後一個門檻達標的等級
        fun fromPoints(points: Int): Rarity {
            return entries.lastOrNull { points >= it.minPoints } ?: COMMON
        }
    }
}
