package dev.sayed.mehrabalmomen.presentation.screen.quran.audio_utils

data class AudioPlayerState(
    val currentUrl: String? = null,
    val playbackState: AudioPlaybackState = AudioPlaybackState.IDLE,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val errorMessage: String? = null
){
    enum class AudioPlaybackState {
        IDLE, BUFFERING, PLAYING, PAUSED, ENDED, ERROR
    }
}