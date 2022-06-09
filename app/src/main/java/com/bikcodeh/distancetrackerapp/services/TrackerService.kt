package com.bikcodeh.distancetrackerapp.services

import android.content.Intent
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.bikcodeh.distancetrackerapp.util.Constants.ACTION_SERVICE_START
import com.bikcodeh.distancetrackerapp.util.Constants.ACTION_SERVICE_STOP

class TrackerService: LifecycleService() {

    companion object {
        val started = MutableLiveData<Boolean>()
    }

    private fun setInitialValues() {
        started.postValue(false)
    }

    override fun onCreate() {
        setInitialValues()
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_SERVICE_START -> {
                    started.postValue(true)
                }
                ACTION_SERVICE_STOP -> {
                    started.postValue(false)
                } else -> {}
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}