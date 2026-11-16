package dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat

import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel

class SurahAyatViewModel : BaseViewModel<SurahAyatUiState, SurahAyatEffect>(SurahAyatUiState()), SurahAyatInteractionListener {
    override fun onAyaLongPressed(id: Int, text: String) {}
    override fun onClearSelection() {}
    override fun onCopyAya() {}
    override fun onBookmarkAya() {}
    override fun onTafseer() {}
    override fun onClickBack() {}
    override fun onClickSearch() {}
    override fun onClickTasmee3() {}
    override fun onDismissTasmee3Range() {}
    override fun onConfirmTasmee3Range(startAyah: Int, endAyah: Int) {}
}
