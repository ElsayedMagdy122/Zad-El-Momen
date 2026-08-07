package dev.sayed.mehrabalmomen.presentation.screen.quran.reciters_search

import dev.sayed.mehrabalmomen.presentation.screen.quran.reciters.ReciterUiState

data class RecitersSearchUiState(
    val surahId: Int = 1,
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val results: List<ReciterUiState> = emptyList(),
    val isArabic: Boolean = true
)
