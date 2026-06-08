package dev.sayed.mehrabalmomen.presentation.screen.reels

interface ReelsInteractionListener {
    fun onShareClicked(reelId: Int)
    fun onBackClicked()
    fun onLoadNextPage()

    fun onLikeReelClicked(reelId: Int)

    fun loadReels()
}
