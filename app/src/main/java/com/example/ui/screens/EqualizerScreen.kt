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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.BudViewModel
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.BorderSurface
import com.example.ui.theme.TextMuted

@Composable
fun EqualizerScreen(viewModel: BudViewModel) {
    val profile by viewModel.currentProfile.collectAsStateWithLifecycle()
    val eqRange = viewModel.audioEffectManager.getBandLevelRange()
    val minEq = eqRange[0].toFloat()
    val maxEq = eqRange[1].toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("EQUALIZER", style = MaterialTheme.typography.labelMedium, color = ElectricPurple)
            Switch(
                checked = profile.eqEnabled,
                onCheckedChange = { viewModel.toggleEq(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = ElectricPurple
                )
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val bands = listOf(
                "60Hz" to profile.band0,
                "230Hz" to profile.band1,
                "910Hz" to profile.band2,
                "4kHz" to profile.band3,
                "14kHz" to profile.band4
            )

            bands.forEachIndexed { index, (label, value) ->
                EqBandColumn(
                    label = label,
                    value = value.toFloat(),
                    min = minEq,
                    max = maxEq,
                    enabled = profile.eqEnabled,
                    onValueChange = { newVal ->
                        viewModel.setEqBand(index, newVal.toInt().toShort())
                    }
                )
            }
        }
    }
}

@Composable
fun EqBandColumn(label: String, value: Float, min: Float, max: Float, enabled: Boolean, onValueChange: (Float) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val dbValue = value / 100f
        Text(String.format("%.1f dB", dbValue), style = MaterialTheme.typography.labelSmall, color = if(enabled) Color.White else TextMuted)
        
        Box(
            modifier = Modifier
                .height(260.dp)
                .width(48.dp)
                .padding(vertical = 16.dp)
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .border(1.dp, BorderSurface, RoundedCornerShape(12.dp))
        ) {
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = min..max,
                enabled = enabled,
                modifier = Modifier
                    .width(228.dp)
                    .align(Alignment.Center)
                    .rotate(-90f),
                colors = SliderDefaults.colors(
                    thumbColor = if(enabled) Color.White else TextMuted,
                    activeTrackColor = ElectricPurple,
                    inactiveTrackColor = Color.Transparent,
                    disabledThumbColor = TextMuted,
                    disabledActiveTrackColor = TextMuted.copy(alpha=0.3f),
                    disabledInactiveTrackColor = Color.Transparent
                )
            )
            // Center line
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.15f)).align(Alignment.Center))
            // Track line behind slider
            Box(modifier = Modifier.width(1.dp).fillMaxHeight().padding(vertical=16.dp).background(Color.White.copy(alpha = 0.05f)).align(Alignment.Center))
        }
        
        Text(label, style = MaterialTheme.typography.labelMedium, color = if(enabled) ElectricPurple else TextMuted)
    }
}
