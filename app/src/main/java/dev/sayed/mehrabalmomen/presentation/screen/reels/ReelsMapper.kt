package dev.sayed.mehrabalmomen.presentation.screen.reels

import dev.sayed.mehrabalmomen.domain.entity.quranReel.ReelVideoItem

fun ReelVideoItem.toUiState() = ReelItemUiState(
    id                   = id,
    videoUrl             = videoUrl,
    mp4Url               = videoMp4Url,
    title                = title,
    ayah                 = ayah,
    sheikhName           = sheikhName,
    sheikhAvatarUrl      = sheikhAvatarUrl,
    sharesCount          = sharesCount,
    likeCount            = likeCount,
    likesCountOptimistic = likeCount,
    isLiked              = isLiked,
    isLikedOptimistic    = isLiked,
    surah                = surahName,
)
