package dev.sayed.mehrabalmomen.presentation.screen.SurahAyat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.domain.entity.quran.Bookmark
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.domain.repository.quran.BookmarkRepository
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranRepository
import dev.sayed.mehrabalmomen.domain.repository.quran.ReadingProgressRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SurahAyatViewModel(
    private val quranRepository: QuranRepository,
    private val readingProgressRepository: ReadingProgressRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val settingsRepository: SettingsRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<SurahAyatUiState, SurahAyatEffect>(
    SurahAyatUiState()
), SurahAyatInteractionListener {

    private val surahId: Int = checkNotNull(savedStateHandle["surahId"])
    private val arabicName: String = checkNotNull(savedStateHandle["arabicName"])
    private val englishName: String = checkNotNull(savedStateHandle["englishName"])
    private val targetAyahId: Int? = savedStateHandle["targetAyahId"]

    init {
        observeReadingMode()
        loadSurahAyat()
        observeFontSize()
    }

    private fun observeReadingMode() {
        viewModelScope.launch {
            settingsRepository.observeReadingMode().collect { readingModeName ->
                val selectedReadingMode =
                    AppSettings.ReadingMode.entries.first { it.name == readingModeName }
                updateState {
                    it.copy(
                        readingMode = selectedReadingMode,
                    )
                }
            }
        }
    }

    fun onAyahVisible(ayahId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            readingProgressRepository.save(
                surahId = surahId,
                ayahId = ayahId
            )
        }
    }

    private fun observeFontSize() {
        viewModelScope.launch {
            settingsRepository.observeQuranFontSize().collect { size ->

                val font = QuranFontSize.entries
                    .firstOrNull { it.sizeSp == size }
                    ?: QuranFontSize.MEDIUM

                updateState {
                    it.copy(fontSize = font)
                }
            }
        }
    }

    private fun loadSurahAyat() {
        tryToCall(
            onStart = {
                updateState { state -> state.copy(isLoading = true) }
            },
            block = { quranRepository.getAyahs(surahId) },
            onSuccess = { ayat ->
                delay(200)
                val uiAyat = ayat.map { AyaUi(it.ayahNumber, it.page, it.text) }
                val targetPage = calculateTargetPage(uiAyat, targetAyahId)
                val ayatPerPages = uiAyat.groupBy(AyaUi::page)
                updateState {
                    it.copy(
                        ayat = uiAyat,
                        ayatPerPages = ayatPerPages,
                        pageNumbers = ayatPerPages.keys.toList(),
                        currentPage = targetPage,
                        isLoading = false,
                        arabicName = arabicName,
                        englishName = englishName,
                        selectedAyaId = targetAyahId,
                        scrollToAyaId = targetAyahId,
                        targetAyahId = targetAyahId,
                    )
                }
            },
            onError = {}
        )
    }

    private fun calculateTargetPage(
        ayat: List<AyaUi>,
        targetAyahId: Int?
    ): Int {
        return ayat.firstOrNull { it.id == targetAyahId }?.page ?: ayat.first().page
    }

    fun onScrolledToTarget() {
        updateState {
            it.copy(
                targetAyahId = null,
                scrollToAyaId = null
            )
        }
    }

    override fun onAyaLongPressed(id: Int, text: String) {
        updateState {
            it.copy(
                selectedAyaId = id,
                selectedAyaText = text,
                showActions = true
            )
        }
    }

    override fun onClearSelection() {
        updateState {
            it.copy(
                selectedAyaId = null,
                selectedAyaPage = null,
                selectedAyaText = "",
                showActions = false
            )
        }
    }

    override fun onCopyAya() {
        val text = screenState.value.selectedAyaText
        if (text.isBlank()) return

        sendEffect(SurahAyatEffect.CopyAya(text))
        onClearSelection()
        sendEffect(
            SurahAyatEffect.ShowToast(
                ToastDetails(
                    title = R.string.success,
                    message = R.string.copied_message_successfully,
                    icon = R.drawable.ic_check_circle
                )
            )
        )
    }

    override fun onBookmarkAya() {
        val ayahId = screenState.value.selectedAyaId ?: return
        val ayahText = screenState.value.selectedAyaText
        if (ayahText.isBlank()) return

        tryToCall(
            onStart = {
                updateState { it.copy(showActions = false) }
            },
            block = {
                bookmarkRepository.addBookmark(
                    Bookmark(
                        surahId = surahId,
                        ayahId = ayahId,
                        arabicName = arabicName,
                        englishName = englishName,
                        text = ayahText
                    )
                )
            },
            onSuccess = {
                onClearSelection()
                sendEffect(
                    SurahAyatEffect.ShowToast(
                        ToastDetails(
                            title = R.string.success,
                            message = R.string.ayah_bookmarked_message_successfully,
                            icon = R.drawable.ic_check_circle
                        )
                    )
                )
            },
            onError = {}
        )
    }

    override fun onTafseer() {
        val ayahId = screenState.value.selectedAyaId ?: return
        val ayaPage = screenState.value.selectedAyaPage ?: return
        val ayaText = screenState.value.selectedAyaText

        tryToCall(
            onStart = {
                updateState {
                    it.copy(
                        showTafseerSheet = true,
                        tafseerUi = null,
                        showActions = false
                    )
                }
            },
            block = {
                quranRepository.getAyahTafseer(surahId, ayahId)
            },
            onSuccess = { tafseer ->
                updateState {
                    it.copy(
                        tafseerUi = TafseerUi(
                            ayahUi = AyaUi(id = ayahId, page = ayaPage, text = ayaText),
                            text = tafseer
                        )
                    )
                }
            },
            onError = {
                updateState { it.copy(showTafseerSheet = false) }
            }
        )
    }

    fun onDismissTafseerSheet() {
        updateState {
            it.copy(
                showTafseerSheet = false,
                tafseerUi = null,
                selectedAyaId = null,
                selectedAyaText = "",
                showActions = false
            )
        }
    }

    override fun onClickBack() {
        sendEffect(SurahAyatEffect.NavigateToBack)
    }

    override fun onClickSearch() {
        sendEffect(
            SurahAyatEffect.NavigateToSearch(
                surahId = surahId,
                arabicName = screenState.value.arabicName,
                englishName = screenState.value.englishName
            )
        )
    }

    override fun onPageChange(page: Int) {
        updateState {
            it.copy(
                ayat = it.ayatPerPages[page] ?: emptyList(),
                currentPage = page,
                showActions = false
            )
        }
    }
}