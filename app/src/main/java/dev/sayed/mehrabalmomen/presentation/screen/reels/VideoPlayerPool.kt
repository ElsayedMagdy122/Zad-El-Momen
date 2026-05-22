package dev.sayed.mehrabalmomen.presentation.screen.reels

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

@Composable
fun rememberVideoPlayerPool(): VideoPlayerPool {
    val context = LocalContext.current
    val pool    = remember(context) { VideoPlayerPool(context) }
    DisposableEffect(pool) { onDispose { pool.release() } }
    return pool
}

class VideoPlayerPool(context: Context) {

    private val players: Array<ExoPlayer> = Array(POOL_SIZE) {
        ExoPlayer.Builder(context).build().apply {
            repeatMode    = Player.REPEAT_MODE_ONE
            volume        = 0f
            playWhenReady = false
        }
    }

    private val _readyPages = mutableStateOf<Set<Int>>(emptySet())
    val readyPages: State<Set<Int>> get() = _readyPages

    private val loadedIndex = IntArray(POOL_SIZE) { -1 }
    private var currentSlot = 1

    private val prevSlot  get() = Math.floorMod(currentSlot - 1, POOL_SIZE)
    private val nextSlot  get() = Math.floorMod(currentSlot + 1, POOL_SIZE)
    private val next2Slot get() = Math.floorMod(currentSlot + 2, POOL_SIZE)

    fun onPageChanged(page: Int, urls: List<String>) {
        val assignments = mapOf(
            prevSlot    to page - 1,
            currentSlot to page,
            nextSlot    to page + 1,
            next2Slot   to page + 2,
        )

        assignments.forEach { (slot, feedPage) ->
            if (feedPage in urls.indices) {
                val player = players[slot]
                val isWrongMedia  = loadedIndex[slot] != feedPage
                val isErrorState  = player.playerError != null
                // ↑ if PesReader corrupted the parser, force a fresh load

                if (isWrongMedia || isErrorState) {
                    player.apply {
                        stop()
                        clearMediaItems()
                        setMediaItem(MediaItem.fromUri(urls[feedPage]))
                        prepare()
                        playWhenReady = false
                        volume = 0f
                    }
                    loadedIndex[slot] = feedPage
                }
            } else {
                players[slot].apply {
                    stop()
                    clearMediaItems()
                }
                loadedIndex[slot] = -1
            }
        }

        players[currentSlot].apply {
            volume        = 1f
            playWhenReady = true
        }

        _readyPages.value = loadedIndex.filter { it >= 0 }.toSet()
    }

    fun rotate(delta: Int) {
        players[currentSlot].apply {
            volume        = 0f
            playWhenReady = false
        }

        val evictedSlot = when {
            delta > 0 -> prevSlot
            delta < 0 -> next2Slot
            else      -> -1
        }
        if (evictedSlot >= 0) {
            players[evictedSlot].apply {
                stop()
                clearMediaItems()
            }
            loadedIndex[evictedSlot] = -1
        }

        currentSlot = Math.floorMod(currentSlot + delta, POOL_SIZE)
    }

    fun playerForPage(page: Int, currentFeedPage: Int): ExoPlayer? {
        val delta = page - currentFeedPage
        if (delta < -1 || delta > 2) return null
        val slotIndex = loadedIndex.indexOfFirst { it == page }
        return if (slotIndex >= 0) players[slotIndex] else null
    }

    fun pauseActive()  { players[currentSlot].pause() }
    fun resumeActive() { players[currentSlot].play() }

    internal fun release() = players.forEach { it.release() }

    companion object {
        const val POOL_SIZE = 4
    }
}