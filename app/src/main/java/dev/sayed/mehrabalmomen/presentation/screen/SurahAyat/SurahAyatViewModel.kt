package dev.sayed.mehrabalmomen.presentation.screen.SurahAyat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.domain.entity.Bookmark
import dev.sayed.mehrabalmomen.domain.repository.BookmarkRepository
import dev.sayed.mehrabalmomen.domain.repository.ContinueTilawahRepository
import dev.sayed.mehrabalmomen.domain.repository.QuranRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SurahAyatViewModel(
    private val quranRepository: QuranRepository,
    private val continueTilawahRepository: ContinueTilawahRepository,
    private val bookmarkRepository: BookmarkRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<SurahAyatUiState, SurahAyatEffect>(
    SurahAyatUiState()
), SurahAyatInteractionListener {

    private val surahId: Int = checkNotNull(savedStateHandle["surahId"])
    private val arabicName: String = checkNotNull(savedStateHandle["arabicName"])
    private val englishName: String = checkNotNull(savedStateHandle["englishName"])
    private val targetAyahId: Int? = savedStateHandle["targetAyahId"]

    init {
        loadSurahAyat()
    }

    fun onAyahVisible(ayahId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            continueTilawahRepository.save(
                surahId = surahId,
                ayahId = ayahId
            )
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
                updateState {
                    it.copy(
                        ayat = ayat.map { AyaUi(it.ayahNumber, it.text) },
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
                    updateState { it.copy(  showActions = false) }
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
                            ayahUi = AyaUi(id = ayahId, text = ayaText),
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
}