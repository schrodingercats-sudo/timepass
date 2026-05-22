package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.BudViewModel

@Composable
fun BoostScreen(viewModel: BudViewModel) {
    val profile by viewModel.currentProfile.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("AUDIO BOOSTS", style = MaterialTheme.typography.labelMedium, color = com.example.ui.theme.ElectricBlue)
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Bass Boost", style = MaterialTheme.typography.titleMedium)
                    Switch(
                        checked = profile.bassBoostEnabled, 
                        onCheckedChange = { viewModel.toggleBassBoost(it) },
                        colors = SwitchDefaults.colors(checkedTrackColor = com.example.ui.theme.ElectricBlue)
                    )
                }
                Slider(
                    value = profile.bassBoostStrength.toFloat(),
                    onValueChange = { viewModel.setBassBoost(it.toInt().toShort()) },
                    valueRange = 0f..1000f,
                    enabled = profile.bassBoostEnabled,
                    colors = SliderDefaults.colors(activeTrackColor = com.example.ui.theme.ElectricBlue)
                )
                Text("Strength: ${profile.bassBoostStrength}", style = MaterialTheme.typography.labelSmall, color = com.example.ui.theme.TextMuted)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Virtualizer (Surround)", style = MaterialTheme.typography.titleMedium)
                    Switch(
                        checked = profile.virtualizerEnabled, 
                        onCheckedChange = { viewModel.toggleVirtualizer(it) },
                        colors = SwitchDefaults.colors(checkedTrackColor = com.example.ui.theme.ElectricBlue)
                    )
                }
                Slider(
                    value = profile.virtualizerStrength.toFloat(),
                    onValueChange = { viewModel.setVirtualizer(it.toInt().toShort()) },
                    valueRange = 0f..1000f,
                    enabled = profile.virtualizerEnabled,
                    colors = SliderDefaults.colors(activeTrackColor = com.example.ui.theme.ElectricBlue)
                )
                Text("Strength: ${profile.virtualizerStrength}", style = MaterialTheme.typography.labelSmall, color = com.example.ui.theme.TextMuted)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Loudness Enhancer", style = MaterialTheme.typography.titleMedium)
                    Switch(
                        checked = profile.loudnessEnhancerEnabled, 
                        onCheckedChange = { viewModel.toggleLoudness(it) },
                        colors = SwitchDefaults.colors(checkedTrackColor = com.example.ui.theme.ElectricBlue)
                    )
                }
                Slider(
                    value = profile.loudnessEnhancerStrength.toFloat(),
                    onValueChange = { viewModel.setLoudness(it.toInt()) },
                    valueRange = 0f..3000f,
                    enabled = profile.loudnessEnhancerEnabled,
                    colors = SliderDefaults.colors(activeTrackColor = com.example.ui.theme.ElectricBlue)
                )
                Text("Target Gain: ${profile.loudnessEnhancerStrength} mB", style = MaterialTheme.typography.labelSmall, color = com.example.ui.theme.TextMuted)
                if (profile.loudnessEnhancerStrength > 1500 && profile.loudnessEnhancerEnabled) {
                    Text(
                        "Warning: High volume may damage hearing.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
