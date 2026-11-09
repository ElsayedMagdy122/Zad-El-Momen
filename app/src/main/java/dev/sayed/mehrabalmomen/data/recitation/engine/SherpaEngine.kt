package dev.sayed.mehrabalmomen.data.recitation.engine

import android.content.Context
import android.util.Log
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
    companion object {
        private const val TAG = "SherpaEngine"
    }

    private val _transcriptionStream = MutableSharedFlow<TranscriptionResult>(replay = 0, extraBufferCapacity = 64)
    val transcriptionStream: SharedFlow<TranscriptionResult> = _transcriptionStream.asSharedFlow()

    private var onlineRecognizer: OnlineRecognizer? = null
    private var onlineStream: OnlineStream? = null
    private var lastLoggedText = ""

    private var isInitialized = false
    var currentStreamEpoch = 0
        private set

    private val lock = Any()

    fun initialize(): Boolean = synchronized(lock) {
        if (isInitialized) {
            Log.d(TAG, "🟢 [SherpaEngine] Already initialized.")
            return@synchronized true
        }
        try {
            Log.d(TAG, "🔄 [SherpaEngine] Extracting VFS assets for Sherpa-ONNX...")
            val modelPath = AssetExtractor.extractAssetIfNeeded(context, "zipformer_p_arabic_v3.1.int8.onnx")
            val tokensPath = AssetExtractor.extractAssetIfNeeded(context, "tokens.txt")

            Log.d(TAG, "📂 [SherpaEngine] Model: $modelPath")
            Log.d(TAG, "📂 [SherpaEngine] Tokens: $tokensPath")

            val config = OnlineRecognizerConfig().apply {
                modelConfig.tokens = tokensPath
                modelConfig.numThreads = 2
                modelConfig.provider = "cpu"
                modelConfig.debug = false // set true only for deep debugging

                try {
                    modelConfig.zipformer2Ctc.model = modelPath
                    Log.d(TAG, "📂 [SherpaEngine] Assigned model via direct property.")
                } catch (e: Throwable) {
                    try {
                        val z2Field = modelConfig.javaClass.getField("zipformer2Ctc")
                        val z2Config = z2Field.get(modelConfig)
                        val modelField = z2Config?.javaClass?.getField("model")
                        modelField?.set(z2Config, modelPath)
                    } catch (_: Exception) {
                        try {
                            val z2Field = modelConfig.javaClass.getDeclaredField("zipformer2Ctc")
                            z2Field.isAccessible = true
                            val z2Config = z2Field.get(modelConfig)
                            val modelField = z2Config?.javaClass?.getDeclaredField("model")
                            modelField?.isAccessible = true
                            modelField?.set(z2Config, modelPath)
                        } catch (ex: Exception) {
                            Log.e(TAG, "❌ [SherpaEngine] Could not set model path: ${ex.message}")
                        }
                    }
                }
            }

            onlineRecognizer = OnlineRecognizer(null, config)
            onlineStream = onlineRecognizer?.createStream()

            isInitialized = onlineRecognizer != null && onlineStream != null
            if (isInitialized) {
                Log.d(TAG, "✅ [SherpaEngine] Initialized OK. Epoch=$currentStreamEpoch")
            } else {
                Log.e(TAG, "❌ [SherpaEngine] recognizer or stream is null after init!")
            }
        } catch (e: Throwable) {
            Log.e(TAG, "❌ [SherpaEngine] Init failed: ${e.message}", e)
            val corruptModel = File(context.filesDir, "zipformer_p_arabic_v3.1.int8.onnx")
            if (corruptModel.exists()) {
                Log.w(TAG, "⚠️ Deleting corrupted model file (${corruptModel.length()} bytes)...")
                corruptModel.delete()
            }
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
                if (text != lastLoggedText) {
                    Log.d(TAG, "🎯 ASR text=\"$text\" tokens=${rawTokens.size} isFinal=$isFinal")
                    lastLoggedText = text
                }
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ [SherpaEngine] transcribe error: ${e.message}", e)
            false
        }
    }

    fun resetBuffer() = synchronized(lock) {
        currentStreamEpoch++
        try {
            onlineStream?.release()
            onlineStream = onlineRecognizer?.createStream()
            Log.d(TAG, "🔄 [SherpaEngine] Buffer reset. Epoch=$currentStreamEpoch")
        } catch (e: Exception) {
            Log.e(TAG, "❌ [SherpaEngine] resetBuffer error: ${e.message}", e)
        }
    }

    fun destroy() = synchronized(lock) {
        isInitialized = false
        try { onlineStream?.release()     } catch (_: Exception) {}
        try { onlineRecognizer?.release() } catch (_: Exception) {}
        onlineStream    = null
        onlineRecognizer = null
        Log.d(TAG, "🛑 [SherpaEngine] Destroyed.")
    }
}
