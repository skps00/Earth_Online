package com.earthonline.app.data.location

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getLastLocation(): Location? {
        val lm = context.getSystemService<LocationManager>() ?: return null
        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        for (provider in providers) {
            try {
                val loc = lm.getLastKnownLocation(provider)
                if (loc != null) return loc
            } catch (_: SecurityException) { }
        }
        return null
    }

    @Suppress("DEPRECATION")
    fun reverseGeocode(latitude: Double, longitude: Double): Triple<String, String, String> {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            val addr = addresses?.firstOrNull()
            val display = listOfNotNull(addr?.adminArea, addr?.locality, addr?.subLocality, addr?.thoroughfare)
                .filter { it.isNotBlank() }.joinToString(", ")
                .ifEmpty { "%.5f, %.5f".format(latitude, longitude) }
            val country = addr?.countryName ?: ""
            val continent = countryToContinent(country)
            Triple(display, country, continent)
        } catch (_: Exception) {
            Triple("%.5f, %.5f".format(latitude, longitude), "", "")
        }
    }

    private fun countryToContinent(country: String): String {
        val map = mapOf(
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
        return map[country] ?: ""
    }
}
