package com.example.audio

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.log10
import kotlin.math.sin
import kotlin.math.sqrt

class AcousticSoundDetector(private val context: Context) {

    private val _currentDb = MutableStateFlow(25f)
    val currentDb: StateFlow<Float> = _currentDb.asStateFlow()

    private val _waveformHistory = MutableStateFlow<List<Float>>(List(30) { 0.1f })
    val waveformHistory: StateFlow<List<Float>> = _waveformHistory.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _isSimulationActive = MutableStateFlow(false)
    val isSimulationActive: StateFlow<Boolean> = _isSimulationActive.asStateFlow()

    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null
    private var simulationSpike = 0f

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun hasRecordPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun triggerSimulationNoise(amountDb: Float) {
        simulationSpike = amountDb
    }

    @SuppressLint("MissingPermission")
    fun startDetection(scope: CoroutineScope, forceSimulation: Boolean = false) {
        if (_isRecording.value) return

        val canUseMic = hasRecordPermission() && !forceSimulation
        _isSimulationActive.value = !canUseMic
        _isRecording.value = true

        recordingJob = scope.launch(Dispatchers.IO) {
            if (canUseMic) {
                runMicCapture()
            } else {
                runSimulationLoop()
            }
        }
    }

    fun stopDetection() {
        _isRecording.value = false
        recordingJob?.cancel()
        recordingJob = null
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (_: Exception) {
        } finally {
            audioRecord = null
        }
        _currentDb.value = 20f
    }

    @SuppressLint("MissingPermission")
    private suspend fun runMicCapture() {
        val sampleRate = 44100
        val bufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(2048)

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                _isSimulationActive.value = true
                runSimulationLoop()
                return
            }

            audioRecord?.startRecording()
            val audioBuffer = ShortArray(bufferSize / 2)
            val history = ArrayDeque<Float>(List(30) { 0.1f })

            var smoothedDb = 25f

            while (_isRecording.value) {
                val readSamples = audioRecord?.read(audioBuffer, 0, audioBuffer.size) ?: 0
                if (readSamples > 0) {
                    var sumSquare = 0.0
                    for (i in 0 until readSamples) {
                        sumSquare += audioBuffer[i] * audioBuffer[i]
                    }
                    val rms = sqrt(sumSquare / readSamples)
                    // Convert RMS to an accessible dB scale [20 dB silent room to ~90 dB loud]
                    val rawDb = if (rms > 0.0) {
                        (20.0 * log10(rms) + 5.0).toFloat().coerceIn(20f, 95f)
                    } else 20f

                    // Smooth filter
                    smoothedDb = (smoothedDb * 0.65f) + (rawDb * 0.35f)
                    _currentDb.value = smoothedDb

                    val normalizedWave = ((smoothedDb - 20f) / 75f).coerceIn(0.05f, 1f)
                    if (history.size >= 30) history.removeFirst()
                    history.addLast(normalizedWave)
                    _waveformHistory.value = history.toList()

                    // Haptic buzz on high threshold
                    if (smoothedDb > 68f) {
                        triggerVibrate(60)
                    }
                }
                delay(60)
            }
        } catch (_: Exception) {
            // Fall back to simulation if mic threw an error
            _isSimulationActive.value = true
            runSimulationLoop()
        }
    }

    private suspend fun runSimulationLoop() {
        var tick = 0
        val history = ArrayDeque<Float>(List(30) { 0.1f })
        var smoothedDb = 28f

        while (_isRecording.value) {
            tick++
            // Organic ambient sound fluctuation
            val ambient = 28f + (sin(tick * 0.15) * 4f).toFloat() + (sin(tick * 0.4) * 2f).toFloat()
            val totalTarget = ambient + simulationSpike

            // Decay simulation spike
            simulationSpike = (simulationSpike * 0.8f)
            if (simulationSpike < 0.5f) simulationSpike = 0f

            smoothedDb = (smoothedDb * 0.7f) + (totalTarget * 0.3f)
            _currentDb.value = smoothedDb

            val normalizedWave = ((smoothedDb - 20f) / 75f).coerceIn(0.05f, 1f)
            if (history.size >= 30) history.removeFirst()
            history.addLast(normalizedWave)
            _waveformHistory.value = history.toList()

            if (smoothedDb > 65f) {
                triggerVibrate(50)
            }

            delay(70)
        }
    }

    fun triggerVibrate(durationMs: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {
        }
    }
}
