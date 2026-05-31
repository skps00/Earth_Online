package com.earthonline.app.domain.model

// 成就觸發器映射 — 定義特定國家／洲的成就 ID 對應及數量型成就清單

// 維護國家→成就ID、洲→成就ID、以及數量門檻成就集合
object AchievementTriggers {
    val countryTriggers = mapOf(
        "Japan" to "explore_japan",
        "Australia" to "explore_australia"
    )

    val continentTriggers = mapOf(
        "Asia" to "explore_asia",
        "Europe" to "explore_europe",
        "Africa" to "explore_africa",
        "North America" to "explore_north_america",
        "South America" to "explore_south_america",
        "Oceania" to "explore_oceania",
        "Antarctica" to "explore_antarctica"
    )

    val countryCountAchievements = listOf("explore_5countries", "explore_10countries", "explore_50countries")
    val continentCountAchievements = listOf("explore_3continents", "explore_7continents")
}
