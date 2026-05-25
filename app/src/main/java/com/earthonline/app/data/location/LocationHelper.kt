package com.earthonline.app.data.location

import android.content.Context
import android.location.Address
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
            if (!Geocoder.isPresent()) {
                return nominationFallback(latitude, longitude)
            }

            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 5)
                ?.filter { it.latitude != 0.0 || it.longitude != 0.0 }

            val addr = selectBestAddress(addresses)
            val display = buildAddressString(addr)
            val country = addr?.countryName ?: ""
            val continent = ContinentMapper.continentOf(country)

            Triple(display, country, continent)
        } catch (_: Exception) {
            Triple("", "", "")
        }
    }

    private fun selectBestAddress(addresses: List<Address>?): Address? {
        if (addresses.isNullOrEmpty()) return null

        return addresses.maxByOrNull { addr ->
            var score = 0
            if (!addr.thoroughfare.isNullOrBlank()) score += 10
            if (!addr.subThoroughfare.isNullOrBlank()) score += 5
            if (!addr.locality.isNullOrBlank()) score += 3
            if (!addr.subLocality.isNullOrBlank()) score += 2
            if (!addr.adminArea.isNullOrBlank()) score += 1
            if (!addr.getAddressLine(0).isNullOrBlank()) score += 8
            score
        }
    }

    @Suppress("DEPRECATION")
    private fun buildAddressString(addr: Address?): String {
        if (addr == null) return ""

        val line = addr.getAddressLine(0)
        if (!line.isNullOrBlank()) {
            return line
                .removeSuffix(", ${addr.countryName}")
                .removeSuffix(addr.countryName ?: "")
                .trim(',', ' ')
        }

        val parts = mutableListOf<String>()

        val street = buildString {
            addr.subThoroughfare?.let { append(it) }
            addr.thoroughfare?.let { append(" $it") }
        }.trim()
        if (street.isNotBlank()) parts.add(street)

        addr.subLocality?.let { if (it.isNotBlank()) parts.add(it) }
        addr.locality?.let { if (it.isNotBlank()) parts.add(it) }
        addr.adminArea?.let { if (it.isNotBlank()) parts.add(it) }

        return parts.joinToString(", ")
    }

    private fun nominationFallback(latitude: Double, longitude: Double): Triple<String, String, String> {
        return try {
            val url = java.net.URL("https://nominatim.openstreetmap.org/reverse?format=json&lat=$latitude&lon=$longitude&zoom=18&addressdetails=1")
            val conn = url.openConnection() as java.net.HttpURLConnection
            conn.setRequestProperty("User-Agent", "EarthOnlineApp/1.0")
            conn.connectTimeout = 5000
            conn.readTimeout = 5000

            val text = conn.inputStream.bufferedReader().readText()
            conn.disconnect()

            val json = org.json.JSONObject(text)
            val displayName = json.optString("display_name", "")
            val addressObj = json.optJSONObject("address")
            val country = addressObj?.optString("country", "") ?: ""
            val continent = ContinentMapper.continentOf(country)

            Triple(displayName, country, continent)
        } catch (_: Exception) {
            Triple("", "", "")
        }
    }
}
