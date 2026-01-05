package dev.sayed.mehrabalmomen.presentation.screen.AzkarDetails

import dev.sayed.mehrabalmomen.domain.entity.AzkarItem

data class AzkarDetailUiState(
    val isLoading: Boolean = true,
    val title: String = "",
    val items: List<AzkarItem> = emptyList()
)