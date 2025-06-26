package com.orbits.testproject.helper.helper_model

import android.app.Application
import com.orbits.testproject.helper.CrashHandler

class AppController : Application() {
    companion object {
        lateinit var instance: AppController
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Setup crash handler
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(this))
    }
}


