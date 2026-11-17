package dev.sayed.mehrabalmomen.data.recitation.audio

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

actual class AudioPipeline actual constructor() {
    private val _audioChunks = MutableSharedFlow<Float32List>(replay = 0, extraBufferCapacity = 64)
    actual val audioChunks: SharedFlow<Float32List> = _audioChunks.asSharedFlow()

    actual fun startStreaming(onError: (Exception) -> Unit) {}
    actual fun stopStreaming() {}
}
