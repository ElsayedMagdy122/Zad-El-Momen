package dev.sayed.mehrabalmomen.presentation.screen.quran.audio_utils

import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerManager {
    val playerState: StateFlow<AudioPlayerState>

    fun play(url: String, startPositionMs: Long = 0L)

    fun pause()
    fun resume()
    fun stop()
    fun seekTo(positionMs: Long)
    fun release()
}