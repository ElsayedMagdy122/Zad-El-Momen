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
        loadAzkar()
    }
    private fun loadAzkar() {
        tryToCall(
            block = {
                val categories = repository.getAzkarCategories()
                val stats = repository.getAzkarStats()
                val history = repository.getAzkarHistory()
                Triple(categories, stats, history)
            },
            onSuccess = { (categories, stats, history) ->
                updateState { state ->
                    state.copy(
                        categories = categories.map { category ->
                            AzkarCategoryUiModel(
                                title = category.title,
                                iconRes = getIconForCategory(category.title),
                                itemCount = category.items.size
                            )
                        },
                        streak = stats.streak,
                        completedCountToday = stats.completedCountToday,
                        remainingCount = stats.remainingCount,
                        completionPercentage = stats.completionPercentage,
                        totalAzkarCount = stats.totalAzkarCount,
                        historyMap = history,
                        isLoading = false
                    )
                }
            },
            onError = { _ ->
                updateState { it.copy(isLoading = false) }
            }
        )
    }

    override fun onClickCategory(title: String) {
        analyticsTracker.logEvent(
            name = "on click azkar category",
            params = mapOf(
                "category_name" to title
            )
        )
        sendEffect(AzkarEffect.NavigateToDetails(title))
    }

    override fun onClickBack() {
        sendEffect(AzkarEffect.NavigateToBack)
    }

    override fun onOpenAddCategoryBottomSheet() {
        unknownAction { updateState { it.copy(isAddCategoryBottomSheetOpen = true) } }
    }

    private fun unknownAction(action: () -> Unit) {
        action()
    }

    override fun onDismissAddCategoryBottomSheet() {
        updateState { it.copy(isAddCategoryBottomSheetOpen = false, newCategoryTitle = "") }
    }

    override fun onNewCategoryTitleChanged(title: String) {
        updateState { it.copy(newCategoryTitle = title) }
    }

    override fun onSaveNewCategory() {
        val title = screenState.value.newCategoryTitle.trim()
        if (title.isNotEmpty()) {
            tryToCall(
                block = { repository.addCategory(title) },
                onSuccess = {
                    updateState { it.copy(isAddCategoryBottomSheetOpen = false, newCategoryTitle = "") }
                    loadAzkar()
                },
                onError = {}
            )
        }
    }

    override fun onSelectTimeFilter(filter: AzkarTimeFilter) {
        updateState { it.copy(selectedFilter = filter) }
    }
}
