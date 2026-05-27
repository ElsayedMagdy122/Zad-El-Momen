package dev.sayed.mehrabalmomen.data.reels.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReelDto(
    val id: Long,
    val title: String,
    val ayah: String,
    @SerialName("surah_name")        val surahName: String,
    @SerialName("sheikh_name")       val sheikhName: String,
    @SerialName("sheikh_avatar_url") val sheikhAvatarUrl: String?,
    @SerialName("video_hls_url")     val videoHlsUrl: String,
    @SerialName("video_mp4_url")     val videoMp4Url: String,
    @SerialName("shares_count")      val sharesCount: Int,
    @SerialName("like_count")        val likeCount: Int,
    @SerialName("is_liked")          val isLiked: Boolean,
)