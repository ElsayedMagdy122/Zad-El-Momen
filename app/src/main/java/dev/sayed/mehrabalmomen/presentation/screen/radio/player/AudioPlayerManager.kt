package dev.sayed.mehrabalmomen.presentation.screen.radio.player


import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AudioPlayerManager(private val context: Context) : PlayerController {

    private var player: ExoPlayer? = null
    private var onError: ((Throwable) -> Unit)? = null
    private var currentUrl: String? = null
    private var pendingUrl: String? = null

    private val scope = CoroutineScope(Dispatchers.Main)
    private val mainHandler = Handler(Looper.getMainLooper())
    private val _playerState = MutableStateFlow(PlayerState())
    override val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private fun ensurePlayerInitialized() {
        if (player == null) {
            player = ExoPlayer.Builder(context).build().apply {
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        updateState(isPlaying, currentUrl, isError = false)
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        scope.launch {
                            _playerState.value = _playerState.value.copy(
                                isPlaying = false,
                                isError = true
                            )
                        }
                        onError?.invoke(error)
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_BUFFERING -> {
                                updateState(
                                    isPlaying = false,
                                    url = currentUrl,
                                    isError = false,
                                    isBuffering = true
                                )
                            }

                            Player.STATE_READY -> {
                                updateState(
                                    isPlaying = player?.isPlaying == true,
                                    url = currentUrl,
                                    isError = false,
                                    isBuffering = false
                                )
                            }

                            Player.STATE_ENDED -> {
                                stop()
                            }
                        }
                    }
                })
            }
        }
    }

    override fun play(url: String) {
        updateState(isPlaying = false, url = url, isError = false, isBuffering = true)
        pendingUrl = url
        
        mainHandler.post {
            try {
                ensurePlayerInitialized()
                val urlToPlay = pendingUrl ?: return@post
                if (currentUrl != urlToPlay) {
                    currentUrl = urlToPlay
                    player?.setMediaItem(MediaItem.fromUri(urlToPlay))
                    player?.prepare()
                }
                player?.play()
            } catch (e: Exception) {
                updateState(isPlaying = false, url = url, isError = true, isBuffering = false)
                onError?.invoke(e)
            }
        }
    }

    override fun pause() {
        player?.pause()
    }

    override fun stop() {
        player?.stop()
        currentUrl = null
        pendingUrl = null
        updateState(false, null, false)
    }

    override fun release() {
        player?.release()
        player = null
        currentUrl = null
        pendingUrl = null
        updateState(false, null, false)
    }

    override fun setOnErrorListener(onError: (Throwable) -> Unit) {
        this.onError = onError
    }

    private fun updateState(
        isPlaying: Boolean,
        url: String?,
        isError: Boolean,
        isBuffering: Boolean = false
    ) {
        scope.launch {
            _playerState.value = PlayerState(
                isPlaying = isPlaying,
                currentUrl = url,
                isError = isError,
                isBuffering = isBuffering
            )
        }
    }
}