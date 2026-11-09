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
import java.nio.ByteBuffer
import java.nio.ByteOrder

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

        // 480ms = wider acoustic window → CTC model sees more context per frame.
        const val CHUNK_MS = 480
        const val BYTES_PER_SAMPLE = 2
        const val RECORD_CHUNK_BYTES = (SAMPLE_RATE * 1 * BYTES_PER_SAMPLE * CHUNK_MS) / 1000
        private const val SILENCE_RMS_THRESHOLD = 0.002f
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
            val audioSource = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                MediaRecorder.AudioSource.UNPROCESSED
            } else {
                MediaRecorder.AudioSource.MIC
            }
            Log.d(TAG, "🎙️ [AudioPipeline] Using AudioSource: $audioSource (UNPROCESSED=${android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N})")

            audioRecord = AudioRecord(
                audioSource,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                Log.w(TAG, "⚠️ [AudioPipeline] UNPROCESSED not supported, retrying with MIC...")
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
                val byteBuffer = ByteArray(RECORD_CHUNK_BYTES)
                val floatBuffer = Float32List(RECORD_CHUNK_BYTES / BYTES_PER_SAMPLE)

                while (isActive && isRecording) {
                    val readBytes = audioRecord?.read(byteBuffer, 0, RECORD_CHUNK_BYTES) ?: -1
                    if (readBytes > 0) {
                        val sampleCount = readBytes / BYTES_PER_SAMPLE
                        val shortBuffer = ByteBuffer.wrap(byteBuffer, 0, readBytes)
                            .order(ByteOrder.LITTLE_ENDIAN)
                            .asShortBuffer()

                        for (i in 0 until sampleCount) {
                            val shortVal = shortBuffer.get(i)
                            floatBuffer[i] = shortVal / 32768.0f
                        }

                        val chunkCopy = Float32List(sampleCount) { i -> floatBuffer[i] }
                        val rms = calculateRms(chunkCopy)

                        // VAD Silence Filter: Only emit audio chunks when voice energy exceeds background noise threshold
                        if (rms >= SILENCE_RMS_THRESHOLD) {
                            _audioChunks.emit(chunkCopy)
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

    private fun calculateRms(buffer: Float32List): Float {
        var sum = 0.0f
        for (i in 0 until buffer.size) {
            val sample = buffer[i]
            sum += sample * sample
        }
        return Math.sqrt((sum / buffer.size).toDouble()).toFloat()
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
