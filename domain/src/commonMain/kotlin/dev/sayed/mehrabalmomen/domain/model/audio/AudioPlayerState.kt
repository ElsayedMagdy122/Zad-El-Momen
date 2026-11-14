package dev.sayed.mehrabalmomen.domain.model.audio

/**
 * Domain model representing the real-time state of an audio playback session.
 */
data class AudioPlayerState(
    /** The source currently being played or loaded. */
    val currentSource: AudioSource? = null,

    /** The current status (Playing, Paused, etc.). */
    val status: AudioPlayerStatus = AudioPlayerStatus.IDLE,

    /** Current playback position in milliseconds. */
    val currentPositionMs: Long = 0L,

    /** Total duration of the audio in milliseconds. */
    val durationMs: Long = 0L,

    /** Optional error message if the status is ERROR. */
    val errorMessage: String? = null
)
