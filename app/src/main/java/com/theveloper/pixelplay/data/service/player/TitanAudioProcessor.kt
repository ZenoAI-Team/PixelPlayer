package com.theveloper.pixelplay.data.service.player

import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.BaseAudioProcessor
import androidx.media3.common.C
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.pow

/**
 * Titan Audio Processor: High-fidelity DSP stage for PixelPlayer.
 * Handles Pre-Amp, ReplayGain, 32-Band Parametric EQ, and Peak Limiting.
 *
 * Processes audio in 32-bit Float PCM to maintain headroom and minimize quantization error.
 */
class TitanAudioProcessor : BaseAudioProcessor() {

    private var preAmpGain: Float = 1.0f
    private var replayGain: Float = 1.0f
    private var eqEnabled: Boolean = false
    private val eqCore = TitanEqCore(32)
    private var bands: List<TitanEqBand> = emptyList()

    /**
     * Sets the Pre-Amp gain in decibels (-30dB to +30dB).
     */
    fun setPreAmp(db: Float) {
        preAmpGain = 10f.pow(db / 20f)
    }

    /**
     * Sets the ReplayGain adjustment in decibels.
     */
    fun setReplayGain(db: Float) {
        replayGain = 10f.pow(db / 20f)
    }

    /**
     * Enables or disables the EQ stage.
     */
    fun setEqEnabled(enabled: Boolean) {
        eqEnabled = enabled
    }

    /**
     * Updates the EQ band configuration.
     */
    fun setEqBands(newBands: List<TitanEqBand>) {
        bands = newBands
        if (isActive) {
            eqCore.setup(inputAudioFormat.sampleRate.toFloat(), bands)
        }
    }

    override fun onConfigure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        // Request Float output from the previous processor/decoder to ensure high-fidelity chain.
        return AudioProcessor.AudioFormat(
            inputAudioFormat.sampleRate,
            inputAudioFormat.channelCount,
            C.ENCODING_PCM_FLOAT
        )
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        val remaining = inputBuffer.remaining()
        if (remaining == 0) return

        // We expect FLOAT input because we requested it in onConfigure
        val outputBuffer = replaceOutputBuffer(remaining)
        outputBuffer.order(ByteOrder.nativeOrder())
        inputBuffer.order(ByteOrder.nativeOrder())

        val totalGain = preAmpGain * replayGain
        val channelCount = inputAudioFormat.channelCount

        while (inputBuffer.hasRemaining()) {
            for (c in 0 until channelCount) {
                if (!inputBuffer.hasRemaining()) break

                var sample = inputBuffer.float * totalGain

                if (eqEnabled) {
                    // Apply EQ (mapping odd channels to Right and even to Left for basic stereo/multi-channel support)
                    sample = if (c % 2 == 0) eqCore.processL(sample) else eqCore.processR(sample)
                }

                // Peak Limiter: Ensures no clipping after gain boost
                sample = sample.coerceIn(-1.0f, 1.0f)

                outputBuffer.putFloat(sample)
            }
        }
        outputBuffer.flip()
    }

    override fun onFlush() {
        eqCore.reset()
    }

    override fun onReset() {
        eqCore.reset()
    }
}
