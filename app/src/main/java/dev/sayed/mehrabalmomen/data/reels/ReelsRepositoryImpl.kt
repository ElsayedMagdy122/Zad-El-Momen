package dev.sayed.mehrabalmomen.data.reels

import android.content.Context
import android.util.Log
import androidx.core.content.FileProvider
import dev.sayed.mehrabalmomen.data.reels.dto.ReelDto
import dev.sayed.mehrabalmomen.data.reels.mapper.toReelVideoItem
import dev.sayed.mehrabalmomen.data.util.AnonymousAuthManager
import dev.sayed.mehrabalmomen.domain.model.LikeResult
import dev.sayed.mehrabalmomen.domain.entity.quranReel.ReelVideoItem
import dev.sayed.mehrabalmomen.domain.model.ShareResult
import dev.sayed.mehrabalmomen.domain.repository.ReelsRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.utils.io.readAvailable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.io.File


class ReelsRepositoryImpl(
    private val context: Context,
    private val supabase: SupabaseClient,
    private val httpClient: HttpClient,
    private val anonymousAuthManager: AnonymousAuthManager
) : ReelsRepository {

    override suspend fun getReels(pageNumber: Int, pageSize: Int): List<ReelVideoItem> {
        val offset = (pageNumber - 1) * pageSize
        val userId = anonymousAuthManager.getUserId()
        val reels = supabase.postgrest
            .rpc(
                function = "get_reels_with_liked_status",
                parameters = buildJsonObject {
                    put("p_user_id", userId)
                    put("p_limit", pageSize)
                    put("p_offset", offset)
                }
            ).decodeList<ReelDto>()

        return reels.map { it.toReelVideoItem() }
    }

    override suspend fun likeReel(reelId: Int): LikeResult {
        val userId = anonymousAuthManager.getUserId()

        supabase.postgrest
            .from("reel_likes")
            .insert(
                buildJsonObject {
                    put("reel_id", reelId.toLong())
                    put("user_id", userId)
                }
            )

        val newCount = supabase.postgrest
            .rpc(
                "increment_like_count",
                buildJsonObject { put("reel_id_input", reelId.toLong()) }
            ).data.trimOrNull()?.toIntOrNull() ?: 0

        return LikeResult(likesCount = newCount, isLiked = true)
    }

    override suspend fun unlikeReel(reelId: Int): LikeResult {
        val userId = anonymousAuthManager.getUserId()

        supabase.postgrest
            .from("reel_likes")
            .delete {
                filter {
                    eq("reel_id", reelId.toLong())    // ← bigint
                    eq("user_id", userId)
                }
            }

        val newCount = supabase.postgrest
            .rpc(
                "decrement_like_count",
                buildJsonObject { put("reel_id_input", reelId.toLong()) }
            ).data.trimOrNull()?.toIntOrNull() ?: 0

        return LikeResult(likesCount = newCount, isLiked = false)
    }

    // ── 4. Cache / download MP4 ───────────────────────────────────────────────
    override suspend fun cacheReelVideo(
        reelMp4Url: String,
        cacheVideoName: String,
        onProgress: (Int) -> Unit,
    ): String {

        val cacheDir = File(context.cacheDir, "shared_reels").also { it.mkdirs() }
        val file = File(cacheDir, "$cacheVideoName.mp4")

        httpClient.prepareGet(reelMp4Url).execute { response ->

            val contentLength = response.contentLength() ?: -1L
            val channel = response.bodyAsChannel()

            val buffer = ByteArray(64 * 1024)
            var downloadedBytes = 0L
            var lastProgress = 0

            file.outputStream().buffered().use { out ->

                while (!channel.isClosedForRead) {

                    val bytesRead = channel.readAvailable(buffer)

                    if (bytesRead > 0) {

                        out.write(buffer, 0, bytesRead)

                        downloadedBytes += bytesRead

                        if (contentLength > 0) {

                            val progress =
                                ((downloadedBytes * 100) / contentLength).toInt()

                            if (progress != lastProgress) {
                                lastProgress = progress
                                onProgress(progress)
                            }
                        }
                    }
                }
            }
        }

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        ).toString()
    }
    // ── Helper ────────────────────────────────────────────────────────────────
    private fun String.trimOrNull(): String? = trim().takeIf { it != "null" && it.isNotEmpty() }

    override suspend fun recordShare(reelId: Int): ShareResult {
        val userId = anonymousAuthManager.getUserId()

        val newCount = supabase.postgrest
            .rpc(
                "record_reel_share",
                buildJsonObject {
                    put("p_reel_id", reelId.toLong())
                    put("p_user_id", userId)
                }
            ).data.trimOrNull()?.toIntOrNull() ?: 0

        return ShareResult(sharesCount = newCount)
    }
}