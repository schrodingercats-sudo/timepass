package com.example.ui.popup

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BatteryCard(
    label: String,
    batteryPercent: Int?,
    iconResId: Int,
    modifier: Modifier = Modifier
) {
    // Battery colors
    val (batteryColor, isLowBattery) = when {
        batteryPercent == null -> Color.Gray to false
        batteryPercent >= 60 -> Color(0xFF34C759) to false // Green
        batteryPercent >= 20 -> Color(0xFFFF9F0A) to false // Orange/Yellow
        else -> Color(0xFFFF3B30) to true // Red
    }

    // Battery bar animation
    var barProgress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = barProgress,
        animationSpec = tween(durationMillis = 600, easing = LinearOutSlowInEasing),
        label = "battery_bar"
    )

    LaunchedEffect(batteryPercent) {
        barProgress = (batteryPercent ?: 0) / 100f
    }

    // Pulse animation for low battery
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by if (isLowBattery) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 0.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_alpha"
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    Box(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = if (isLowBattery) Color.Red.copy(alpha = pulseAlpha) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (batteryPercent != null) "$batteryPercent%" else "-- %",
                color = if (batteryPercent != null) Color.White else Color.Gray,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            
            if (batteryPercent != null) {
                Spacer(modifier = Modifier.height(6.dp))
                
                // Battery bar background
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                ) {
                    // Battery fill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .background(batteryColor)
                    )
                }
            }
        }
    }
}
