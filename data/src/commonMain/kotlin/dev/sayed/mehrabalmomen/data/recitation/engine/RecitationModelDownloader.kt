package dev.sayed.mehrabalmomen.data.recitation.engine

import kotlinx.coroutines.flow.StateFlow

expect class RecitationModelDownloader {
    val downloadState: StateFlow<ModelDownloadState>
    fun isModelReady(): Boolean
    fun isDownloading(): Boolean
    suspend fun downloadAndPrepareModels(): Boolean
    fun resetState()
    fun startDownloadService()
    fun stopDownloadService()
}
