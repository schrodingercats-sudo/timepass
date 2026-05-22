package com.example.data

import android.content.Context
import android.content.SharedPreferences

class PopupSettings(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("popup_settings", Context.MODE_PRIVATE)

    var showConnectionPopup: Boolean
        get() = prefs.getBoolean("show_connection_popup", true)
        set(value) = prefs.edit().putBoolean("show_connection_popup", value).apply()

    var autoDismissSeconds: Int
        get() = prefs.getInt("auto_dismiss_seconds", 8)
        set(value) = prefs.edit().putInt("auto_dismiss_seconds", value).apply()

    var showOnLockScreen: Boolean
        get() = prefs.getBoolean("show_on_lock_screen", false)
        set(value) = prefs.edit().putBoolean("show_on_lock_screen", value).apply()

    var popupPositionBottom: Boolean
        get() = prefs.getBoolean("popup_position_bottom", true)
        set(value) = prefs.edit().putBoolean("popup_position_bottom", value).apply()

    var showBatteryDetails: Boolean
        get() = prefs.getBoolean("show_battery_details", true)
        set(value) = prefs.edit().putBoolean("show_battery_details", value).apply()
}
