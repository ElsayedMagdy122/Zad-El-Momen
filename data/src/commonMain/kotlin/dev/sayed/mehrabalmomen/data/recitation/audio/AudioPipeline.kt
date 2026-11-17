package dev.sayed.mehrabalmomen.data.recitation.audio

import kotlinx.coroutines.flow.SharedFlow

expect class AudioPipeline() {
    val audioChunks: SharedFlow<Float32List>
    fun startStreaming(onError: (Exception) -> Unit = {})
    fun stopStreaming()
}
