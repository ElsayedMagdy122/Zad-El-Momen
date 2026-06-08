package dev.sayed.mehrabalmomen.presentation.screen.home_reels

sealed interface HomeReelsEffect {

    data class ShareReel(
        val cachedReelUrl: String,
        val title: String,
        val reelId: Int
    ) : HomeReelsEffect

    data class NavigateToReelsScreen(val reelId : Int) : HomeReelsEffect

    data class ShowMessage(val titleResId : Int,val messageResId: Int) : HomeReelsEffect

    data class Error(val titleResId : Int,val messageResId: Int) : HomeReelsEffect

}