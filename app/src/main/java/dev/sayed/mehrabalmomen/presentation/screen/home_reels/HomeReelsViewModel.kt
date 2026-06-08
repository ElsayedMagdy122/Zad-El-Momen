package dev.sayed.mehrabalmomen.presentation.screen.home_reels

import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.domain.model.LikeResult
import dev.sayed.mehrabalmomen.domain.repository.quranReel.ReelsRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel

class HomeReelsViewModel(private val reelsRepository: ReelsRepository) :
    BaseViewModel<HomeReelsUiState, HomeReelsEffect>(HomeReelsUiState()),
    HomeReelsInteractionListener {

    init {
        loadReelsPreview()
    }
    private companion object {
         const val PAGE_SIZE = 20
    }
    override fun loadReelsPreview() {
        tryToCall(
            onStart = { updateState { it.copy(isLoading = true, isError = false) } },
            block = {
                reelsRepository.getReels(pageNumber = 1, pageSize = PAGE_SIZE) },
            onSuccess = { items ->
                updateState {
                    it.copy(
                        reelsPreviewItems = items.map { r -> r.toPreviewUiState() },
                        isLoading = false,
                        isError = false
                    )
                }
            },
            onError = {
                updateState { it.copy(isLoading = false, isError = true) }
            }
        )
    }

    override fun loadMoreReels() {
        val state = screenState.value
        if (state.isLoadingMore || state.hasReachedEnd || state.isLoading) return

        val nextPage = state.currentPage + 1

        tryToCall(
            onStart = { updateState { it.copy(isLoadingMore = true) } },
            block = {
                reelsRepository.getReels(pageNumber = nextPage, pageSize = PAGE_SIZE)
            },
            onSuccess = { items ->
                updateState {
                    it.copy(
                        reelsPreviewItems = it.reelsPreviewItems + items.map { it.toPreviewUiState() },
                        isLoadingMore = false,
                        currentPage = nextPage,
                        hasReachedEnd = items.size < PAGE_SIZE,
                    )
                }
            },
            onError = {
                updateState { it.copy(isLoadingMore = false) }
            }
        )
    }

    override fun onLikeClick(reelId: Int) {
        val item = screenState.value.reelsPreviewItems.find { it.id == reelId } ?: return

        val optimisticIsLiked = !item.isLikedOptimistic
        val optimisticCount = if (optimisticIsLiked) item.likesCountOptimistic + 1
        else item.likesCountOptimistic - 1

        updateState { current ->
            current.copy(reelsPreviewItems = current.reelsPreviewItems.map {
                if (it.id == reelId) it.copy(
                    isLikedOptimistic = optimisticIsLiked,
                    likesCountOptimistic = optimisticCount,
                ) else it
            })
        }

        val action: suspend () -> LikeResult =
            if (item.isLikedOptimistic) {
                { reelsRepository.unlikeReel(reelId = reelId) }
            } else {
                { reelsRepository.likeReel(reelId = reelId) }
            }

        tryToCall(
            block = { action() },
            onSuccess = { result ->
                updateState {
                    it.copy(reelsPreviewItems = it.reelsPreviewItems.map { reel ->
                        if (reel.id == reelId) reel.copy(
                            isLiked = result.isLiked,
                            likesCount = result.likesCount,
                        ) else reel
                    })
                }
            },
            onError = { error ->
                updateState { current ->
                    current.copy(reelsPreviewItems = current.reelsPreviewItems.map {
                        if (it.id == reelId) it.copy(
                            isLikedOptimistic = item.isLiked,
                            likesCountOptimistic = item.likesCount,
                        ) else it
                    })
                }
            },
        )
    }

    override fun onShareClick(reelId : Int) {
        val reel = screenState.value.reelsPreviewItems.find { it.id == reelId } ?: return
        if (reel.isSharing) return

        updateState { current ->
            current.copy(reelsPreviewItems = current.reelsPreviewItems.map {
                if (it.id == reelId) it.copy(isSharing = true) else it
            })
        }
        sendEffect(
            HomeReelsEffect.ShowMessage(
                titleResId = R.string.preparing_video,
                messageResId = R.string.please_wait_while_we_prepare_video
            )
        )

        tryToCall(
            block = {
                reelsRepository.cacheReelVideo(reel.mp4Url, cacheVideoName = "reel_${reel.id}") { downloadPercentage ->
                    updateState {
                        it.copy(reelsPreviewItems = it.reelsPreviewItems.map {
                            if (it.id == reelId) it.copy(downloadPercentage = downloadPercentage) else it
                        })
                    }
                }
            },
            onSuccess = { cachedReelUrl ->
                sendEffect(
                    HomeReelsEffect.ShareReel(
                        cachedReelUrl = cachedReelUrl,
                        title = reel.surah,
                        reelId = reelId
                    )
                )
            },
            onError = {
                sendEffect(HomeReelsEffect.Error(
                    titleResId = R.string.failed_to_cache_video,
                    messageResId = R.string.failed_to_share_message,
                ))
            },
            onEnd = {
                updateState { current ->
                    current.copy(reelsPreviewItems = current.reelsPreviewItems.map {
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
                    current.copy(reelsPreviewItems = current.reelsPreviewItems.map {
                        if (it.id == reelId) it.copy(sharesCount = result.sharesCount) else it
                    })
                }
            },
            onError = {},
        )
    }

    override fun onThumbnailClick(reelId: Int) {
        sendEffect(HomeReelsEffect.NavigateToReelsScreen(reelId))
    }

}