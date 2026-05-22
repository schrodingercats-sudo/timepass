package com.example.ui.onboarding

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ElectricBlue

@Composable
fun PermissionOnboardingScreen(onPermissionGrantedOrSkipped: () -> Unit) {
    val context = LocalContext.current
    
    // Check if permission is magically granted in background (not really needed since we do this on return)
    DisposableEffect(Unit) {
        onDispose { }
    }

    Scaffold(
        containerColor = Color(0xFF0A0A0A)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // Nice big fake card indicating what we'll show
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(200.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E))
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("🎧", style = MaterialTheme.typography.displayLarge)
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "Enable Connection Popup",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "See your earbuds battery and info every time they connect, just like AirPods.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Note: If Android says \"Restricted Setting\", go to device Settings → Apps → BudControl Pro → tap the 3 dots (top right) → \"Allow restricted settings\", then try again.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}")
                    )
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Enable Permission", color = Color.Black, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(
                onClick = onPermissionGrantedOrSkipped
            ) {
                Text("Skip for now", color = Color.White.copy(alpha = 0.5f))
            }
        }
    }
}
