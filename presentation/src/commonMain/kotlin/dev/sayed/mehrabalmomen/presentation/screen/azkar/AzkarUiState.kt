package dev.sayed.mehrabalmomen.presentation.screen.azkar

data class AzkarUiState(
    val isLoading: Boolean = false,
    val categories: List<AzkarCategoryUiModel> = emptyList(),
    val isAddCategoryBottomSheetOpen: Boolean = false,
    val newCategoryTitle: String = "",
    val streak: Int = 0,
    val completedCountToday: Int = 0,
    val remainingCount: Int = 0,
    val completionPercentage: Int = 0,
    val totalAzkarCount: Int = 0,
    val historyMap: Map<String, Int> = emptyMap(),
    val selectedFilter: AzkarTimeFilter = AzkarTimeFilter.YEAR
)

enum class AzkarTimeFilter {
    DAY, WEEK, MONTH, YEAR
}

data class AzkarCategoryUiModel(
    val title: String,
    val iconRes: Int = 0,
    val itemCount: Int = 0
)
