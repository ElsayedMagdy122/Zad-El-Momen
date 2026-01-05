package dev.sayed.mehrabalmomen.presentation.screen.AzkarDetails

import dev.sayed.mehrabalmomen.domain.repository.AzkarRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel

class AzkarDetailViewModel(
    private val repository: AzkarRepository
) : BaseViewModel<AzkarDetailUiState, AzkarDetailEffect>(
    AzkarDetailUiState()
), AzkarDetailInteractionListener {

    fun loadAzkar(title: String) {
        updateState { it.copy(isLoading = true, title = title) }

        tryToCall(
            block = {
                repository.getAzkarCategories()
                    .firstOrNull { it.title == title }
                    ?.items
                    .orEmpty()
            },
            onSuccess = { items ->
                updateState {
                    it.copy(
                        isLoading = false,
                        items = items
                    )
                }
            },
            onError = {
                sendEffect(AzkarDetailEffect.ShowError(it.message ?: "Error"))
            }
        )
    }

    override fun onClickBack() {
        sendEffect(AzkarDetailEffect.NavigateBack)
    }
}