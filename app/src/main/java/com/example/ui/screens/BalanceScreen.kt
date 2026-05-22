package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.BudViewModel
import kotlin.math.roundToInt
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ActiveRight
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.BorderSurface

@Composable
fun BalanceScreen(viewModel: BudViewModel) {
    val profile by viewModel.currentProfile.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            VolumeSliderColumn(
                label = "LEFT",
                value = profile.leftVolume,
                activeColor = ElectricBlue,
                onValueChange = { newVal ->
                    if (profile.linked) {
                        viewModel.setVolume(newVal, newVal, true, profile.balance)
                    } else {
                        viewModel.setVolume(newVal, profile.rightVolume, false, profile.balance)
                    }
                }
            )

            // Link/Unlink middle section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Box(modifier = Modifier.width(1.dp).height(48.dp).background(BorderSurface))
                IconButton(
                    onClick = {
                        val isLinked = !profile.linked
                        viewModel.setVolume(profile.leftVolume, profile.leftVolume, isLinked, profile.balance)
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF1A1A1A), RoundedCornerShape(24.dp))
                        .border(1.dp, BorderSurface, RoundedCornerShape(24.dp))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(modifier = Modifier.width(16.dp).height(2.dp).background(if (profile.linked) ElectricBlue else com.example.ui.theme.TextMuted))
                        Box(modifier = Modifier.width(16.dp).height(2.dp).background(if (profile.linked) ElectricBlue else com.example.ui.theme.TextMuted))
                    }
                }
                Text(
                    text = if (profile.linked) "LINKED" else "UNLINKED",
                    style = MaterialTheme.typography.labelSmall,
                    color = com.example.ui.theme.TextMuted,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Box(modifier = Modifier.width(1.dp).height(48.dp).background(BorderSurface))
            }

            VolumeSliderColumn(
                label = "RIGHT",
                value = profile.rightVolume,
                activeColor = ActiveRight,
                onValueChange = { newVal ->
                    if (profile.linked) {
                        viewModel.setVolume(newVal, newVal, true, profile.balance)
                    } else {
                        viewModel.setVolume(profile.leftVolume, newVal, false, profile.balance)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Balance Slider Section
        Column(modifier = Modifier.fillMaxWidth()) {
             Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("L BIAS", style = MaterialTheme.typography.labelSmall, color = com.example.ui.theme.TextMuted)
                val balText = if(profile.balance < 0) "-${(profile.balance * -100).roundToInt()} L" else if (profile.balance > 0) "+${(profile.balance * 100).roundToInt()} R" else "CENTER"
                Text(balText, style = MaterialTheme.typography.labelSmall, color = ElectricBlue)
                Text("R BIAS", style = MaterialTheme.typography.labelSmall, color = com.example.ui.theme.TextMuted)
            }
            
            Slider(
                value = profile.balance,
                onValueChange = {
                    viewModel.setVolume(profile.leftVolume, profile.rightVolume, profile.linked, it)
                },
                valueRange = -1f..1f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = ElectricBlue,
                    inactiveTrackColor = DarkSurface
                )
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Quick fixes (just visually styled)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            Button(
                onClick = { viewModel.setVolume(1.0f, 1.4f, false, profile.balance) },
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface, contentColor = com.example.ui.theme.TextSecondary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.border(1.dp, BorderSurface, RoundedCornerShape(8.dp))
            ) {
                Text("FIX RIGHT", style = MaterialTheme.typography.labelSmall)
            }
            
            Button(
                onClick = { viewModel.setVolume(1.4f, 1.0f, false, profile.balance) },
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface, contentColor = com.example.ui.theme.TextSecondary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.border(1.dp, BorderSurface, RoundedCornerShape(8.dp))
            ) {
                Text("FIX LEFT", style = MaterialTheme.typography.labelSmall)
            }
            
            Button(
                onClick = { viewModel.setVolume(1.0f, 1.0f, true, 0f) },
                colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.ElectricPurple.copy(alpha = 0.2f), contentColor = com.example.ui.theme.PurpleAccentLight),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.border(1.dp, com.example.ui.theme.ElectricPurple.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            ) {
                Text("RESET ALL", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun VolumeSliderColumn(label: String, value: Float, activeColor: Color, onValueChange: (Float) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("${(value * 100).roundToInt()}%", style = MaterialTheme.typography.titleMedium, color = activeColor)
        Spacer(modifier = Modifier.height(16.dp))
        
        Box(
            modifier = Modifier
                .height(220.dp)
                .width(64.dp)
                .background(DarkSurface, RoundedCornerShape(16.dp))
                .border(1.dp, BorderSurface, RoundedCornerShape(16.dp))
                .padding(vertical = 12.dp)
        ) {
             Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = 0f..1.5f,
                modifier = Modifier
                    .width(196.dp)
                    .align(Alignment.Center)
                    .rotate(-90f),
                colors = SliderDefaults.colors(
                    thumbColor = Color.Transparent,
                    activeTrackColor = activeColor,
                    inactiveTrackColor = Color.Transparent
                )
            )
             // Draw vertical line hint
             Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color.White.copy(alpha = 0.1f)).align(Alignment.Center))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = com.example.ui.theme.TextMuted)
    }
}
