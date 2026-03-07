package dev.sayed.mehrabalmomen.presentation.screen.radio.player

import kotlinx.coroutines.flow.StateFlow

interface PlayerController {
    val playerState: StateFlow<PlayerState>
    fun play(url: String)
    fun pause()
    fun stop()
    fun release()
    fun setOnErrorListener(onError: (Throwable) -> Unit)
}