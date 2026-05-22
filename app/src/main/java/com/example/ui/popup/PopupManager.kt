package com.example.ui.popup

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner

import com.example.data.PopupSettings

class PopupManager(private val context: Context) : LifecycleOwner, SavedStateRegistryOwner {
    
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var composeView: ComposeView? = null
    private val popupSettings = PopupSettings(context)
    
    // Set up lifecycle and saved state for ComposeView in a standalone window
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry
        
    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    fun showPopup(deviceName: String, leftBattery: Int?, rightBattery: Int?, caseBattery: Int?) {
        // Prevent duplicate popups
        removePopup()
        
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        composeView = ComposeView(context).apply {
            setViewTreeLifecycleOwner(this@PopupManager)
            setViewTreeSavedStateRegistryOwner(this@PopupManager)
            setContent {
                ConnectionPopup(
                    deviceName = deviceName,
                    leftBattery = leftBattery,
                    rightBattery = rightBattery,
                    caseBattery = caseBattery,
                    onDismissRequest = { removePopup() }
                )
            }
        }

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = if (popupSettings.popupPositionBottom) (Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL) else (Gravity.TOP or Gravity.CENTER_HORIZONTAL)
        }

        try {
            windowManager.addView(composeView, params)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun removePopup() {
        composeView?.let {
            try {
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
                lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                windowManager.removeView(it)
            } catch (e: Exception) {
                // View might already be removed
            }
            composeView = null
        }
    }
}
