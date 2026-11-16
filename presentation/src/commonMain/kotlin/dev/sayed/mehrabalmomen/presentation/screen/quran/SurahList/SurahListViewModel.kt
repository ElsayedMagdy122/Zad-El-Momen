package dev.sayed.mehrabalmomen.presentation.screen.quran.SurahList

import dev.sayed.mehrabalmomen.domain.analytics.AnalyticsTracker
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.delay

class SurahListViewModel(
    private val quranRepository: QuranRepository,
    private val analyticsTracker: AnalyticsTracker
) : BaseViewModel<SurahListUiState, SurahListEffect>(SurahListUiState()),
    SurahListInteractionListener {

    init {
        loadSurahs()
    }

    private fun loadSurahs() {
        tryToCall(
            onStart = { updateState { it.copy(isLoading = true) } },
            block = { quranRepository.getSurahs() },
            onSuccess = { surahs ->
                updateState { it.copy(surahList = surahs.map { it.toUiState() }) }
                delay(100)
                updateState { it.copy(isLoading = false) }
            },
            onError = { updateState { it.copy(isLoading = false) } }
        )
    }

    fun onScreenOpened() {
        analyticsTracker.logScreen("surah list")
    }

    override fun onSurahClick(surahId: Int, arabicName: String, englishName: String) {
        analyticsTracker.logEvent(
            name = "on click surah",
            params = mapOf(
                "surah_id" to surahId.toString(),
                "surah_name" to englishName
            )
        )
        sendEffect(SurahListEffect.NavigateToSurahAyat(surahId, arabicName, englishName))
    }

    override fun onSearchClick() {
        analyticsTracker.logEvent(
            name = "on click search surah"
        )
        sendEffect(SurahListEffect.NavigateToQuranSearch)
    }

    override fun onBookmarksClick() {
        analyticsTracker.logEvent(
            name = "on click bookmarks"
        )
        sendEffect(SurahListEffect.NavigateToBookmarksList)
    }
}