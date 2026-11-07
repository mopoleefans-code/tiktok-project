package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Random
import kotlin.math.sin

object SoundboardSynthesizer {

    private const val SAMPLE_RATE = 44100

    suspend fun playSynthesizedDecoy(soundType: String) = withContext(Dispatchers.Default) {
        val buffer = when (soundType) {
            "GLASS_SHATTER" -> generateGlassShatter()
            "ALARM_CLOCK" -> generateAlarmClock()
            "ULTRASONIC" -> generateUltrasonicPulse()
            "RADIO_STATIC" -> generateRadioStatic()
            "FLARE_HISS" -> generateFlareHiss()
            else -> generateGenericClick()
        }

        playPcmBuffer(buffer)
    }

    private fun generateGlassShatter(): ShortArray {
        val durationSeconds = 1.0f
        val numSamples = (SAMPLE_RATE * durationSeconds).toInt()
        val buffer = ShortArray(numSamples)
        val random = Random()

        for (i in 0 until numSamples) {
            val progress = i.toFloat() / numSamples
            val decay = Math.exp(-progress * 6.0).toFloat()
            // High frequency ring + burst noise
            val tone1 = sin(2.0 * Math.PI * 2400.0 * i / SAMPLE_RATE).toFloat()
            val tone2 = sin(2.0 * Math.PI * 3800.0 * i / SAMPLE_RATE).toFloat()
            val noise = (random.nextFloat() * 2f - 1f)
            val sample = (noise * 0.6f + tone1 * 0.2f + tone2 * 0.2f) * decay
            buffer[i] = (sample.coerceIn(-1.0f, 1.0f) * Short.MAX_VALUE * 0.85f).toInt().toShort()
        }
        return buffer
    }

    private fun generateAlarmClock(): ShortArray {
        val durationSeconds = 1.2f
        val numSamples = (SAMPLE_RATE * durationSeconds).toInt()
        val buffer = ShortArray(numSamples)

        val pingPeriod = (SAMPLE_RATE * 0.15f).toInt()
        for (i in 0 until numSamples) {
            val pingIndex = i % pingPeriod
            val pingProgress = pingIndex.toFloat() / pingPeriod
            val decay = Math.exp(-pingProgress * 12.0).toFloat()
            val tone = sin(2.0 * Math.PI * 1400.0 * i / SAMPLE_RATE).toFloat()
            val harmonic = sin(2.0 * Math.PI * 2800.0 * i / SAMPLE_RATE).toFloat() * 0.5f
            val sample = (tone + harmonic) * decay
            buffer[i] = (sample.coerceIn(-1.0f, 1.0f) * Short.MAX_VALUE * 0.7f).toInt().toShort()
        }
        return buffer
    }

    private fun generateUltrasonicPulse(): ShortArray {
        val durationSeconds = 1.5f
        val numSamples = (SAMPLE_RATE * durationSeconds).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val progress = i.toFloat() / numSamples
            val freq = 2800.0 - (progress * 1200.0) // 2.8kHz down to 1.6kHz piercing chirp
            val envelope = sin(Math.PI * progress).toFloat()
            val sample = sin(2.0 * Math.PI * freq * i / SAMPLE_RATE).toFloat() * envelope
            buffer[i] = (sample.coerceIn(-1.0f, 1.0f) * Short.MAX_VALUE * 0.8f).toInt().toShort()
        }
        return buffer
    }

    private fun generateRadioStatic(): ShortArray {
        val durationSeconds = 1.0f
        val numSamples = (SAMPLE_RATE * durationSeconds).toInt()
        val buffer = ShortArray(numSamples)
        val random = Random()

        for (i in 0 until numSamples) {
            val progress = i.toFloat() / numSamples
            val envelope = sin(Math.PI * progress).toFloat()
            val noise = (random.nextFloat() * 2f - 1f)
            val flutter = sin(2.0 * Math.PI * 18.0 * i / SAMPLE_RATE).toFloat() * 0.3f
            val sample = noise * (0.7f + flutter) * envelope
            buffer[i] = (sample.coerceIn(-1.0f, 1.0f) * Short.MAX_VALUE * 0.65f).toInt().toShort()
        }
        return buffer
    }

    private fun generateFlareHiss(): ShortArray {
        val durationSeconds = 1.4f
        val numSamples = (SAMPLE_RATE * durationSeconds).toInt()
        val buffer = ShortArray(numSamples)
        val random = Random()

        for (i in 0 until numSamples) {
            val progress = i.toFloat() / numSamples
            val envelope = if (progress < 0.2f) progress / 0.2f else Math.exp(-(progress.toDouble() - 0.2) * 3.0).toFloat()
            val hiss = (random.nextFloat() * 2f - 1f) * 0.8f
            val rumble = sin(2.0 * Math.PI * 120.0 * i / SAMPLE_RATE).toFloat() * 0.2f
            val sample = (hiss + rumble) * envelope
            buffer[i] = (sample.coerceIn(-1.0f, 1.0f) * Short.MAX_VALUE * 0.7f).toInt().toShort()
        }
        return buffer
    }

    private fun generateGenericClick(): ShortArray {
        val numSamples = (SAMPLE_RATE * 0.2f).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val decay = Math.exp(-i.toDouble() / (SAMPLE_RATE * 0.05)).toFloat()
            val sample = sin(2.0 * Math.PI * 880.0 * i / SAMPLE_RATE).toFloat() * decay
            buffer[i] = (sample * Short.MAX_VALUE * 0.5f).toInt().toShort()
        }
        return buffer
    }

    private fun playPcmBuffer(buffer: ShortArray) {
        try {
            val bufferSize = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(buffer.size * 2)

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack.play()
            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.stop()
            audioTrack.release()
        } catch (_: Exception) {
            // Ignore playback interruptions
        }
    }
}
