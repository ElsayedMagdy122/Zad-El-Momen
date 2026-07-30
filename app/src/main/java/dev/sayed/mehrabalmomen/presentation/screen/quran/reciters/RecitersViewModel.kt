package dev.sayed.mehrabalmomen.presentation.screen.quran.reciters

import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranAudioReadersRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel

class RecitersViewModel(
    private val readersRepository: QuranAudioReadersRepository
) : BaseViewModel<RecitersUiState, RecitersEffect>(RecitersUiState()) {

    init {
        loadReciters()
    }

    fun loadReciters(isArabic: Boolean = true) {
        tryToCall(
            onStart = { updateState { it.copy(isLoading = true) } },
            block = {
                readersRepository.getReaders()
            },
            onSuccess = { readersList ->
                updateState { state ->
                    state.copy(
                        reciters = readersList.map { reader -> reader.toUiState(isArabic) },
                        isLoading = false
                    )
                }
            },
            onError = {
                updateState { it.copy(isLoading = false) }
                sendEffect(
                    RecitersEffect.ShowToast(
                        ToastDetails(
                            title = R.string.error,
                            message = R.string.no_internet_connection,
                            icon = R.drawable.ic_close_circle
                        )
                    )
                )
            }
        )
    }
}