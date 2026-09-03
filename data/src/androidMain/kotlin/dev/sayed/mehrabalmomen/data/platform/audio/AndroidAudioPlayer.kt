package dev.sayed.mehrabalmomen.data.platform.audio

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import dev.sayed.mehrabalmomen.domain.model.audio.AudioPlayerState
import dev.sayed.mehrabalmomen.domain.model.audio.AudioPlayerStatus
import dev.sayed.mehrabalmomen.domain.model.audio.AudioSource
import dev.sayed.mehrabalmomen.domain.repository.audio.AudioPlayer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import androidx.core.net.toUri

@UnstableApi
class AndroidAudioPlayer(
    private val context: Context
) : AudioPlayer {

    private val exoPlayer = ExoPlayer.Builder(context).build()
    private val playerScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var progressJob: Job? = null

    private val _playerState = MutableStateFlow(AudioPlayerState())
    override val playerState: StateFlow<AudioPlayerState> = _playerState.asStateFlow()

    init {
        setupPlayerListener()
    }

    private fun setupPlayerListener() {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                val status = when (playbackState) {
                    Player.STATE_BUFFERING -> AudioPlayerStatus.BUFFERING
                    Player.STATE_READY -> if (exoPlayer.isPlaying) AudioPlayerStatus.PLAYING else AudioPlayerStatus.PAUSED
                    Player.STATE_ENDED -> AudioPlayerStatus.ENDED
                    Player.STATE_IDLE -> AudioPlayerStatus.IDLE
                    else -> AudioPlayerStatus.IDLE
                }

                _playerState.update { 
                    it.copy(
                        status = status,
                        durationMs = exoPlayer.duration.coerceAtLeast(0L)
                    ) 
                }

                if (exoPlayer.isPlaying) startProgressTracker() else stopProgressTracker()
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playerState.update {
                    it.copy(
                        status = if (isPlaying) AudioPlayerStatus.PLAYING else AudioPlayerStatus.PAUSED
                    )
                }
                if (isPlaying) startProgressTracker() else stopProgressTracker()
            }

            override fun onPlayerError(error: PlaybackException) {
                stopProgressTracker()
                _playerState.update {
                    it.copy(
                        status = AudioPlayerStatus.ERROR,
                        errorMessage = error.message
                    )
                }
            }
        })
    }

    override fun play(source: AudioSource, startPositionMs: Long) {
        val uri = when (source) {
            is AudioSource.RemoteUrl -> source.url.toUri()
            is AudioSource.LocalFile -> Uri.fromFile(File(source.path))
            is AudioSource.LocalResource -> {
                val resId = context.resources.getIdentifier(source.name, "raw", context.packageName)
                if (resId != 0) RawResourceDataSource.buildRawResourceUri(resId) else null
            }
        }

        if (uri == null) {
            _playerState.update { it.copy(status = AudioPlayerStatus.ERROR, errorMessage = "Resource not found: $source") }
            return
        }

        _playerState.update { it.copy(currentSource = source, status = AudioPlayerStatus.BUFFERING) }

        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        exoPlayer.setMediaItem(MediaItem.fromUri(uri), startPositionMs)
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
        playerScope.cancel()
    }

    private fun startProgressTracker() {
        stopProgressTracker()
        progressJob = playerScope.launch {
            while (isActive) {
                _playerState.update {
                    it.copy(
                        currentPositionMs = exoPlayer.currentPosition.coerceAtLeast(0L),
                        durationMs = exoPlayer.duration.coerceAtLeast(0L)
                    )
                }
                delay(500L)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }
}
