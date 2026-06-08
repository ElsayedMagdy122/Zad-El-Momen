package dev.sayed.mehrabalmomen.data.reels.mapper

import dev.sayed.mehrabalmomen.data.reels.dto.ReelDto
import dev.sayed.mehrabalmomen.domain.entity.quranReel.ReelVideoItem

fun ReelDto.toReelVideoItem() = ReelVideoItem(
    id = id.toInt(),
    videoUrl = videoHlsUrl,
    title = title,
    ayah = ayah,
    sheikhName = sheikhName,
    sheikhAvatarUrl = sheikhAvatarUrl ?: "",
    sharesCount = sharesCount,
    likeCount = likeCount,
    isLiked = isLiked,
    surahName = surahName,
    videoMp4Url = videoMp4Url,
    thumbnailUrl = thumbnailUrl
)
