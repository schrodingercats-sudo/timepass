package com.example.service

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class BluetoothReceiver : BroadcastReceiver() {
    
    // Callback to the service to handle popup creation
    var onDeviceConnected: ((BluetoothDevice, Int?) -> Unit)? = null
    var onDeviceDisconnected: ((BluetoothDevice) -> Unit)? = null

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        
        // Safely extract parcelable using SDK check to prevent crashes on Android 13 to 16
        val device: BluetoothDevice? = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            }
        } catch (e: Exception) {
            try {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            } catch (ex: Exception) {
                null
            }
        }
        
        Log.d("BluetoothReceiver", "Action: $action, Device: ${device?.name}")

        when (action) {
            BluetoothDevice.ACTION_ACL_CONNECTED -> {
                if (device != null) {
                    var battery: Int? = null
                    // Try to get battery from headset broadcast extra
                    battery = intent.getIntExtra("android.bluetooth.headset.extra.BATTERY_LEVEL", -1)
                    if (battery == -1) battery = null
                    
                    onDeviceConnected?.invoke(device, battery)
                }
            }
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                if (device != null) {
                    onDeviceDisconnected?.invoke(device)
                }
            }
            "android.bluetooth.headset.action.BATTERY_LEVEL_CHANGED" -> {
                val bat = intent.getIntExtra("android.bluetooth.headset.extra.BATTERY_LEVEL", -1)
                Log.d("BluetoothReceiver", "Battery for ${device?.name}: $bat")
            }
        }
    }
}
