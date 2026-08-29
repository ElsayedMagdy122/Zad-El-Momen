package dev.sayed.mehrabalmomen.presentation.screen.AzkarDetails

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.domain.repository.azkar.AzkarRepository
import dev.sayed.mehrabalmomen.domain.repository.companion.CompanionRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.launch

class AzkarDetailViewModel(
    private val repository: AzkarRepository,
    private val companionRepository: CompanionRepository
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
                viewModelScope.launch {
                    companionRepository.updateAzkarReadStatus(true)
                    companionRepository.updateLastInteraction(System.currentTimeMillis())
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