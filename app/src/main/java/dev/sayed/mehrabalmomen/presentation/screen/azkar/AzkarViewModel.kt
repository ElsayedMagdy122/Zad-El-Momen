package dev.sayed.mehrabalmomen.presentation.screen.azkar

import dev.sayed.mehrabalmomen.domain.analytics.AnalyticsTracker
import dev.sayed.mehrabalmomen.domain.repository.azkar.AzkarRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel

class AzkarViewModel(
    private val repository: AzkarRepository,
    private val analyticsTracker: AnalyticsTracker
) : BaseViewModel<AzkarUiState, AzkarEffect>(AzkarUiState()), AzkarInteractionListener {

    init {
        loadAzkar()
    }
    fun onScreenOpened() {
        analyticsTracker.logScreen("azkar")
    }
    private fun loadAzkar() {
        tryToCall(
            block = { repository.getAzkarCategories() },
            onSuccess = { categories ->
                updateState { state ->
                    state.copy(
                        categories = categories.map { category ->
                            AzkarCategoryUiModel(category.title.toAzkarType())
                        },
                        isLoading = false
                    )
                }
            },
            onError = {
                updateState { it.copy(isLoading = false) }
            }
        )
    }

    override fun onClickCategory(type: AzkarType) {
        analyticsTracker.logEvent(
            name = "on click azkar category",
            params = mapOf(
                "category_name" to type.domainTitle
            )
        )
        sendEffect(AzkarEffect.NavigateToDetails(type))
    }

    override fun onClickBack() {
        sendEffect(AzkarEffect.NavigateToBack)
    }
}