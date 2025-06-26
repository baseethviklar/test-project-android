package com.orbits.testproject.helper

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Process
import android.util.Log
import kotlin.system.exitProcess

class CrashHandler(private val context: Context) : Thread.UncaughtExceptionHandler {

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        Log.e("CrashHandler", "App crashed", throwable)
        println("App crashed")

        // Schedule restart immediately (Handler might not work after crash)
        restartApplication()

        // Let the system handle the crash
        defaultHandler?.uncaughtException(thread, throwable)
    }

    private fun restartApplication() {
        // Get the launcher intent from package manager
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            putExtra("CRASH_RESTART", true)  // Optional: identify restarts
        } ?: return

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or
                    PendingIntent.FLAG_IMMUTABLE or
                    PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.RTC,
            System.currentTimeMillis() + 2000,  // Restart after 2 seconds
            pendingIntent
        )

        // Kill current process
        Process.killProcess(Process.myPid())
        exitProcess(1)
    }
}
