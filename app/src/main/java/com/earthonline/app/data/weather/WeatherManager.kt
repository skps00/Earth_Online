package com.earthonline.app.data.weather

import android.util.Log
import com.earthonline.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "WeatherManager"
private const val HEAT_THRESHOLD = 35.0

sealed class WeatherCondition {
    data object None : WeatherCondition()
    data object Rain : WeatherCondition()
    data object Storm : WeatherCondition()
    data object Lightning : WeatherCondition()
    data object ExtremeHeat : WeatherCondition()
}

@Singleton
class WeatherManager @Inject constructor() {

    suspend fun checkConditions(lat: Double, lon: Double): List<WeatherCondition> {
        val key = BuildConfig.OPENWEATHERMAP_API_KEY
        if (key.isEmpty()) return emptyList()

        return withContext(Dispatchers.IO) {
            try {
                val result = mutableListOf<WeatherCondition>()
                val urlString = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&units=metric&appid=$key"
                val conn = URL(urlString).openConnection() as HttpURLConnection
                conn.connectTimeout = 10000
                conn.readTimeout = 10000

                val text = conn.inputStream.bufferedReader().use { it.readText() }
                conn.disconnect()

                val json = JSONObject(text)

                val weatherArray = json.optJSONArray("weather") ?: return@withContext emptyList()
                var hasRain = false
                var hasStorm = false
                var hasLightning = false

                for (i in 0 until weatherArray.length()) {
                    val w = weatherArray.getJSONObject(i)
                    val id = w.optInt("id", 0)
                    when (id) {
                        in 200..232 -> hasLightning = true
                        in 300..531 -> hasRain = true
                        in 771..781 -> hasStorm = true
                        960, 961, 962 -> hasStorm = true
                    }
                }

                if (hasRain) result.add(WeatherCondition.Rain)
                if (hasStorm) result.add(WeatherCondition.Storm)
                if (hasLightning) result.add(WeatherCondition.Lightning)

                val mainObj = json.optJSONObject("main")
                val temp = mainObj?.optDouble("temp_max") ?: 0.0
                if (temp > HEAT_THRESHOLD) result.add(WeatherCondition.ExtremeHeat)

                result
            } catch (e: Exception) {
                Log.e(TAG, "checkConditions failed", e)
                emptyList()
            }
        }
    }
}
