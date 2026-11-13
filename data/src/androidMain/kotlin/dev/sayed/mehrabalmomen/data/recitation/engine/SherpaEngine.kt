package dev.sayed.mehrabalmomen.data.recitation.engine

import android.content.Context
import java.io.File
import com.k2fsa.sherpa.onnx.OnlineRecognizer
import com.k2fsa.sherpa.onnx.OnlineRecognizerConfig
import com.k2fsa.sherpa.onnx.OnlineStream
import dev.sayed.mehrabalmomen.data.recitation.audio.Float32List
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class TranscriptionResult(
    val text: String,
    val isFinal: Boolean,
    val startTime: Long,
    val tokens: List<String>,
    val timestamps: List<Double>,
    val streamEpoch: Int
)

/**
 * Production-grade Sherpa-ONNX ASR Streaming Engine.
 *
 * KEY FIX: getResult(stream) exposes real per-token timestamps through the
 * OnlineRecognizerResult. We must read result.tokens AND result.timestamps
 * directly instead of splitting result.text by spaces (which loses timing).
 *
 * The model emits tokens such as: ["بِ", "سمِ", "لل", "اهِ"] with a
 * corresponding timestamp (in seconds from stream start) per token.
 * These timestamps are what drive the Tajweed duration matrix.
 */
class SherpaEngine(private val context: Context) {

    private val _transcriptionStream = MutableSharedFlow<TranscriptionResult>(replay = 0, extraBufferCapacity = 64)
    val transcriptionStream: SharedFlow<TranscriptionResult> = _transcriptionStream.asSharedFlow()

    private var onlineRecognizer: OnlineRecognizer? = null
    private var onlineStream: OnlineStream? = null

    private var isInitialized = false
    var currentStreamEpoch = 0
        private set

    private val lock = Any()

    fun initialize(): Boolean = synchronized(lock) {
        if (isInitialized) {
            return@synchronized true
        }
        try {
            val modelPath = AssetExtractor.extractAssetIfNeeded(context, "zipformer_p_arabic_v3.1.int8.onnx")
            val tokensPath = AssetExtractor.extractAssetIfNeeded(context, "tokens.txt")

            val config = OnlineRecognizerConfig().apply {
                modelConfig.tokens = tokensPath
                modelConfig.numThreads = 2
                modelConfig.provider = "cpu"
                modelConfig.debug = false
                modelConfig.zipformer2Ctc.model = modelPath
            }

            onlineRecognizer = OnlineRecognizer(null, config)
            onlineStream = onlineRecognizer?.createStream()

            isInitialized = onlineRecognizer != null && onlineStream != null
        } catch (_: Throwable) {
            isInitialized = false
        }
        return@synchronized isInitialized
    }

    fun transcribe(chunk: Float32List, isFinal: Boolean = false): Boolean = synchronized(lock) {
        val recognizer = onlineRecognizer
        val stream = onlineStream
        if (!isInitialized || recognizer == null || stream == null) {
            return@synchronized false
        }

        return@synchronized try {
            stream.acceptWaveform(chunk.toFloatArray(), 16000)

            while (recognizer.isReady(stream)) {
                recognizer.decode(stream)
            }

            val result = recognizer.getResult(stream)
            val text = result.text.trim()

            if (text.isNotEmpty()) {
                val rawTokens: List<String>
                val rawTimestamps: List<Double>

                val tokArray: Array<String>? = try { result.tokens } catch (_: Throwable) { null }
                val tsArray:  FloatArray?     = try { result.timestamps } catch (_: Throwable) { null }

                if (tokArray != null && tokArray.isNotEmpty()) {
                    rawTokens     = tokArray.toList()
                    rawTimestamps = tsArray?.map { it.toDouble() }
                        ?: List(tokArray.size) { i -> (i + 1) * 0.08 }
                } else {
                    rawTokens     = text.split(" ").filter { it.isNotBlank() }
                    rawTimestamps = List(rawTokens.size) { i -> (i + 1) * 0.08 }
                }

                _transcriptionStream.tryEmit(
                    TranscriptionResult(
                        text        = text,
                        isFinal     = isFinal,
                        startTime   = System.currentTimeMillis(),
                        tokens      = rawTokens,
                        timestamps  = rawTimestamps.take(rawTokens.size),
                        streamEpoch = currentStreamEpoch
                    )
                )
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    fun resetBuffer() = synchronized(lock) {
        currentStreamEpoch++
        try {
            onlineStream?.release()
            onlineStream = onlineRecognizer?.createStream()
        } catch (_: Exception) {
        }
    }

    fun destroy() = synchronized(lock) {
        isInitialized = false
        try { onlineStream?.release()     } catch (_: Exception) {}
        try { onlineRecognizer?.release() } catch (_: Exception) {}
        onlineStream    = null
        onlineRecognizer = null
    }
}
