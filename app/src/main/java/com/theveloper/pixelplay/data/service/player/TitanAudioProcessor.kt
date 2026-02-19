package com.theveloper.pixelplay.data.service.player

import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.BaseAudioProcessor
import androidx.media3.common.C
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.pow

/**
 * Titan Audio Processor: High-fidelity DSP stage for VoidPlayer.
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

    // Real-time amplitude tracking for visualizers
    @Volatile
    private var currentPeakAmplitude: Float = 0f

    /**
     * Returns the current peak amplitude (0.0 to 1.0) of the processed audio.
     */
    fun getPeakAmplitude(): Float {
        val peak = currentPeakAmplitude
        currentPeakAmplitude = 0f // Reset for next sample block
        return peak
    }

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
        if (inputAudioFormat.encoding != C.ENCODING_PCM_FLOAT && inputAudioFormat.encoding != C.ENCODING_PCM_16BIT) {
            throw AudioProcessor.UnhandledAudioFormatException(inputAudioFormat)
        }
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

        val encoding = inputAudioFormat.encoding
        val channelCount = inputAudioFormat.channelCount
        if (channelCount <= 0) return

        // Calculate expected bytes per frame
        val bytesPerSample = if (encoding == C.ENCODING_PCM_FLOAT) 4 else 2
        val bytesPerFrame = bytesPerSample * channelCount

        // Ensure we only process complete frames to avoid partial reads/crashes
        val completeFramesCount = remaining / bytesPerFrame
        if (completeFramesCount == 0) {
            inputBuffer.position(inputBuffer.limit()) // Skip unusable partial frame
            return
        }

        val outputSize = completeFramesCount * channelCount * 4 // Always output FLOAT (4 bytes)

        val outputBuffer = replaceOutputBuffer(outputSize)
        outputBuffer.order(ByteOrder.nativeOrder())
        inputBuffer.order(ByteOrder.nativeOrder())

        val totalGain = preAmpGain * replayGain
        var maxInBlock = 0f

        repeat(completeFramesCount) {
            for (c in 0 until channelCount) {
                var sample = if (encoding == C.ENCODING_PCM_FLOAT) {
                    if (inputBuffer.remaining() >= 4) inputBuffer.float else 0f
                } else {
                    // Convert 16-bit PCM to Float [-1.0, 1.0]
                    if (inputBuffer.remaining() >= 2) inputBuffer.short / 32768.0f else 0f
                }

                sample *= totalGain

                if (eqEnabled) {
                    // Apply EQ (mapping odd channels to Right and even to Left for basic stereo/multi-channel support)
                    sample = if (c % 2 == 0) eqCore.processL(sample) else eqCore.processR(sample)
                }

                // Peak Limiter: Ensures no clipping after gain boost
                sample = sample.coerceIn(-1.0f, 1.0f)

                // Track peak for visualizer
                val absSample = if (sample < 0) -sample else sample
                if (absSample > maxInBlock) maxInBlock = absSample

                outputBuffer.putFloat(sample)
            }
        }
        if (maxInBlock > currentPeakAmplitude) {
            currentPeakAmplitude = maxInBlock
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
