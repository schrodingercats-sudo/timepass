package com.example

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.example.audio.AudioService
import com.example.service.BudControlService
import com.example.ui.onboarding.PermissionOnboardingScreen
import com.example.ui.screens.MainAppScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
        }
        permissions.add(Manifest.permission.RECORD_AUDIO)
        
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 1)
        }
        
        try {
            startService(Intent(this, AudioService::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            startService(Intent(this, BudControlService::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setContent {
            MyApplicationTheme {
                var hasOverlayPermission by remember { mutableStateOf(Settings.canDrawOverlays(this)) }
                
                // Periodically check if permission was granted in settings
                LaunchedEffect(Unit) {
                    while (!hasOverlayPermission) {
                        hasOverlayPermission = Settings.canDrawOverlays(this@MainActivity)
                        delay(1000)
                    }
                }

                if (!hasOverlayPermission) {
                    PermissionOnboardingScreen {
                        hasOverlayPermission = true // Skip for now
                    }
                } else {
                    MainAppScreen()
                }
            }
        }
    }
}
