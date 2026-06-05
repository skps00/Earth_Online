package com.earthonline.app.data.weather

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "EarthquakeManager"
private const val EQ_RADIUS_KM = 200.0

@Singleton
class EarthquakeManager @Inject constructor() {

    data class EarthquakeInfo(
        val magnitude: Double,
        val place: String,
        val distanceKm: Double
    )

    suspend fun checkNearby(lat: Double, lon: Double): EarthquakeInfo? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/4.5_day.geojson")
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 10000
                conn.readTimeout = 10000

                val text = conn.inputStream.bufferedReader().use { it.readText() }
                conn.disconnect()

                val json = JSONObject(text)
                val features = json.optJSONArray("features") ?: return@withContext null

                for (i in 0 until features.length()) {
                    val feature = features.getJSONObject(i)
                    val properties = feature.optJSONObject("properties") ?: continue
                    val mag = properties.optDouble("mag", 0.0)
                    if (mag < 4.0) continue

                    val geometry = feature.optJSONObject("geometry") ?: continue
                    val coordinates = geometry.optJSONArray("coordinates") ?: continue
                    val eqLon = coordinates.optDouble(0, 0.0)
                    val eqLat = coordinates.optDouble(1, 0.0)
                    if (eqLat == 0.0 && eqLon == 0.0) continue

                    val distance = haversineKm(lat, lon, eqLat, eqLon)
                    if (distance <= EQ_RADIUS_KM) {
                        return@withContext EarthquakeInfo(
                            magnitude = mag,
                            place = properties.optString("place", "Unknown"),
                            distanceKm = Math.round(distance * 10.0) / 10.0
                        )
                    }
                }
                null
            } catch (e: Exception) {
                Log.e(TAG, "checkNearby failed", e)
                null
            }
        }
    }

    private fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }
}
