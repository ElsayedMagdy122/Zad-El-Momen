package dev.sayed.mehrabalmomen.data.recitation.engine

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

actual class RecitationModelDownloader {
    private val _downloadState = MutableStateFlow<ModelDownloadState>(ModelDownloadState.Ready)
    actual val downloadState: StateFlow<ModelDownloadState> = _downloadState.asStateFlow()

    actual fun isDownloading(): Boolean = false
    actual fun isModelReady(): Boolean = true
    actual suspend fun downloadAndPrepareModels(): Boolean = true
    actual fun resetState() { _downloadState.value = ModelDownloadState.Ready }
}
