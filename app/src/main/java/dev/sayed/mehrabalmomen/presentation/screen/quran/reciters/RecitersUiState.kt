package dev.sayed.mehrabalmomen.presentation.screen.quran.reciters


data class RecitersUiState(
    val reciters: List<ReciterUiState> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val errorMessage: String? = null
)

data class ReciterUiState(
    val id: Int,
    val name: String,
    val rewayaName: String,
    val baseAudioUrl: String,
    val isDownloaded: Boolean = false
)