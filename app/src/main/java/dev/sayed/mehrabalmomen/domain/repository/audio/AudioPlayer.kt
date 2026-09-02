package dev.sayed.mehrabalmomen.domain.repository.audio

import dev.sayed.mehrabalmomen.domain.model.audio.AudioPlayerState
import dev.sayed.mehrabalmomen.domain.model.audio.AudioSource
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface defining the contract for audio playback operations.
 * This abstraction isolates the business logic and UI from specific libraries like ExoPlayer.
 */
interface AudioPlayer {

    /**
     * Observable state of the player.
     */
    val playerState: StateFlow<AudioPlayerState>

    /**
     * Starts playback of the provided source.
     * @param source The [AudioSource] to play.
     * @param startPositionMs Optional starting position in milliseconds.
     */
    fun play(source: AudioSource, startPositionMs: Long = 0L)

    /**
     * Pauses the current playback.
     */
    fun pause()

    /**
     * Resumes the current playback from where it was paused.
     */
    fun resume()

    /**
     * Stops playback and clears the current source.
     */
    fun stop()

    /**
     * Seeks to a specific position in the current audio.
     * @param positionMs The target position in milliseconds.
     */
    fun seekTo(positionMs: Long)

    /**
     * Releases system resources held by the player.
     */
    fun release()
}
