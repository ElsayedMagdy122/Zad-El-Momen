package dev.sayed.mehrabalmomen.data.recitation.engine

sealed interface ModelDownloadState {
    object Idle : ModelDownloadState
    data class Downloading(val progress: Float, val currentFile: String) : ModelDownloadState
    data class Processing(val message: String) : ModelDownloadState
    data class Error(val message: String) : ModelDownloadState
    object Ready : ModelDownloadState
}
