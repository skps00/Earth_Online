package com.earthonline.app.data.activity

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionEvent
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityRecognitionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PREFS = "activity_stats"
        private const val KEY_WALK_MIN = "total_walk_minutes"
        private const val KEY_RUN_MIN = "total_run_minutes"
        private const val KEY_BIKE_MIN = "total_bike_minutes"
        private const val KEY_DRIVE_MIN = "total_drive_minutes"
        private const val KEY_BIKE_KM = "total_bike_km"
        private const val KEY_DRIVE_KM = "total_drive_km"
        private const val REQUEST_CODE = 1001
    }

    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private var lastActivityTime = 0L
    private var currentActivity: Int = DetectedActivity.UNKNOWN

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return
            if (ActivityTransitionResult.hasResult(intent)) {
                val result = ActivityTransitionResult.extractResult(intent) ?: return
                for (event in result.transitionEvents) {
                    handleTransition(event)
                }
            }
        }
    }

    init {
        context.registerReceiver(
            receiver,
            IntentFilter("com.earthonline.app.ACTIVITY_TRANSITION"),
            Context.RECEIVER_NOT_EXPORTED
        )
    }

    fun startTracking() {
        val transitions = listOf(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build(),
        )

        val request = ActivityTransitionRequest(transitions)
        val pendingIntent = PendingIntent.getBroadcast(
            context, REQUEST_CODE,
            Intent("com.earthonline.app.ACTIVITY_TRANSITION"),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            ActivityRecognition.getClient(context)
                .requestActivityTransitionUpdates(request, pendingIntent)
        } catch (_: Exception) { }
    }

    private fun handleTransition(event: ActivityTransitionEvent) {
        val now = System.currentTimeMillis()
        val durationMinutes = if (lastActivityTime > 0 && event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
            ((now - lastActivityTime) / 60000).toInt().coerceAtMost(60)
        } else {
            1
        }

        if (event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
            currentActivity = event.activityType
            lastActivityTime = now
        } else {
            currentActivity = DetectedActivity.UNKNOWN
            val speedKmh = when (event.activityType) {
                DetectedActivity.ON_BICYCLE -> 15.0
                DetectedActivity.IN_VEHICLE -> 40.0
                else -> 0.0
            }
            val distanceKm = if (speedKmh > 0) (speedKmh * durationMinutes / 60.0) else 0.0

            when (event.activityType) {
                DetectedActivity.WALKING -> incrementStat(KEY_WALK_MIN, durationMinutes)
                DetectedActivity.RUNNING -> incrementStat(KEY_RUN_MIN, durationMinutes)
                DetectedActivity.ON_BICYCLE -> {
                    incrementStat(KEY_BIKE_MIN, durationMinutes)
                    incrementStat(KEY_BIKE_KM, distanceKm.toInt())
                }
                DetectedActivity.IN_VEHICLE -> {
                    incrementStat(KEY_DRIVE_MIN, durationMinutes)
                    incrementStat(KEY_DRIVE_KM, distanceKm.toInt())
                }
            }
        }
    }

    private fun incrementStat(key: String, amount: Int) {
        prefs.edit().putLong(key, prefs.getLong(key, 0) + amount).apply()
    }

    fun getWalkingMinutes(): Int = prefs.getLong(KEY_WALK_MIN, 0).toInt()
    fun getRunningMinutes(): Int = prefs.getLong(KEY_RUN_MIN, 0).toInt()
    fun getBikingMinutes(): Int = prefs.getLong(KEY_BIKE_MIN, 0).toInt()
    fun getDrivingMinutes(): Int = prefs.getLong(KEY_DRIVE_MIN, 0).toInt()
    fun getBikingKm(): Int = prefs.getLong(KEY_BIKE_KM, 0).toInt()
    fun getDrivingKm(): Int = prefs.getLong(KEY_DRIVE_KM, 0).toInt()

    /** For testing: simulate activity data */
    fun injectTestData() {
        prefs.edit()
            .putLong(KEY_WALK_MIN, 15)
            .putLong(KEY_BIKE_MIN, 60)
            .putLong(KEY_BIKE_KM, 120)
            .putLong(KEY_DRIVE_MIN, 30)
            .apply()
    }
}
