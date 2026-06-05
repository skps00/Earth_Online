package com.earthonline.app.data.weather

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.earthonline.app.AppConstants
import com.earthonline.app.data.location.LocationHelper
import com.earthonline.app.data.repository.AchievementRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

private const val TAG = "WeatherWorker"

@HiltWorker
class WeatherWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val locationHelper: LocationHelper,
    private val weatherManager: WeatherManager,
    private val earthquakeManager: EarthquakeManager,
    private val achievementRepository: AchievementRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val location = locationHelper.getLastLocation() ?: return Result.retry()
            val lat = location.latitude
            val lon = location.longitude

            val weatherConditions = weatherManager.checkConditions(lat, lon)
            val earthquake = earthquakeManager.checkNearby(lat, lon)

            val achievementIds = mutableListOf<String>()

            if (weatherConditions.any { it is WeatherCondition.Storm }) {
                achievementIds.add("weather_storm")
            }
            if (weatherConditions.any { it is WeatherCondition.Rain }) {
                achievementIds.add("weather_rain")
            }
            if (weatherConditions.any { it is WeatherCondition.Lightning }) {
                achievementIds.add("weather_lightning")
            }
            if (weatherConditions.any { it is WeatherCondition.ExtremeHeat }) {
                achievementIds.add("weather_extreme_heat")
            }
            if (earthquake != null) {
                achievementIds.add("epic_earthquake")
            }

            if (achievementIds.isNotEmpty()) {
                achievementRepository.unlockByIds(achievementIds)
            }

            Log.d(TAG, "Checked conditions: unocked=${achievementIds.joinToString()}")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "doWork failed", e)
            Result.retry()
        }
    }
}
