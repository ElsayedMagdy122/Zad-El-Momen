package dev.sayed.mehrabalmomen.domain.repository.audio

import dev.sayed.mehrabalmomen.domain.model.audio.AudioPlayerState
import dev.sayed.mehrabalmomen.domain.model.audio.AudioSource
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayer {
    val playerState: StateFlow<AudioPlayerState>
    fun play(source: AudioSource, startPositionMs: Long = 0L)
    fun pause()
    fun resume()
    fun stop()
    fun seekTo(positionMs: Long)
    fun release()
}
