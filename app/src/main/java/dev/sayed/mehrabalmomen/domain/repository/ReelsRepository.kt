package dev.sayed.mehrabalmomen.domain.repository

import dev.sayed.mehrabalmomen.domain.entity.quranReel.LikeResult
import dev.sayed.mehrabalmomen.domain.entity.quranReel.ReelVideoItem

interface ReelsRepository {
   // suspend fun getReels(page: Int, pageSize: Int): List<ReelVideoItem>
   suspend fun getReels(): List<ReelVideoItem>

    suspend fun likeReel(reelId: Long): LikeResult

    suspend fun unlikeReel(reelId: Long): LikeResult

    suspend fun cacheReelVideo(
        reelMp4Url: String,
        cacheVideoName: String,
        onProgress: (Int) -> Unit
    ): String
}
