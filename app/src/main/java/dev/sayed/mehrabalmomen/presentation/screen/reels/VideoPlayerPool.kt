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

// ─── Pool ────────────────────────────────────────────────────────────────────

/**
 * A fixed pool of [POOL_SIZE] ExoPlayer instances recycled as the user
 * scrolls through reels.
 *
 * Pool layout (always relative to the current settled page):
 *   slot A → previous page        (pre-buffered, paused)
 *   slot B → current page         (playing)
 *   slot C → next page            (pre-buffered, paused)
 *   slot D → next page + 1        (pre-buffered, paused)  ← extended look-ahead
 *
 * 4 players = 1 prev + 1 current + 2 next, matching TikTok / Instagram
 * buffer strategy for smooth scrolling without excessive RAM/battery usage.
 *
 * ### Compose integration
 * - Obtain an instance via [rememberVideoPlayerPool] — it handles lifecycle.
 * - Observe [readyPages] in your composable; it updates as players load.
 */
class VideoPlayerPool(context: Context) {

    // ── Players ──────────────────────────────────────────────────────────────

    private val players: Array<ExoPlayer> = Array(POOL_SIZE) {
        ExoPlayer.Builder(context).build().apply {
            repeatMode    = Player.REPEAT_MODE_ONE
            volume        = 0f
            playWhenReady = false
        }
    }

    /** Which feed-page indices currently have a prepared player. */
    private val _readyPages = mutableStateOf<Set<Int>>(emptySet())
    val readyPages: State<Set<Int>> get() = _readyPages

    // ── Slot bookkeeping ─────────────────────────────────────────────────────

    private val loadedIndex = IntArray(POOL_SIZE) { -1 }
    private var currentSlot = 1

    private val prevSlot  get() = Math.floorMod(currentSlot - 1, POOL_SIZE)
    private val nextSlot  get() = Math.floorMod(currentSlot + 1, POOL_SIZE)
    private val next2Slot get() = Math.floorMod(currentSlot + 2, POOL_SIZE)

    // ── Public API ───────────────────────────────────────────────────────────

    /**
     * Call when the pager settles on [page].
     * Loads neighbouring URLs into idle slots and starts the current player.
     */
    fun onPageChanged(page: Int, urls: List<String>) {
        val assignments = mapOf(
            prevSlot    to page - 1,
            currentSlot to page,
            nextSlot    to page + 1,
            next2Slot   to page + 2,
        )

        assignments.forEach { (slot, feedPage) ->
            if (feedPage in urls.indices) {
                if (loadedIndex[slot] != feedPage) {
                    players[slot].apply {
                        setMediaItem(MediaItem.fromUri(urls[feedPage]))
                        prepare()
                        playWhenReady = false
                        volume        = 0f
                    }
                    loadedIndex[slot] = feedPage
                }
            } else {
                players[slot].stop()
                loadedIndex[slot] = -1
            }
        }

        players[currentSlot].apply {
            volume        = 1f
            playWhenReady = true
        }

        // Publish the updated ready set to Compose observers
        _readyPages.value = loadedIndex.filter { it >= 0 }.toSet()
    }

    /**
     * Rotate the ring buffer by [delta] (+1 scroll forward, -1 scroll back).
     * Call *before* [onPageChanged] when the settled page changes.
     */
    fun rotate(delta: Int) {
        players[currentSlot].apply {
            volume        = 0f
            playWhenReady = false
        }
        currentSlot = Math.floorMod(currentSlot + delta, POOL_SIZE)
    }

    /**
     * Returns the [ExoPlayer] assigned to [page], or `null` if it's outside
     * the ±1 / +2 window or not yet prepared.
     */
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