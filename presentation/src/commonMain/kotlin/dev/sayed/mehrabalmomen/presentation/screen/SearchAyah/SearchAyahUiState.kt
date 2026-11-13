package dev.sayed.mehrabalmomen.presentation.screen.SearchAyah

data class SearchAyahUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val results: List<AyahUi> = emptyList(),
    val searchType: SearchType,
    val surahId: Int? = null,
    val surahName: String? = null
)
data class  AyahUi(
    val id: Int,
    val text: String,
    val surahId: Int,
    val surahName: String=""
)
