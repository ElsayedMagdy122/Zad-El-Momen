package dev.sayed.mehrabalmomen.presentation.screen.reels

data class ReelsUiState(
    val reels: List<ReelItemUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isPaginating: Boolean = false,
    val hasMore: Boolean = true,
    val isError: Boolean = false,
    val currentPage: Int = 1
)

data class ReelItemUiState(
    val id: Int,
    val videoUrl: String,
    val mp4Url: String,
    val title: String,
    val ayah: String,
    val sheikhName: String,
    val sheikhAvatarUrl: String,
    val sharesCount: Int,
    val likeCount: Int,
    val surah: String,
    val likesCountOptimistic: Int,
    val isLiked: Boolean = false,
    val isLikedOptimistic: Boolean = false,
    val isSharing: Boolean = false,
    val downloadPercentage: Int = 0
)
