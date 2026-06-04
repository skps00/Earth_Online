package com.earthonline.app.data.activity

// 活動識別管理器 — 使用 Google Activity Transition API 自動偵測並記錄活動時長與距離

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.earthonline.app.AppConstants
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionEvent
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

// 活動識別管理器 — 使用 SharedPreferences 持久化累計活動統計
@Singleton
class ActivityRecognitionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // 活動統計儲存鍵值定義
    companion object {
        private const val REQUEST_CODE = 1001
        private const val TAG = "ActivityRecognitionManager"
    }

    private val prefs = context.getSharedPreferences(AppConstants.ACTIVITY_STATS_PREFS_NAME, Context.MODE_PRIVATE)
    private var lastActivityTime = 0L
    private var currentActivity: Int = DetectedActivity.UNKNOWN

    // 接收活動轉換廣播 — 由 Activity Transition API 觸發
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return
            if (ActivityTransitionResult.hasResult(intent)) {
                val result = ActivityTransitionResult.extractResult(intent) ?: return
                for (event in result .transitionEvents) {
                    handleTransition(event)
                }
            }
        }
    }

    // 初始化時註冊廣播接收器 — RECEIVER_NOT_EXPORTED 防止外部應用發送
    init {
        context.registerReceiver(
            receiver,
            IntentFilter("com.earthonline.app.ACTIVITY_TRANSITION"),
            Context.RECEIVER_NOT_EXPORTED
        )
    }

    // 啟動活動追蹤 — 註冊走路／跑步／騎行／駕駛的進入與離開轉換監聽
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
        } catch (e: Exception) {
            Log.e(TAG, "Failed to request activity transition updates", e)
        }
    }

    // 處理活動轉換事件 — 進入時記錄開始時間，離開時累計時長與預估距離
    private fun handleTransition(event: ActivityTransitionEvent) {
        val now = System.currentTimeMillis()
        val durationMinutes = if (lastActivityTime > 0 && event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
            ((now - lastActivityTime) / 60000).toInt().coerceIn(0, 60)
        } else {
            1
        }

        if (event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
            currentActivity = event.activityType
            lastActivityTime = now
        } else {
            currentActivity = DetectedActivity.UNKNOWN
            // 使用平均速度估算距離（km），騎行 15km/h，駕駛 40km/h
            val speedKmh = when (event.activityType) {
                DetectedActivity.ON_BICYCLE -> 15.0
                DetectedActivity.IN_VEHICLE -> 40.0
                else -> 0.0
            }
            val distanceKm = if (speedKmh > 0) (speedKmh * durationMinutes / 60.0) else 0.0

            when (event.activityType) {
                DetectedActivity.WALKING -> incrementStat(AppConstants.KEY_WALK_MIN, durationMinutes)
                DetectedActivity.RUNNING -> incrementStat(AppConstants.KEY_RUN_MIN, durationMinutes)
                DetectedActivity.ON_BICYCLE -> {
                    incrementStat(AppConstants.KEY_BIKE_MIN, durationMinutes)
                    incrementStat(AppConstants.KEY_BIKE_KM, distanceKm.toInt())
                }
                DetectedActivity.IN_VEHICLE -> {
                    incrementStat(AppConstants.KEY_DRIVE_MIN, durationMinutes)
                    incrementStat(AppConstants.KEY_DRIVE_KM, distanceKm.toInt())
                }
            }
        }
    }

    // 累加指定統計項目的數值
    private fun incrementStat(key: String, amount: Int) {
        prefs.edit().putLong(key, prefs.getLong(key, 0) + amount).apply()
    }

    // 以下為各活動統計的 getter — 從 SharedPreferences 讀取累計數值
    fun getWalkingMinutes(): Int = prefs.getLong(AppConstants.KEY_WALK_MIN, 0).toInt()
    fun getRunningMinutes(): Int = prefs.getLong(AppConstants.KEY_RUN_MIN, 0).toInt()
    fun getBikingMinutes(): Int = prefs.getLong(AppConstants.KEY_BIKE_MIN, 0).toInt()
    fun getDrivingMinutes(): Int = prefs.getLong(AppConstants.KEY_DRIVE_MIN, 0).toInt()
    fun getBikingKm(): Int = prefs.getLong(AppConstants.KEY_BIKE_KM, 0).toInt()
    fun getDrivingKm(): Int = prefs.getLong(AppConstants.KEY_DRIVE_KM, 0).toInt()
}
