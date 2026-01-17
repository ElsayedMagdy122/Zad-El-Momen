package dev.sayed.mehrabalmomen.presentation.screen.quran

import dev.sayed.mehrabalmomen.domain.repository.QuranRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel

class SurahListViewModel(
    private val quranRepository: QuranRepository
) : BaseViewModel<SurahListUiState, SurahListEffect>(
    SurahListUiState()
), SurahListInteractionListener {

    init {
        loadSurahs()
    }

    private fun loadSurahs() {
        tryToCall(
            block = { quranRepository.getSurahs().map { it.toUiState() } },
            onSuccess = { surahs ->
                updateState { it.copy(surahList = surahs) }
            },
            onError = {}
        )
    }

    override fun onSurahClick(surahId: Int,surahName:String) {
        sendEffect(SurahListEffect.NavigateToSurahAyat(surahId,surahName))
    }
}