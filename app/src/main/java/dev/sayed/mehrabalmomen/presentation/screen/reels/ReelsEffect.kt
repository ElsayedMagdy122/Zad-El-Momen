package dev.sayed.mehrabalmomen.presentation.screen.reels

sealed interface ReelsEffect {

    data class ShareReel(
        val cachedReelUrl: String,
        val title: String,
        val reelId: Int
    ) : ReelsEffect

    data object NavigateBack : ReelsEffect

    data class Error(val titleResId : Int,val messageResId: Int) : ReelsEffect
    data class ShowMessage(val titleResId : Int,val messageResId: Int) : ReelsEffect
}
