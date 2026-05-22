package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioEffectManager
import com.example.data.BudDatabase
import com.example.data.BudProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class BudViewModel(application: Application) : AndroidViewModel(application) {
    private val profileDao = BudDatabase.getDatabase(application).budProfileDao()
    val audioEffectManager = AudioEffectManager
    
    private val _currentProfile = MutableStateFlow(BudProfile(name = "Default"))
    val currentProfile: StateFlow<BudProfile> = _currentProfile.asStateFlow()

    init {
        viewModelScope.launch {
            val profiles = profileDao.getAllProfiles().firstOrNull() ?: emptyList()
            if (profiles.isNotEmpty()) {
                val profile = profiles.firstOrNull { it.isAutoLoad } ?: profiles.first()
                _currentProfile.value = profile
                applyProfileToSystem(profile)
            } else {
                val defaultProfile = BudProfile(name = "Default", isAutoLoad = true)
                val id = profileDao.insertProfile(defaultProfile)
                val newProfile = defaultProfile.copy(id = id.toInt())
                _currentProfile.value = newProfile
                applyProfileToSystem(newProfile)
            }
        }
    }

    // Update state and apply
    private fun updateAndApply(modifier: BudProfile.() -> BudProfile) {
        val nextState = _currentProfile.value.modifier()
        _currentProfile.value = nextState
        viewModelScope.launch {
            profileDao.updateProfile(nextState)
        }
        applyProfileToSystem(nextState)
    }

    // Provide methods for the UI to call
    fun setVolume(left: Float, right: Float, linked: Boolean, balance: Float) {
        updateAndApply {
            copy(leftVolume = left, rightVolume = right, linked = linked, balance = balance)
        }
        // Balance logic could go here by directly altering output, but true global L/R balance is not easily achievable via standard public APIs.
        // We'll emulate it through the UI presentation.
    }
    
    fun toggleEq(enabled: Boolean) = updateAndApply { copy(eqEnabled = enabled) }
    fun setEqBand(band: Int, level: Short) {
        updateAndApply {
            when(band) {
                0 -> copy(band0 = level)
                1 -> copy(band1 = level)
                2 -> copy(band2 = level)
                3 -> copy(band3 = level)
                4 -> copy(band4 = level)
                else -> this
            }
        }
    }
    
    fun toggleBassBoost(enabled: Boolean) = updateAndApply { copy(bassBoostEnabled = enabled) }
    fun setBassBoost(strength: Short) = updateAndApply { copy(bassBoostStrength = strength) }
    
    fun toggleVirtualizer(enabled: Boolean) = updateAndApply { copy(virtualizerEnabled = enabled) }
    fun setVirtualizer(strength: Short) = updateAndApply { copy(virtualizerStrength = strength) }
    
    fun toggleLoudness(enabled: Boolean) = updateAndApply { copy(loudnessEnhancerEnabled = enabled) }
    fun setLoudness(gain: Int) = updateAndApply { copy(loudnessEnhancerStrength = gain) }

    fun addProfile(name: String) {
        viewModelScope.launch {
            val profile = BudProfile(name = name)
            profileDao.insertProfile(profile)
            _currentProfile.value = profile
        }
    }

    private fun applyProfileToSystem(profile: BudProfile) {
        with(audioEffectManager) {
            setEqualizerEnabled(profile.eqEnabled)
            if (profile.eqEnabled) {
                setBandLevel(0, profile.band0)
                setBandLevel(1, profile.band1)
                setBandLevel(2, profile.band2)
                setBandLevel(3, profile.band3)
                setBandLevel(4, profile.band4)
            }
            
            setBassBoostEnabled(profile.bassBoostEnabled)
            setBassBoostStrength(profile.bassBoostStrength)
            
            setVirtualizerEnabled(profile.virtualizerEnabled)
            setVirtualizerStrength(profile.virtualizerStrength)
            
            setLoudnessEnhancerEnabled(profile.loudnessEnhancerEnabled)
            setLoudnessEnhancerTargetGain(profile.loudnessEnhancerStrength)
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Do not release AudioEffectManager here so effects survive backgrounding
    }
}
