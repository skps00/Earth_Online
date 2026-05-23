package com.earthonline.app.data.location

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
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

    suspend fun reverseGeocode(latitude: Double, longitude: Double): Triple<String, String, String> = withContext(Dispatchers.IO) {
        try {
            val url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=$latitude&lon=$longitude&accept-language=zh"
            val json = URL(url).readText()
            val obj = JSONObject(json)
            val address = obj.optJSONObject("address") ?: JSONObject()
            val display = listOfNotNull(
                address.optString("road", ""),
                address.optString("suburb", ""),
                address.optString("city", address.optString("town", address.optString("village", "")))
            ).filter { it.isNotBlank() }.joinToString(", ").ifEmpty { "" }
            val country = address.optString("country", "")
            val continent = ContinentMapper.continentOf(country)
            Triple(display, country, continent)
        } catch (_: Exception) {
            Triple("", "", "")
        }
    }
}
