package dev.sayed.mehrabalmomen.presentation.screen.quran

import dev.sayed.mehrabalmomen.domain.repository.QuranRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.delay

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
            onStart = { updateState { it.copy(isLoading = true) } },
            block = { quranRepository.getSurahs().map { it.toUiState() } },
            onSuccess = { surahs ->
                updateState { it.copy(surahList = surahs) }
                delay(100)
                updateState { it.copy(isLoading = false) }
            },
            onError = {}
        )
    }

    override fun onSurahClick(surahId: Int,surahName:String) {
        sendEffect(SurahListEffect.NavigateToSurahAyat(surahId,surahName))
    }

    override fun onSearchClick() {
        sendEffect(SurahListEffect.NavigateToQuranSearch)
    }
}