package dev.sayed.mehrabalmomen.presentation.screen.radio

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface MediaRepository {
   suspend fun play(url: String)
    suspend  fun pause()
    suspend fun stop()
    suspend  fun isPlaying(): Boolean
}
class MediaRepositoryImpl(
    private val context: Context
) : MediaRepository {

    private val player = ExoPlayer.Builder(context).build()
    private var currentUrl: String? = null

    override suspend fun play(url: String) = withContext(Dispatchers.Main) {
        if (currentUrl != url) {
            player.setMediaItem(MediaItem.fromUri(url))
            player.prepare()
            currentUrl = url
        }
        player.playWhenReady = true
    }

    override suspend fun pause() = withContext(Dispatchers.Main) {
        player.pause()
    }

    override suspend fun stop() = withContext(Dispatchers.Main) {
        player.stop()
        currentUrl = null
    }

    override suspend fun isPlaying(): Boolean = withContext(Dispatchers.Main) {
        player.isPlaying
    }

    fun currentUrl(): String? = currentUrl
}