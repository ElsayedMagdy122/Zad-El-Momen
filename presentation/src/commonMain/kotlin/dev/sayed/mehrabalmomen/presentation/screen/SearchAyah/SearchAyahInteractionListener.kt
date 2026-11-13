package dev.sayed.mehrabalmomen.presentation.screen.SearchAyah

interface SearchAyahInteractionListener {
    fun onSearchQueryChange(query: String)
    fun onBackClick()
    fun onAyahClick(ayah: AyahUi)
}