package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.data.PopupSettings
import com.example.ui.theme.ElectricBlue

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val popupSettings = remember { PopupSettings(context) }
    
    var showPopup by remember { mutableStateOf(popupSettings.showConnectionPopup) }
    var autoDismiss by remember { mutableStateOf(popupSettings.autoDismissSeconds) }
    var showLockScreen by remember { mutableStateOf(popupSettings.showOnLockScreen) }
    var popupPositionBottom by remember { mutableStateOf(popupSettings.popupPositionBottom) }
    var showBatteryDetails by remember { mutableStateOf(popupSettings.showBatteryDetails) }
    
    val canDrawOverlays = Settings.canDrawOverlays(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("SETTINGS", style = MaterialTheme.typography.labelMedium, color = ElectricBlue)
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Connection Popup", style = MaterialTheme.typography.titleMedium, color = Color.White)
                
                if (!canDrawOverlays) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Requires 'Display over other apps' permission to work.",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f)
                            )
                            val overlayIntent = remember {
                                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
                            }
                            val detailsIntent = remember {
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}"))
                            }
                            TextButton(onClick = {
                                try {
                                    context.startActivity(detailsIntent)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }) {
                                Text("Unlock")
                            }
                            TextButton(onClick = {
                                try {
                                    context.startActivity(overlayIntent)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }) {
                                Text("Grant")
                            }
                        }
                        
                        Text(
                            "If Android blocks this as a \"Restricted Setting\", go to device Settings → Apps → BudControl Pro → tap the 3 dots (top right) → \"Allow restricted settings\".",
                            color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show connection popup", color = Color.White)
                    Switch(
                        checked = showPopup,
                        onCheckedChange = { 
                            showPopup = it
                            popupSettings.showConnectionPopup = it
                        },
                        colors = SwitchDefaults.colors(checkedTrackColor = ElectricBlue)
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show on lock screen", color = Color.White)
                    Switch(
                        checked = showLockScreen,
                        onCheckedChange = { 
                            showLockScreen = it
                            popupSettings.showOnLockScreen = it
                        },
                        colors = SwitchDefaults.colors(checkedTrackColor = ElectricBlue)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Popup position bottom", color = Color.White)
                    Switch(
                        checked = popupPositionBottom,
                        onCheckedChange = { 
                            popupPositionBottom = it
                            popupSettings.popupPositionBottom = it
                        },
                        colors = SwitchDefaults.colors(checkedTrackColor = ElectricBlue)
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show battery details", color = Color.White)
                    Switch(
                        checked = showBatteryDetails,
                        onCheckedChange = { 
                            showBatteryDetails = it
                            popupSettings.showBatteryDetails = it
                        },
                        colors = SwitchDefaults.colors(checkedTrackColor = ElectricBlue)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("Auto-dismiss duration (seconds)", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
                Slider(
                    value = autoDismiss.toFloat(),
                    onValueChange = { 
                        autoDismiss = it.toInt()
                        popupSettings.autoDismissSeconds = it.toInt()
                    },
                    valueRange = 0f..15f,
                    steps = 14,
                    colors = SliderDefaults.colors(activeTrackColor = ElectricBlue)
                )
                Text(if (autoDismiss == 0) "Never" else "$autoDismiss seconds", color = Color.White)
            }
        }
    }
}
