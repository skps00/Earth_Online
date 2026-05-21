package com.earthonline.app.data.location

object ContinentMapper {
    private val map = mapOf(
        "China" to "Asia", "Japan" to "Asia", "Korea" to "Asia", "India" to "Asia",
        "Thailand" to "Asia", "Vietnam" to "Asia", "Malaysia" to "Asia", "Singapore" to "Asia",
        "Indonesia" to "Asia", "Philippines" to "Asia", "Taiwan" to "Asia", "Hong Kong" to "Asia",
        "France" to "Europe", "Germany" to "Europe", "Italy" to "Europe", "Spain" to "Europe",
        "United Kingdom" to "Europe", "Netherlands" to "Europe", "Switzerland" to "Europe",
        "United States" to "North America", "Canada" to "North America", "Mexico" to "North America",
        "Brazil" to "South America", "Argentina" to "South America", "Chile" to "South America",
        "Australia" to "Oceania", "New Zealand" to "Oceania",
        "Egypt" to "Africa", "South Africa" to "Africa", "Kenya" to "Africa", "Nigeria" to "Africa",
        "Morocco" to "Africa"
    )

    fun continentOf(country: String): String = map[country] ?: ""
}
