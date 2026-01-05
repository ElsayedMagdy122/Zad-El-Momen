package dev.sayed.mehrabalmomen.presentation.screen.azkar

import dev.sayed.mehrabalmomen.domain.repository.AzkarRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel

class AzkarViewModel(
    private val repository: AzkarRepository
) : BaseViewModel<AzkarUiState, AzkarEffect>(
    AzkarUiState(isLoading = true)
), AzkarInteractionListener {

    init {
        loadAzkar()
    }

    private fun loadAzkar() {
        tryToCall(
            block = { repository.getAzkarCategories() },
            onSuccess = { list ->
                updateState {
                    it.copy(
                        isLoading = false,
                        categories = list.map { it.toUiModel() }
                    )
                }
            },
            onError = {
                updateState { it.copy(isLoading = false) }
                sendEffect(AzkarEffect.ShowError(it.message ?: "Error"))
            }
        )
    }

    override fun onClickCategory(type: AzkarType) {
        sendEffect(AzkarEffect.NavigateToDetails(type))
    }

    override fun onClickBack() {
      sendEffect(AzkarEffect.NavigateToBack)
    }
}