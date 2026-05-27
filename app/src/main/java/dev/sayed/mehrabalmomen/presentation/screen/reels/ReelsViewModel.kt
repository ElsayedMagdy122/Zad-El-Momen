package dev.sayed.mehrabalmomen.presentation.screen.reels

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.domain.model.LikeResult
import dev.sayed.mehrabalmomen.domain.repository.ReelsRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.launch

class ReelsViewModel(
    private val reelsRepository: ReelsRepository,
) : BaseViewModel<ReelsUiState, ReelsEffect>(ReelsUiState()), ReelsInteractionListener {

    private var currentPage = 1
    private val pageSize = 10

    init {
        loadReels()
    }
    private fun loadReels() {
        tryToCall(
            onStart = { updateState { it.copy(isLoading = true, error = null) }},
            block = {reelsRepository.getReels(
                pageNumber = currentPage,
                pageSize = pageSize
            )},
            onSuccess = {items->
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
            }
        )
    }

    override fun onLoadNextPage() {
        if (screenState.value.isPaginating || !screenState.value.hasMore) return
        viewModelScope.launch {
            updateState { it.copy(isPaginating = true) }
            try {
                currentPage++
                val items = reelsRepository.getReels(
                    pageNumber = currentPage,
                    pageSize = pageSize
                )
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
                { reelsRepository.unlikeReel(reelId = itemId) }
            } else {
                { reelsRepository.likeReel(reelId = itemId) }
            }

        tryToCall(
            block = { action() },
            onSuccess = { result ->
                Log.d("ReelsViewModel", "like: ${result.isLiked} ${result.likesCount}")
                updateState {
                    it.copy(reels = it.reels.map { reel ->
                        if (reel.id == itemId) reel.copy(
                            isLiked = result.isLiked,
                            likeCount = result.likesCount,
                        ) else reel
                    })
                }
            },
            onError = {error->
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
        sendEffect(
            ReelsEffect.ShowMessage(
                titleResId = R.string.preparing_video,
                messageResId = R.string.please_wait_while_we_prepare_video
            )
        )

        tryToCall(
            block = {
                reelsRepository.cacheReelVideo(reel.mp4Url, cacheVideoName = "reel_${reel.id}") { downloadPercentage ->
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
                sendEffect(ReelsEffect.Error(
                    titleResId = R.string.failed_to_cache_video,
                    messageResId = R.string.failed_to_share_message,
                ))
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

    fun onShareCompleted(reelId: Int, shared: Boolean) {
        if (!shared) return

        tryToCall(
            block = { reelsRepository.recordShare(reelId) },
            onSuccess = { result ->
                updateState { current ->
                    current.copy(reels = current.reels.map {
                        if (it.id == reelId) it.copy(sharesCount = result.sharesCount) else it
                    })
                }
            },
            onError = {},
        )
    }

    override fun onBackClicked() = sendEffect(ReelsEffect.NavigateBack)
}
