package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.PopupSettings

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val settings = PopupSettings(context)
            if (settings.showConnectionPopup) {
                // Determine if we want to restart it. 
                // Wait, starting a foreground service from background on boot requires special considerations 
                // in newer Android versions, but normally allowed.
                try {
                    val serviceIntent = Intent(context, BudControlService::class.java)
                    context.startForegroundService(serviceIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
