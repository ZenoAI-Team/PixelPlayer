package com.theveloper.pixelplay.data.service.player

import kotlinx.serialization.Serializable
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

/**
 * High-fidelity Biquad filter implementation for parametric equalization.
 */
class BiquadFilter {
    private var b0 = 0f
    private var b1 = 0f
    private var b2 = 0f
    private var a1 = 0f
    private var a2 = 0f

    private var x1 = 0f
    private var x2 = 0f
    private var y1 = 0f
    private var y2 = 0f

    /**
     * Configures the filter as a peaking equalizer band.
     */
    fun configurePeakingEq(sampleRate: Float, centerFreq: Float, Q: Float, dbGain: Float) {
        val a = 10f.pow(dbGain / 40f)
        val w0 = 2f * PI.toFloat() * centerFreq / sampleRate
        val alpha = sin(w0) / (2f * Q)

        val b0Raw = 1f + alpha * a
        val b1Raw = -2f * cos(w0)
        val b2Raw = 1f - alpha * a
        val a0Raw = 1f + alpha / a
        val a1Raw = -2f * cos(w0)
        val a2Raw = 1f - alpha / a

        // Normalize coefficients
        b0 = b0Raw / a0Raw
        b1 = b1Raw / a0Raw
        b2 = b2Raw / a0Raw
        a1 = a1Raw / a0Raw
        a2 = a2Raw / a0Raw
    }

    /**
     * Processes a single sample through the filter.
     */
    fun process(sample: Float): Float {
        val y = b0 * sample + b1 * x1 + b2 * x2 - a1 * y1 - a2 * y2
        x2 = x1
        x1 = sample
        y2 = y1
        y1 = y
        return y
    }

    /**
     * Resets the filter state to prevent artifacts.
     */
    fun reset() {
        x1 = 0f
        x2 = 0f
        y1 = 0f
        y2 = 0f
    }
}

/**
 * Represents a single band in the Titan Parametric EQ.
 */
@Serializable
data class TitanEqBand(
    val frequency: Float,
    var gain: Float,
    var q: Float,
    var enabled: Boolean = true
)

/**
 * Core engine for the 32-band Parametric EQ.
 */
class TitanEqCore(private val numBands: Int = 32) {
    private val filtersL = Array(numBands) { BiquadFilter() }
    private val filtersR = Array(numBands) { BiquadFilter() }
    private var currentBands: List<TitanEqBand> = emptyList()

    /**
     * Sets up the filter chain for the given sample rate and band configuration.
     */
    fun setup(sampleRate: Float, bands: List<TitanEqBand>) {
        currentBands = bands
        val bandsToProcess = numBands.coerceAtMost(bands.size)
        for (i in 0 until bandsToProcess) {
            val band = bands[i]
            if (band.enabled) {
                filtersL[i].configurePeakingEq(sampleRate, band.frequency, band.q, band.gain)
                filtersR[i].configurePeakingEq(sampleRate, band.frequency, band.q, band.gain)
            } else {
                filtersL[i].reset()
                filtersR[i].reset()
            }
        }
    }

    /**
     * Processes a stereo sample pair.
     */
    fun processL(sampleL: Float): Float {
        var outL = sampleL
        for (i in 0 until currentBands.size.coerceAtMost(numBands)) {
            if (currentBands[i].enabled) {
                outL = filtersL[i].process(outL)
            }
        }
        return outL
    }

    fun processR(sampleR: Float): Float {
        var outR = sampleR
        for (i in 0 until currentBands.size.coerceAtMost(numBands)) {
            if (currentBands[i].enabled) {
                outR = filtersR[i].process(outR)
            }
        }
        return outR
    }

    /**
     * Resets all filters in the chain.
     */
    fun reset() {
        filtersL.forEach { it.reset() }
        filtersR.forEach { it.reset() }
    }
}
