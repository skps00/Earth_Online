package com.earthonline.app.data.screentime

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScreenTimeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "ScreenTimeManager"
        private const val MINUTES_NIGHT_OWL = 30L
        private const val EARLY_HOUR = 5
        private const val NIGHT_OWL_START = 2
    }

    fun isUsageStatsPermissionGranted(): Boolean {
        return try {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as android.app.AppOpsManager
            val mode = appOps.checkOpNoThrow(
                android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
            mode == android.app.AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            Log.e(TAG, "isUsageStatsPermissionGranted failed", e)
            false
        }
    }

    fun openUsageAccessSettings() {
        try {
            context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        } catch (e: Exception) {
            Log.e(TAG, "openUsageAccessSettings failed", e)
        }
    }

    suspend fun evaluateAchievements(): List<String> {
        if (!isUsageStatsPermissionGranted()) return emptyList()
        return withContext(Dispatchers.IO) {
            val result = mutableListOf<String>()
            if (isEarlyBird()) result.add("daily_earlybird")
            if (isNightOwl()) result.add("daily_allnighter")
            if (hasNoPhoneToday()) result.add("daily_no_phone")
            result
        }
    }

    private fun getStartOfToday(): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    private fun getEarlyBirdThreshold(): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, EARLY_HOUR)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    private fun getNightOwlStart(): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, NIGHT_OWL_START)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    private fun isEarlyBird(): Boolean {
        return try {
            val todayStart = getStartOfToday()
            val now = System.currentTimeMillis()
            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val events = usageStatsManager.queryEvents(todayStart, now) ?: return false
            val event = UsageEvents.Event()

            var earliestKeyguardHidden: Long = Long.MAX_VALUE
            var earliestForeground: Long = Long.MAX_VALUE

            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                    && event.eventType == UsageEvents.Event.KEYGUARD_HIDDEN
                ) {
                    if (event.timeStamp < earliestKeyguardHidden) {
                        earliestKeyguardHidden = event.timeStamp
                    }
                }
                if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    if (event.timeStamp < earliestForeground) {
                        earliestForeground = event.timeStamp
                    }
                }
            }

            val earliest = if (earliestKeyguardHidden != Long.MAX_VALUE) earliestKeyguardHidden else earliestForeground
            val threshold = getEarlyBirdThreshold()
            earliest != Long.MAX_VALUE && earliest < threshold
        } catch (e: Exception) {
            Log.e(TAG, "isEarlyBird failed", e)
            false
        }
    }

    private fun isNightOwl(): Boolean {
        return try {
            val todayStart = getStartOfToday()
            val now = System.currentTimeMillis()
            val nightStart = getNightOwlStart()
            val threshold = getEarlyBirdThreshold()

            if (now < threshold) {
                false
            } else {
                val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                val events = usageStatsManager.queryEvents(todayStart, now) ?: return false
                val event = UsageEvents.Event()

                var totalMillis = 0L
                var lastForegroundTime: Long? = null

                while (events.hasNextEvent()) {
                    events.getNextEvent(event)
                    if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                        lastForegroundTime = event.timeStamp
                    }
                    if (event.eventType == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                        if (lastForegroundTime != null) {
                            val duration = event.timeStamp - lastForegroundTime
                            if (lastForegroundTime >= nightStart) {
                                totalMillis += duration
                            }
                            lastForegroundTime = null
                        }
                    }
                }

                if (lastForegroundTime != null && lastForegroundTime >= nightStart) {
                    totalMillis += now - lastForegroundTime
                }

                (totalMillis / 60_000) >= MINUTES_NIGHT_OWL
            }
        } catch (e: Exception) {
            Log.e(TAG, "isNightOwl failed", e)
            false
        }
    }

    private fun hasNoPhoneToday(): Boolean {
        return try {
            val oneDayAgo = System.currentTimeMillis() - 24 * 60 * 60 * 1000
            val now = System.currentTimeMillis()
            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val events = usageStatsManager.queryEvents(oneDayAgo, now) ?: return false
            val event = UsageEvents.Event()

            var hasActivity = false
            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    hasActivity = true
                    break
                }
            }
            !hasActivity
        } catch (e: Exception) {
            Log.e(TAG, "hasNoPhoneToday failed", e)
            false
        }
    }
}
