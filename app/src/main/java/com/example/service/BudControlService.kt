package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.R
import com.example.data.PopupSettings
import com.example.ui.popup.PopupManager
import kotlinx.coroutines.*
import android.provider.Settings

class BudControlService : Service() {

    private val receiver = BluetoothReceiver()
    private var popupManager: PopupManager? = null
    private var dismissJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var popupSettings: PopupSettings

    override fun onCreate() {
        super.onCreate()
        
        popupManager = PopupManager(this)
        popupSettings = PopupSettings(this)

        createNotificationChannel()
        val notification = createNotification()
        
        // Start Foreground
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // we should be careful about foregroundServiceType in Android 14+
            // the manifest says connectedDevice.
            try {
                startForeground(2, notification)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            startForeground(2, notification)
        }

        // Register Receiver
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            addAction("android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED")
            addAction("android.bluetooth.headset.action.BATTERY_LEVEL_CHANGED")
        }
        
        receiver.onDeviceConnected = { device, extBattery -> 
            handleDeviceConnected(device, extBattery)
        }
        
        receiver.onDeviceDisconnected = { device ->
            // Maybe dismiss if the current popup is for this device?
            // Simple approach: just dismiss
            dismissPopup()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(receiver, filter)
        }
    }

    private fun handleDeviceConnected(device: BluetoothDevice, extBattery: Int?) {
        if (!popupSettings.showConnectionPopup) return
        
        // Check if we have overlay permission
        if (!Settings.canDrawOverlays(this)) {
            Log.d("BudControlService", "No overlay permission")
            return
        }
        
        // Check screen status
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val isScreenOn = powerManager.isInteractive
        if (!isScreenOn && !popupSettings.showOnLockScreen) {
            return
        }

        // Attempt to read battery from Android hidden API (Method 1)
        var retrievedBattery: Int? = extBattery
        try {
            val method = device.javaClass.getMethod("getBatteryLevel")
            val batteryLevel = method.invoke(device) as Int
            if (batteryLevel in 0..100) {
                retrievedBattery = batteryLevel
            }
        } catch (e: Exception) {
            Log.d("BudControlService", "Could not read battery: ${e.message}")
        }

        // For non-apple devices, L/R/Case individual batteries are very hard to get.
        // We will just set all of them to the retrieved battery, or null
        val leftBat = retrievedBattery
        val rightBat = retrievedBattery
        val caseBat = retrievedBattery

        // Get Name
        var name = "Unknown Device"
        try {
            val permCheck = checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
            if (permCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                name = device.name ?: "Unknown Device"
            }
        } catch (e: Exception) {
            // permission issue
        }

        // Display
        scope.launch {
            popupManager?.showPopup(name, leftBat, rightBat, caseBat)
            
            // Auto dismiss
            dismissJob?.cancel()
            val duration = popupSettings.autoDismissSeconds
            if (duration > 0) {
                dismissJob = launch {
                    delay(duration * 1000L)
                    dismissPopup()
                }
            }
        }
    }

    private fun dismissPopup() {
        scope.launch {
            popupManager?.removePopup()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        popupManager?.removePopup()
        scope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "bud_control_service",
                "Connection Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "bud_control_service")
            .setContentTitle("BudControl Pro")
            .setContentText("Listening for headphone connections...")
            .setSmallIcon(R.drawable.ic_earbuds_case) // Use new icon
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
