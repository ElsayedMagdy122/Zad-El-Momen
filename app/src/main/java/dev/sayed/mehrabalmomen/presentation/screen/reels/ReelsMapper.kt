package dev.sayed.mehrabalmomen.presentation.screen.reels

import dev.sayed.mehrabalmomen.domain.entity.quranReel.ReelVideoItem

fun ReelVideoItem.toUiState() = ReelItemUiState(
    id                   = id,
    videoUrl             = videoUrl,
    mp4Url               = "https://symytfqhmvhubxikhpxy.supabase.co/storage/v1/object/public/reels/7a8340f4-7823-4234-8c4f-d44e759c9b0d-1779291274451.mp4",
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
