package dev.sayed.mehrabalmomen.presentation.screen.quran

import android.annotation.SuppressLint
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import dev.sayed.mehrabalmomen.presentation.utils.AnalyticsHelper
import kotlinx.coroutines.delay

class SurahListViewModel(
    private val quranRepository: QuranRepository,
    private val analyticsHelper: AnalyticsHelper
) : BaseViewModel<SurahListUiState, SurahListEffect>(
    SurahListUiState()
), SurahListInteractionListener {

    init {
        loadSurahs()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun loadSurahs() {
        tryToCall(
            onStart = { updateState { it.copy(isLoading = true) } },
            block = {
                val tafseer = quranRepository.getAyahTafseer(114, 1)
                println("TAfseer : ${tafseer}")
                quranRepository.getSurahs().map { it.toUiState() }
            },
            onSuccess = { surahs ->
                updateState { it.copy(surahList = surahs) }
                delay(100)
                updateState { it.copy(isLoading = false) }
            },
            onError = {}
        )
    }
    fun onScreenOpened() {
        analyticsHelper.logScreen("surah list")
    }
    override fun onSurahClick(surahId: Int, arabicName: String, englishName: String) {
        analyticsHelper.logEvent(
            name = "on click surah",
            params = mapOf(
                "surah_id" to surahId.toString(),
                "surah_name" to arabicName,
            )
        )
        sendEffect(SurahListEffect.NavigateToSurahAyat(surahId, arabicName,englishName))
    }

    override fun onSearchClick() {
        analyticsHelper.logEvent(
            name = "on click search"
        )
        sendEffect(SurahListEffect.NavigateToQuranSearch)
    }

    override fun onBookmarksClick() {
        analyticsHelper.logEvent(
            name = "on click bookmarks"
        )
        sendEffect(SurahListEffect.NavigateToBookmarksList)
    }
}