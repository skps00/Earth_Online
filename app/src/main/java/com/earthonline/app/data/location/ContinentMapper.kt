package com.earthonline.app.data.location

// 洲別映射器 — 將國家名稱映射到所屬洲別字串（支援中英文）

// 以 HashMap 維護國家到洲別的對應關係（涵蓋中英文國家名稱）
object ContinentMapper {
    private val map = mapOf(
        "China" to "Asia", "中國" to "Asia",
        "Japan" to "Asia", "日本" to "Asia",
        "Korea" to "Asia", "韓國" to "Asia", "南韓" to "Asia",
        "India" to "Asia", "印度" to "Asia",
        "Thailand" to "Asia", "泰國" to "Asia",
        "Vietnam" to "Asia", "越南" to "Asia",
        "Malaysia" to "Asia", "馬來西亞" to "Asia",
        "Singapore" to "Asia", "新加坡" to "Asia",
        "Indonesia" to "Asia", "印尼" to "Asia", "印度尼西亞" to "Asia",
        "Philippines" to "Asia", "菲律賓" to "Asia",
        "Taiwan" to "Asia", "台灣" to "Asia", "臺灣" to "Asia",
        "Hong Kong" to "Asia", "香港" to "Asia",
        "France" to "Europe", "法國" to "Europe",
        "Germany" to "Europe", "德國" to "Europe",
        "Italy" to "Europe", "義大利" to "Europe", "意大利" to "Europe",
        "Spain" to "Europe", "西班牙" to "Europe",
        "United Kingdom" to "Europe", "英國" to "Europe",
        "Netherlands" to "Europe", "荷蘭" to "Europe",
        "Switzerland" to "Europe", "瑞士" to "Europe",
        "United States" to "North America", "美國" to "North America",
        "Canada" to "North America", "加拿大" to "North America",
        "Mexico" to "North America", "墨西哥" to "North America",
        "Brazil" to "South America", "巴西" to "South America",
        "Argentina" to "South America", "阿根廷" to "South America",
        "Chile" to "South America", "智利" to "South America",
        "Australia" to "Oceania", "澳洲" to "Oceania", "澳大利亞" to "Oceania",
        "New Zealand" to "Oceania", "紐西蘭" to "Oceania", "新西蘭" to "Oceania",
        "Egypt" to "Africa", "埃及" to "Africa",
        "South Africa" to "Africa", "南非" to "Africa",
        "Kenya" to "Africa", "肯亞" to "Africa", "肯尼亞" to "Africa",
        "Nigeria" to "Africa", "奈及利亞" to "Africa", "尼日利亞" to "Africa",
        "Morocco" to "Africa", "摩洛哥" to "Africa",
    )

    // 根據國家名稱回傳所屬洲別 — 未收錄國家回傳空字串
    fun continentOf(country: String): String = map[country] ?: ""
}
