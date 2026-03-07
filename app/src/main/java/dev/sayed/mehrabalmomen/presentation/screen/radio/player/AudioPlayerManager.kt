package dev.sayed.mehrabalmomen.presentation.screen.radio.player


import android.content.Context
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

    private val scope = CoroutineScope(Dispatchers.Main)
    private val _playerState = MutableStateFlow(PlayerState())
    override val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private fun initPlayer() {
        if (player == null) {
            player = ExoPlayer.Builder(context).build().apply {

                addListener(object : Player.Listener {

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        updateState(isPlaying, currentUrl)
                    }

                    override fun onMediaItemTransition(
                        mediaItem: MediaItem?,
                        reason: Int
                    ) {
                        currentUrl = mediaItem?.localConfiguration?.uri?.toString()
                        updateState(isPlaying, currentUrl)
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        onError?.invoke(error)
                    }
                    override fun onPlaybackStateChanged(playbackState: Int) {

                        when (playbackState) {

                            Player.STATE_BUFFERING -> {
                                updateState(false, currentUrl)
                            }

                            Player.STATE_IDLE,
                            Player.STATE_ENDED -> {
                                updateState(false, currentUrl)
                            }

                            Player.STATE_READY -> {
                                updateState(player?.isPlaying == true, currentUrl)
                            }
                        }
                    }
                })
            }
        }
    }

    override fun play(url: String) {
        initPlayer()

        try {
            if (player?.currentMediaItem == null || currentUrl != url) {
                currentUrl = url
                player?.setMediaItem(MediaItem.fromUri(url))
                player?.prepare()
            }

            player?.play()
            updateState(isPlaying = true, currentUrl = url)

        } catch (e: Exception) {
            onError?.invoke(e)
        }
    }

    override fun pause() {
        player?.pause()
        updateState(isPlaying = false)
    }

    override fun stop() {
        player?.stop()
        updateState(isPlaying = false, currentUrl = null)
    }

    override fun release() {
        player?.release()
        player = null
        currentUrl = null
        updateState(false, null)
    }

    override fun setOnErrorListener(onError: (Throwable) -> Unit) {
        this.onError = onError
    }

    private fun updateState(isPlaying: Boolean, currentUrl: String? = this.currentUrl) {
        scope.launch {
            _playerState.value = PlayerState(isPlaying, currentUrl)
        }
    }
}