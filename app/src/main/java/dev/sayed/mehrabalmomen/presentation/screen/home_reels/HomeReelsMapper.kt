package dev.sayed.mehrabalmomen.presentation.screen.home_reels

import dev.sayed.mehrabalmomen.domain.entity.quranReel.ReelVideoItem

fun ReelVideoItem.toPreviewUiState() = HomeReelsUiState.ReelPreviewUiState(
    id = id,
    sheikhAvatarUrl = sheikhAvatarUrl,
    sheikhName = sheikhName,
    surahName = surahName,
    thumbnailUrl = thumbnailUrl,
    ayah = ayah,
    isLiked = isLiked,
    likesCount = likeCount,
    sharesCount = sharesCount,
    likesCountOptimistic = likeCount,
    isLikedOptimistic = isLiked,
    mp4Url = videoMp4Url,
    surah = surahName,
    isSharing = false,
    downloadPercentage = 0,
)