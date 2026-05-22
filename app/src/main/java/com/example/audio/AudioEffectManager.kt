package com.example.audio

import android.media.MediaPlayer
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import android.media.audiofx.Virtualizer
import android.util.Log

object AudioEffectManager {
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null
    private var loudnessEnhancer: LoudnessEnhancer? = null
    private var testPlayer: MediaPlayer? = null
    private var sessionId = 0

    init {
        try {
            testPlayer = MediaPlayer()
            sessionId = testPlayer?.audioSessionId ?: 0
        } catch (e: Exception) {
            sessionId = 0
        }
        initEffects()
    }

    private fun initEffects() {
        try { equalizer = Equalizer(0, sessionId) } catch (e: Exception) { Log.e("AudioEffectManager", "Failed to init Equalizer", e) }
        try { bassBoost = BassBoost(0, sessionId) } catch (e: Exception) { Log.e("AudioEffectManager", "Failed to init BassBoost", e) }
        try { virtualizer = Virtualizer(0, sessionId) } catch (e: Exception) { Log.e("AudioEffectManager", "Failed to init Virtualizer", e) }
        try { loudnessEnhancer = LoudnessEnhancer(sessionId) } catch (e: Exception) { Log.e("AudioEffectManager", "Failed to init LoudnessEnhancer", e) }
    }

    // --- Equalizer ---
    fun setEqualizerEnabled(enabled: Boolean) {
        try {
            equalizer?.enabled = enabled
        } catch (e: Exception) { }
    }
    
    fun getEqualizerBands(): Short {
        return try {
            equalizer?.numberOfBands ?: 5
        } catch (e: Exception) {
            5
        }
    }

    fun setBandLevel(band: Short, level: Short) {
        try {
            equalizer?.setBandLevel(band, level)
        } catch (e: Exception) { }
    }
    
    fun getBandLevelRange(): ShortArray {
        return try {
            val range = equalizer?.bandLevelRange
            if (range != null && range.size >= 2 && range[0] < range[1]) {
                range
            } else {
                shortArrayOf(-1500, 1500)
            }
        } catch (e: Exception) {
            shortArrayOf(-1500, 1500)
        }
    }

    // --- Boosts ---
    fun setBassBoostEnabled(enabled: Boolean) {
        try { bassBoost?.enabled = enabled } catch (e: Exception) { }
    }

    fun setBassBoostStrength(strength: Short) {
        try { bassBoost?.setStrength(strength) } catch (e: Exception) { }
    }

    fun setVirtualizerEnabled(enabled: Boolean) {
        try { virtualizer?.enabled = enabled } catch (e: Exception) { }
    }

    fun setVirtualizerStrength(strength: Short) {
        try { virtualizer?.setStrength(strength) } catch (e: Exception) { }
    }

    fun setLoudnessEnhancerEnabled(enabled: Boolean) {
        try { loudnessEnhancer?.enabled = enabled } catch (e: Exception) { }
    }

    fun setLoudnessEnhancerTargetGain(gainmB: Int) {
        try { loudnessEnhancer?.setTargetGain(gainmB) } catch (e: Exception) { }
    }

    // --- Calibration/Test Player ---
    fun playTestTone(left: Float, right: Float) {
        testPlayer?.setVolume(left, right)
        // If we had a data source, we would call play() here.
        // For now, it just sets the volume of the dummy player.
    }
    
    fun stopTestTone() {
        // Dummy pause
        try {
            testPlayer?.pause()
        } catch (e: Exception) {}
    }

    fun release() {
        equalizer?.release()
        bassBoost?.release()
        virtualizer?.release()
        loudnessEnhancer?.release()
        testPlayer?.release()
        testPlayer = null
    }
}
