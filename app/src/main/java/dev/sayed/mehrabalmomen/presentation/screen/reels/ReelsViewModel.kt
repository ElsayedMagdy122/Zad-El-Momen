package dev.sayed.mehrabalmomen.presentation.screen.reels

import android.util.Log
import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.data.util.AnonymousAuthManager
import dev.sayed.mehrabalmomen.domain.entity.quranReel.LikeResult
import dev.sayed.mehrabalmomen.domain.repository.ReelsRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.launch

class ReelsViewModel(
    private val reelsRepository: ReelsRepository,
    private val authManager: AnonymousAuthManager
) : BaseViewModel<ReelsUiState, ReelsEffect>(ReelsUiState()), ReelsInteractionListener {

    private var currentPage = 1
    private val pageSize = 10

    init {
      userSession()
    }
    private fun userSession() {
        tryToCall(
            block = {
                authManager.ensureAnonymousLogin()
            },
            onSuccess = {
                loadReels()
            },
            onError = { error ->
                Log.d("ReelsViewModel", "Auth Session error: ${error}")
                loadReels()
            }
        )
    }
    private fun loadReels() {
        tryToCall(
            onStart = { updateState { it.copy(isLoading = true, error = null) }},
            block = {reelsRepository.getReels()},
            onSuccess = {items->
                Log.d("ReelsViewModel", "loadReels: $items")
                updateState {
                    it.copy(
                        reels = items.map { r -> r.toUiState() },
                        isLoading = false,
                        hasMore = items.size == pageSize,
                    )
                }
            },
            onError = {error->
                updateState { it.copy(isLoading = false, error = error.message) }
                Log.d("ReelsViewModel", "error: ${error.message}")
            }
        )
    }

    override fun onLoadNextPage() {
        if (screenState.value.isPaginating || !screenState.value.hasMore) return
        viewModelScope.launch {
            updateState { it.copy(isPaginating = true) }
            try {
                currentPage++
                val items = reelsRepository.getReels()
                updateState {
                    it.copy(
                        reels = it.reels + items.map { it.toUiState() },
                        isPaginating = false,
                        hasMore = items.size == pageSize,
                    )
                }
            } catch (e: Exception) {
                currentPage--
                updateState { it.copy(isPaginating = false) }
            }
        }
    }

    override fun onLikeReelClicked(itemId: Int) {
        val item = screenState.value.reels.find { it.id == itemId } ?: return

        val optimisticIsLiked = !item.isLikedOptimistic
        val optimisticCount = if (optimisticIsLiked) item.likesCountOptimistic + 1
        else item.likesCountOptimistic - 1

        updateState { current ->
            current.copy(reels = current.reels.map {
                if (it.id == itemId) it.copy(
                    isLikedOptimistic = optimisticIsLiked,
                    likesCountOptimistic = optimisticCount,
                ) else it
            })
        }

        val action: suspend () -> LikeResult =
            if (item.isLikedOptimistic) {
                { reelsRepository.unlikeReel(reelId = itemId.toLong()) }
            } else {
                { reelsRepository.likeReel(reelId = itemId.toLong()) }
            }

        tryToCall(
            block = { action() },
            onSuccess = { result ->
                updateState {
                    it.copy(reels = it.reels.map { reel ->
                        if (reel.id == itemId) reel.copy(
                            isLikedOptimistic = result.isLiked,
                            likesCountOptimistic = result.likesCount,
                            isLiked = result.isLiked,
                            likeCount = result.likesCount,
                        ) else reel
                    })
                }
            },
            onError = {error->
                Log.d("ReelsViewModel", "like: ${error}")
                updateState { current ->
                    current.copy(reels = current.reels.map {
                        if (it.id == itemId) it.copy(
                            isLikedOptimistic = item.isLiked,
                            likesCountOptimistic = item.likeCount,
                        ) else it
                    })
                }
            },
        )
    }

    override fun onShareClicked(reelId: Int) {
        val reel = screenState.value.reels.find { it.id == reelId } ?: return
        if (reel.isSharing) return

        updateState { current ->
            current.copy(reels = current.reels.map {
                if (it.id == reelId) it.copy(isSharing = true) else it
            })
        }

        tryToCall(
            block = {
                reelsRepository.cacheReelVideo(reel.mp4Url, reel.surah) { downloadPercentage ->
                    updateState {
                        it.copy(reels = it.reels.map {
                            if (it.id == reelId) it.copy(downloadPercentage = downloadPercentage) else it
                        })
                    }
                }
            },
            onSuccess = { cachedReelUrl ->
                sendEffect(
                    ReelsEffect.ShareReel(
                        cachedReelUrl = cachedReelUrl,
                        title = reel.surah,
                        reelId = reelId
                    )
                )
            },
            onError = {

            },
            onEnd = {
                updateState { current ->
                    current.copy(reels = current.reels.map {
                        if (it.id == reelId) it.copy(isSharing = false) else it
                    })
                }
            }
        )
    }

    /** Called by the screen once the download+share flow finishes (success or error). */
    fun onShareCompleted(reelId: Int, shared: Boolean) {
        updateState { current ->
            current.copy(reels = current.reels.map {
                if (it.id == reelId) it.copy(
                    sharesCount = if (shared) it.sharesCount + 1 else it.sharesCount,
                ) else it
            })
        }
        // will fire request to increase shared count if it success
    }

    override fun onBackClicked() = sendEffect(ReelsEffect.NavigateBack)
}
