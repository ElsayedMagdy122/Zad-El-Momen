package dev.sayed.mehrabalmomen.presentation.screen.reels

sealed interface ReelsEffect {

    data class ShareReel(
        val cachedReelUrl: String,
        val title: String,
        val reelId: Int
    ) : ReelsEffect

    data object NavigateBack : ReelsEffect
}
