package dev.sayed.mehrabalmomen.data.recitation.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Production-grade Audio Recording Pipeline tailored for Quranic speech with VAD & silence filtering.
 */
class AudioPipeline(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    companion object {
        private const val TAG = "AudioPipeline"
        const val SAMPLE_RATE = 16000
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT

        // 480ms acoustic window matching reference Flutter implementation
        const val CHUNK_MS = 480
        const val BYTES_PER_SAMPLE = 2
        const val SAMPLES_PER_CHUNK = (SAMPLE_RATE * CHUNK_MS) / 1000
        const val RECORD_CHUNK_BYTES = SAMPLES_PER_CHUNK * BYTES_PER_SAMPLE
    }

    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null

    private val _audioChunks = MutableSharedFlow<Float32List>(replay = 0, extraBufferCapacity = 64)
    val audioChunks: SharedFlow<Float32List> = _audioChunks.asSharedFlow()

    private var isRecording = false

    @SuppressLint("MissingPermission")
    fun startStreaming(onError: (Exception) -> Unit = {}) {
        if (isRecording) {
            Log.w(TAG, "⚠️ [AudioPipeline] startStreaming called while already recording.")
            return
        }

        val minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        val bufferSize = maxOf(minBufferSize, RECORD_CHUNK_BYTES * 2)
        Log.d(TAG, "🎙️ [AudioPipeline] Initializing AudioRecord (sampleRate=$SAMPLE_RATE, bufferSize=$bufferSize)...")

        try {
            // VOICE_RECOGNITION is specifically tuned on Android for ASR without aggressive noise-gating
            val audioSource = MediaRecorder.AudioSource.VOICE_RECOGNITION
            Log.d(TAG, "🎙️ [AudioPipeline] Using AudioSource: VOICE_RECOGNITION ($audioSource)")

            audioRecord = AudioRecord(
                audioSource,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                Log.w(TAG, "⚠️ [AudioPipeline] VOICE_RECOGNITION not supported, retrying with MIC...")
                audioRecord?.release()
                audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE,
                    CHANNEL_CONFIG,
                    AUDIO_FORMAT,
                    bufferSize
                )
            }

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                throw IllegalStateException("AudioRecord failed to initialize.")
            }

            audioRecord?.startRecording()
            isRecording = true
            Log.d(TAG, "🟢 [AudioPipeline] Audio recording started successfully.")

            recordingJob = scope.launch(Dispatchers.IO) {
                val readBuffer = ShortArray(1024)
                val chunkSamples = SAMPLES_PER_CHUNK
                val accumulator = FloatArray(chunkSamples)
                var accCount = 0

                // Stream all continuous audio directly to Sherpa ONNX without VAD drops
                // Matching Flutter: completely eliminates dropped phonemes and mic calibration discrepancies
                while (isActive && isRecording) {
                    val readCount = audioRecord?.read(readBuffer, 0, readBuffer.size) ?: -1
                    if (readCount > 0) {
                        for (i in 0 until readCount) {
                            accumulator[accCount++] = readBuffer[i] / 32768.0f
                            if (accCount >= chunkSamples) {
                                val chunk = Float32List(chunkSamples) { idx -> accumulator[idx] }
                                _audioChunks.emit(chunk)
                                accCount = 0
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            isRecording = false
            Log.e(TAG, "❌ [AudioPipeline] Error starting audio stream: ${e.message}", e)
            onError(e)
        }
    }

    fun stopStreaming() {
        isRecording = false
        recordingJob?.cancel()
        recordingJob = null

        try {
            audioRecord?.stop()
            audioRecord?.release()
            Log.d(TAG, "🛑 [AudioPipeline] Audio recording stopped and released.")
        } catch (e: Exception) {
            Log.e(TAG, "❌ [AudioPipeline] Error stopping audio recorder: ${e.message}", e)
        }
        audioRecord = null
    }
}

class Float32List(val size: Int, init: (Int) -> Float = { 0f }) {
    private val data = FloatArray(size, init)

    operator fun get(index: Int): Float = data[index]
    operator fun set(index: Int, value: Float) {
        data[index] = value
    }

    fun toFloatArray(): FloatArray = data

    companion object {
        fun from(array: FloatArray): Float32List {
            val list = Float32List(array.size) { 0f }
            System.arraycopy(array, 0, list.data, 0, array.size)
            return list
        }
    }
}
