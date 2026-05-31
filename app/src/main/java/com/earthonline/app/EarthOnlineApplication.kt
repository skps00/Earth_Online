package com.earthonline.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// 應用程式 Application 類別，標記為 Hilt 依賴注入入口
@HiltAndroidApp
class EarthOnlineApplication : Application()
