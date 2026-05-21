package dev.sayed.mehrabalmomen.data.reels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReelLikeDto(

    @SerialName("reel_id")
    val reelId: Long,

    @SerialName("user_id")
    val userId: String
)