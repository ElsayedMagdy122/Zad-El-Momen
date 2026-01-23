package dev.sayed.mehrabalmomen.presentation.screen.SurahAyat

interface SurahAyatInteractionListener {
    fun onAyaLongPressed(id: Int, text: String)
    fun onClearSelection()
    fun onCopyAya()
    fun onTafseer()
    fun onClickBack()
    fun onClickSearch()
}