package dev.sayed.mehrabalmomen.data.recitation.engine

import dev.sayed.mehrabalmomen.data.recitation.audio.Float32List
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

actual class SherpaEngine {
    private val _transcriptionStream = MutableSharedFlow<TranscriptionResult>(replay = 0, extraBufferCapacity = 64)
    actual val transcriptionStream: SharedFlow<TranscriptionResult> = _transcriptionStream.asSharedFlow()

    actual var currentStreamEpoch: Int = 0

    actual fun initialize(): Boolean = true
    actual fun transcribe(chunk: Float32List, isFinal: Boolean): Boolean = true
    actual fun resetBuffer() { currentStreamEpoch++ }
    actual fun destroy() {}
}
