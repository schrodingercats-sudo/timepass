package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class BudProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val isAutoLoad: Boolean = false,
    
    // Balance
    val leftVolume: Float = 1.0f,
    val rightVolume: Float = 1.0f,
    val linked: Boolean = true,
    val balance: Float = 0.0f,
    
    // Equalizer
    val eqEnabled: Boolean = false,
    val band0: Short = 0, // 60Hz
    val band1: Short = 0, // 230Hz
    val band2: Short = 0, // 910Hz
    val band3: Short = 0, // 4kHz
    val band4: Short = 0, // 14kHz
    
    // Boosts
    val bassBoostEnabled: Boolean = false,
    val bassBoostStrength: Short = 0,
    val virtualizerEnabled: Boolean = false,
    val virtualizerStrength: Short = 0,
    val loudnessEnhancerEnabled: Boolean = false,
    val loudnessEnhancerStrength: Int = 0
)
