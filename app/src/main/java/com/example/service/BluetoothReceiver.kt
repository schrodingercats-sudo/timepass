package com.example.service

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BluetoothReceiver : BroadcastReceiver() {
    
    // Callback to the service to handle popup creation
    var onDeviceConnected: ((BluetoothDevice, Int?) -> Unit)? = null
    var onDeviceDisconnected: ((BluetoothDevice) -> Unit)? = null

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        
        Log.d("BluetoothReceiver", "Action: $action, Device: ${device?.name}")

        when (action) {
            BluetoothDevice.ACTION_ACL_CONNECTED,
            "android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED" -> {
                if (device != null) {
                    var battery: Int? = null
                    // Try to get battery from an intent extra if available (sometimes headset actions send it)
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
                // If we get battery events
                val bat = intent.getIntExtra("android.bluetooth.headset.extra.BATTERY_LEVEL", -1)
                Log.d("BluetoothReceiver", "Battery for ${device?.name}: $bat")
            }
        }
    }
}
