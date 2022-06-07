package com.bikcodeh.distancetrackerapp

import android.app.Application
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes

class DistanceTrackerApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        setupAppCenter()
    }

    private fun setupAppCenter() {
        AppCenter.start(
            this,
            BuildConfig.APP_CENTER_KEY,
            Analytics::class.java,
            Crashes::class.java
        )
    }
}