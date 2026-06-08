package dev.sayed.mehrabalmomen.domain.repository.quranReel

import dev.sayed.mehrabalmomen.domain.entity.quranReel.ReelVideoItem
import dev.sayed.mehrabalmomen.domain.model.LikeResult
import dev.sayed.mehrabalmomen.domain.model.ShareResult

interface ReelsRepository {
   suspend fun getReels(pageNumber : Int , pageSize : Int ,firstReelId : Int? = null): List<ReelVideoItem>

    suspend fun likeReel(reelId: Int): LikeResult

    suspend fun unlikeReel(reelId: Int): LikeResult

    suspend fun recordShare(reelId: Int): ShareResult

    suspend fun cacheReelVideo(
        reelMp4Url: String,
        cacheVideoName: String,
        onProgress: (Int) -> Unit
    ): String
}