package dev.sayed.mehrabalmomen.data.recitation.engine

import dev.sayed.mehrabalmomen.data.recitation.audio.Float32List
import kotlinx.coroutines.flow.SharedFlow

expect class SherpaEngine {
    val transcriptionStream: SharedFlow<TranscriptionResult>
    var currentStreamEpoch: Int
    fun initialize(): Boolean
    fun transcribe(chunk: Float32List, isFinal: Boolean = false): Boolean
    fun resetBuffer()
    fun destroy()
}
