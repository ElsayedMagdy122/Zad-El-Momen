package dev.sayed.mehrabalmomen.domain.entity.quranReel


data class ReelVideoItem(
    val id: Int,
    val videoUrl: String,
    val title: String,
    val ayah: String,
    val surahName : String,
    val sheikhName: String,
    val sheikhAvatarUrl: String,
    val sharesCount: Int,
    val likeCount: Int,
    val isLiked: Boolean,
    val videoMp4Url : String
)
