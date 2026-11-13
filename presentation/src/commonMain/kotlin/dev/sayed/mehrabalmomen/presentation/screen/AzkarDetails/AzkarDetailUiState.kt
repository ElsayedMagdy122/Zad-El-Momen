package dev.sayed.mehrabalmomen.presentation.screen.AzkarDetails

import dev.sayed.mehrabalmomen.domain.entity.azkar.AzkarItem

data class AzkarDetailUiState(
    val isLoading: Boolean = true,
    val title: String = "",
    val items: List<AzkarItem> = emptyList(),
    val itemCounts: Map<Int, Int> = emptyMap(),
    val isAddDhikrBottomSheetOpen: Boolean = false,
    val newContent: String = "",
    val newCount: String = "1",
    val newDescription: String = "",
    val newReference: String = "",
    val messageState: Boolean = true
)
