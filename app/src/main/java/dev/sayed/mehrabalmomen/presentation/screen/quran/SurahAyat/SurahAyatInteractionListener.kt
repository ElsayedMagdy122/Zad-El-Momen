package dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat

interface SurahAyatInteractionListener {
    fun onAyaLongPressed(id: Int, text: String)
    fun onClearSelection()
    fun onCopyAya()
    fun onBookmarkAya()
    fun onTafseer()
    fun onClickBack()
    fun onClickSearch()
    fun onClickTasmee3()
    fun onDismissTasmee3Range()
    fun onConfirmTasmee3Range(startAyah: Int, endAyah: Int)
}