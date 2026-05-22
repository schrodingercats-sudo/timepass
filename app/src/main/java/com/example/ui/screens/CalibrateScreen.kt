package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.viewmodel.BudViewModel

@Composable
fun CalibrateScreen(viewModel: BudViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("CALIBRATION WIZARD", style = MaterialTheme.typography.labelMedium, color = com.example.ui.theme.ElectricBlue)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Use this tool to fix unbalanced earbuds where one side is quieter than the other.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Step 1: Test Playback", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { viewModel.audioEffectManager.playTestTone(1f, 0f) },
                        colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.ElectricBlue)
                    ) {
                        Text("Test Left")
                    }
                    Button(
                        onClick = { viewModel.audioEffectManager.playTestTone(0f, 1f) },
                        colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.ElectricBlue)
                    ) {
                        Text("Test Right")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { viewModel.audioEffectManager.stopTestTone() },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = com.example.ui.theme.ErrorRed)
                ) {
                    Text("Stop Test Tone")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Step 2: Quick Fixes", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Which ear is quieter?")
                
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Left is quiet", color = com.example.ui.theme.TextSecondary)
                        Button(
                            onClick = { viewModel.setVolume(1.4f, 1.0f, false, 0f) }, modifier = Modifier.padding(top = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.ElectricPurple)
                        ) {
                            Text("Boost L 40%")
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Right is quiet", color = com.example.ui.theme.TextSecondary)
                        Button(
                            onClick = { viewModel.setVolume(1.0f, 1.4f, false, 0f) }, modifier = Modifier.padding(top = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.ElectricPurple)
                        ) {
                            Text("Boost R 40%")
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        Text("Note: Hardware balance issues require manual slider tweaking in the Balance tab for perfect results.", 
             style = MaterialTheme.typography.labelSmall)
    }
}
