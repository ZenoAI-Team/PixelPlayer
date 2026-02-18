package com.theveloper.pixelplay.data.service.player

import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder

class TitanAudioProcessorTest {

    @Test
    fun testPreAmpGain() {
        val processor = TitanAudioProcessor()
        val format = AudioProcessor.AudioFormat(44100, 2, C.ENCODING_PCM_FLOAT)
        processor.configure(format)
        processor.isActive // Ensure it's active
        processor.flush()

        // +6.02dB is exactly 2.0x gain
        processor.setPreAmp(6.0206f)

        val input = ByteBuffer.allocateDirect(8).order(ByteOrder.nativeOrder())
        input.putFloat(0.4f) // L
        input.putFloat(0.4f) // R
        input.flip()

        processor.queueInput(input)
        val output = processor.getOutput()
        output.order(ByteOrder.nativeOrder())

        assertTrue(output.hasRemaining())
        val outL = output.float
        val outR = output.float

        assertEquals(0.8f, outL, 0.001f)
        assertEquals(0.8f, outR, 0.001f)
    }

    @Test
    fun testPeakLimiter() {
        val processor = TitanAudioProcessor()
        val format = AudioProcessor.AudioFormat(44100, 2, C.ENCODING_PCM_FLOAT)
        processor.configure(format)
        processor.flush()

        processor.setPreAmp(20f) // High gain (10x)

        val input = ByteBuffer.allocateDirect(8).order(ByteOrder.nativeOrder())
        input.putFloat(0.5f) // Result would be 5.0f without limiter
        input.putFloat(-0.5f) // Result would be -5.0f without limiter
        input.flip()

        processor.queueInput(input)
        val output = processor.getOutput()
        output.order(ByteOrder.nativeOrder())

        val outL = output.float
        val outR = output.float

        assertEquals(1.0f, outL, 0.0001f)
        assertEquals(-1.0f, outR, 0.0001f)
    }

    @Test
    fun testEqPeaking() {
        val processor = TitanAudioProcessor()
        val format = AudioProcessor.AudioFormat(44100, 2, C.ENCODING_PCM_FLOAT)
        processor.configure(format)
        processor.flush()

        processor.setEqEnabled(true)
        // Boost 1kHz by 6dB
        val bands = listOf(TitanEqBand(1000f, 6.0206f, 1.414f))
        processor.setEqBands(bands)

        // Input a 1kHz "sample" (this is a bit simplistic as EQ works over time, but we test if it's active)
        val input = ByteBuffer.allocateDirect(8).order(ByteOrder.nativeOrder())
        input.putFloat(0.5f)
        input.putFloat(0.5f)
        input.flip()

        processor.queueInput(input)
        val output = processor.getOutput()
        output.order(ByteOrder.nativeOrder())

        val outL = output.float
        val outR = output.float

        // Since it's the first sample, it won't be exactly the gain, but it should be different from 0.5f
        assertTrue(outL != 0.5f)
    }
}
