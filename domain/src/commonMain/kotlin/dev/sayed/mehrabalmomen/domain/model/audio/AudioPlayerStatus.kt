package dev.sayed.mehrabalmomen.domain.model.audio

/**
 * Represents the current operational state of the audio player.
 */
enum class AudioPlayerStatus {
    /** The player is created but has no source. */
    IDLE,

    /** The player is loading or buffering the source. */
    BUFFERING,

    /** Audio is currently playing. */
    PLAYING,

    /** Audio is paused. */
    PAUSED,

    /** Playback has reached the end of the source. */
    ENDED,

    /** An error occurred during playback or initialization. */
    ERROR
}
