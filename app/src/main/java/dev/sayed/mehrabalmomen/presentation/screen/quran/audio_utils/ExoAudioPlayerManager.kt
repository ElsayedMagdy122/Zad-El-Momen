package dev.sayed.mehrabalmomen.data.audio

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dev.sayed.mehrabalmomen.presentation.screen.quran.audio_utils.AudioPlayerManager
import dev.sayed.mehrabalmomen.presentation.screen.quran.audio_utils.AudioPlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ExoAudioPlayerManager(
    context: Context
) : AudioPlayerManager {

    private val exoPlayer = ExoPlayer.Builder(context).build()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var progressJob: Job? = null

    private val _playerState = MutableStateFlow(AudioPlayerState())
    override val playerState: StateFlow<AudioPlayerState> = _playerState.asStateFlow()

    init {
        setupPlayerListener()
    }

    private fun setupPlayerListener() {
        exoPlayer.addListener(object : Player.Listener {

            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        _playerState.update { it.copy(playbackState = AudioPlayerState.AudioPlaybackState.BUFFERING) }
                    }

                    Player.STATE_READY -> {
                        _playerState.update {
                            it.copy(
                                playbackState = if (exoPlayer.isPlaying) AudioPlayerState.AudioPlaybackState.PLAYING else AudioPlayerState.AudioPlaybackState.PAUSED,
                                durationMs = exoPlayer.duration.coerceAtLeast(0L)
                            )
                        }
                        if (exoPlayer.isPlaying) startProgressTracker()
                    }

                    Player.STATE_ENDED -> {
                        stopProgressTracker()
                        _playerState.update {
                            it.copy(
                                playbackState = AudioPlayerState.AudioPlaybackState.ENDED,
                                currentPositionMs = it.durationMs
                            )
                        }
                    }

                    Player.STATE_IDLE -> {
                        _playerState.update { it.copy(playbackState = AudioPlayerState.AudioPlaybackState.IDLE) }
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    _playerState.update { it.copy(playbackState = AudioPlayerState.AudioPlaybackState.PLAYING) }
                    startProgressTracker()
                } else {
                    stopProgressTracker()
                    if (_playerState.value.playbackState != AudioPlayerState.AudioPlaybackState.ENDED) {
                        _playerState.update { it.copy(playbackState = AudioPlayerState.AudioPlaybackState.PAUSED) }
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                stopProgressTracker()
                _playerState.update {
                    it.copy(
                        playbackState = AudioPlayerState.AudioPlaybackState.ERROR,
                        errorMessage = error.message
                    )
                }
            }
        })
    }

    override fun play(url: String, startPositionMs: Long) {
        _playerState.update {
            it.copy(currentUrl = url, playbackState = AudioPlayerState.AudioPlaybackState.BUFFERING)
        }

        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        exoPlayer.setMediaItem(MediaItem.fromUri(url), startPositionMs)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    override fun pause() {
        exoPlayer.pause()
        stopProgressTracker()
    }

    override fun resume() {
        if (exoPlayer.playbackState == Player.STATE_ENDED) {
            exoPlayer.seekTo(0)
        }
        exoPlayer.play()
    }

    override fun stop() {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        stopProgressTracker()
        _playerState.update { AudioPlayerState() }
    }

    override fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
        _playerState.update { it.copy(currentPositionMs = positionMs) }
    }

    override fun release() {
        stopProgressTracker()
        exoPlayer.release()
    }

    private fun startProgressTracker() {
        stopProgressTracker()
        progressJob = scope.launch {
            while (isActive) {
                _playerState.update {
                    it.copy(
                        currentPositionMs = exoPlayer.currentPosition.coerceAtLeast(0L),
                        durationMs = exoPlayer.duration.coerceAtLeast(0L)
                    )
                }
                delay(200L)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }
}