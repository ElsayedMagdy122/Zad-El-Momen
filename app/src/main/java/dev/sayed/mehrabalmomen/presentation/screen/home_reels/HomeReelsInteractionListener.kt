package dev.sayed.mehrabalmomen.presentation.screen.home_reels

interface HomeReelsInteractionListener {
    fun onLikeClick(reelId : Int)
    fun onShareClick(reelId : Int)
    fun onThumbnailClick(reelId : Int)

    fun loadReelsPreview()
    fun loadMoreReels()
}