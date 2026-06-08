package dev.sayed.mehrabalmomen.presentation.screen.home_reels

data class HomeReelsUiState(
    val reelsPreviewItems: List<ReelPreviewUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasReachedEnd: Boolean = false,
    val currentPage: Int = 1,
) {
    data class ReelPreviewUiState(
        val id : Int,
        val sheikhAvatarUrl: String,
        val sheikhName: String,
        val mp4Url: String,
        val surahName: String,
        val thumbnailUrl: String,
        val ayah: String,

        val surah: String,
        val isLiked: Boolean,
        val likesCount: Int,
        val sharesCount: Int,
        val likesCountOptimistic: Int,
        val isLikedOptimistic: Boolean = false,
        val isSharing: Boolean = false,
        val downloadPercentage : Int = 0
        )
}
