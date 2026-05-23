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
            val display = addr?.getAddressLine(0)?.removeSuffix(", ${addr.countryName}")?.removeSuffix(addr.countryName ?: "")?.trim { it == ',' || it == ' ' } ?: ""
            val country = addr?.countryName ?: ""
            val continent = ContinentMapper.continentOf(country)
            Triple(display, country, continent)
        } catch (_: Exception) {
            Triple("", "", "")
        }
    }
}
