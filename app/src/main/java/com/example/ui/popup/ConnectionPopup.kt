package com.example.ui.popup

import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainActivity
import com.example.R

@Composable
fun ConnectionPopup(
    deviceName: String,
    leftBattery: Int?,
    rightBattery: Int?,
    caseBattery: Int?,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val popupSettings = remember { com.example.data.PopupSettings(context) }
    
    // Animation states
    var isVisible by remember { mutableStateOf(false) }
    
    val transition = updateTransition(targetState = isVisible, label = "popup_transition")
    
    val offsetY by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium)
            } else {
                tween(durationMillis = 250, easing = FastOutLinearInEasing)
            }
        },
        label = "offset_y"
    ) { visible ->
        if (visible) 0f else 300f // Slide from bottom (300px down) to 0
    }
    
    val scale by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium)
            } else {
                tween(durationMillis = 250, easing = FastOutLinearInEasing)
            }
        },
        label = "scale"
    ) { visible ->
        if (visible) 1f else 0.9f
    }
    
    val alpha by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                tween(durationMillis = 300)
            } else {
                tween(durationMillis = 200)
            }
        },
        label = "alpha"
    ) { visible ->
        if (visible) 1f else 0f
    }

    LaunchedEffect(Unit) {
        isVisible = true // Trigger enter animation
    }

    // Wrap the card in a Box to apply animations
    Box(
        modifier = Modifier
            .fillMaxSize() // Fills the WindowManager layout params
            .padding(bottom = 80.dp), // Match requirements
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .offset(y = offsetY.dp)
                .scale(scale)
                .background(Color.Transparent),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E).copy(alpha = alpha)) // "Apple dark" with opacity
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                
                // Close button top right
                IconButton(
                    onClick = {
                        isVisible = false
                        // The actual dismiss callback must happen AFTER animation finishes in caller, or we can delay it
                        // Better to delay here or pass an animated value out
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(32.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    // Quick hack to wait for animation before triggering dismiss callback
                    LaunchedEffect(isVisible) {
                        if (!isVisible) {
                            kotlinx.coroutines.delay(250)
                            onDismissRequest()
                        }
                    }
                }
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🎧", fontSize = 32.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = deviceName,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Connected",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (popupSettings.showBatteryDetails) {
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Battery stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            BatteryCard(
                                label = "LEFT",
                                batteryPercent = leftBattery,
                                iconResId = R.drawable.ic_earbuds_left,
                                modifier = Modifier.weight(1f)
                            )
                            BatteryCard(
                                label = "CASE",
                                batteryPercent = caseBattery,
                                iconResId = R.drawable.ic_earbuds_case,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            BatteryCard(
                                label = "RIGHT",
                                batteryPercent = rightBattery,
                                iconResId = R.drawable.ic_earbuds_right,
                                modifier = Modifier.fillMaxWidth(0.48f) // Approx half width
                            )
                        }
                        
                        if (leftBattery == null && rightBattery == null && caseBattery == null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "(Battery info unavailable)",
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Open App Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .clickable {
                                isVisible = false
                                // Wait for animation
                                // Open MainActivity
                                val intent = Intent(context, MainActivity::class.java).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                }
                                context.startActivity(intent)
                                
                                // Dismiss
                                onDismissRequest() // Better to dismiss immediately to remove overlay
                            }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Open BudControl Pro",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
