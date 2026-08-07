package dev.sayed.mehrabalmomen.presentation.screen.quran.reciters


data class RecitersUiState(
    val reciters: List<ReciterUiState> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val errorMessage: String? = null
)

data class ReciterUiState(
    val id: Int,
    val nameAr: String,
    val nameEn: String,
    val rewayaName: String,
    val baseAudioUrl: String,
    val downloadState: DownloadState = DownloadState.NOT_DOWNLOADED,
    val playState: PlayState = PlayState.RESUME
)

enum class PlayState {
    PLAY,
    LOADING,
    RESUME
}
enum class DownloadState {
    NOT_DOWNLOADED,
    DOWNLOADING,
    DOWNLOADED,
    FAILED
}