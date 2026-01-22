package dev.sayed.mehrabalmomen.presentation.screen.SurahAyat

import androidx.lifecycle.SavedStateHandle
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.domain.repository.QuranRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.delay

class SurahAyatViewModel(
    private val quranRepository: QuranRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<SurahAyatUiState, SurahAyatEffect>(
    SurahAyatUiState()
), SurahAyatInteractionListener {

    private val surahId: Int = checkNotNull(savedStateHandle["surahId"])
    private val surahName: String = checkNotNull(savedStateHandle["surahName"])
    private val targetAyahId: Int? = savedStateHandle["targetAyahId"]

    init {
        loadSurahAyat()
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
                        surahName = surahName,
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

    override fun onClickBack() {
        sendEffect(SurahAyatEffect.NavigateToBack)
    }

    override fun onClickSearch() {
        sendEffect(
            SurahAyatEffect.NavigateToSearch(
                surahId = surahId,
                surahName = screenState.value.surahName
            )
        )
    }
}