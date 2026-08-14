package dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat

interface SurahAyatInteractionListener {
    fun onAyaLongPressed(id: Int, text: String)
    fun onClearSelection()
    fun onCopyAya()
    fun onBookmarkAya()
    fun onTafseer()
    fun onClickBack()
    fun onClickSearch()
}