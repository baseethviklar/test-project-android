package com.orbits.testproject.helper.helper_model
import android.os.Handler
import android.os.Looper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.orbits.testproject.main.view.MainActivity

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("BootCompletedReceiver", "Received action: ${intent.action}")

        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            "android.intent.action.QUICKBOOT_POWERON",
            "com.htc.intent.action.QUICKBOOT_POWERON" -> {
                Log.d("BootCompletedReceiver", "Device booted - starting app")

                // Add delay to ensure system is ready
                Handler(Looper.getMainLooper()).postDelayed({
                    try {
                        val launchIntent = Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            putExtra("AUTO_STARTED", true)
                        }
                        context.startActivity(launchIntent)
                        Log.d("BootCompletedReceiver", "App started successfully")
                    } catch (e: Exception) {
                        Log.e("BootCompletedReceiver", "Failed to start app", e)
                    }
                }, 3000) // 3 second delay
            }
        }
    }
}

