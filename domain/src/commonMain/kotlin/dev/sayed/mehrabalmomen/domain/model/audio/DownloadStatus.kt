package dev.sayed.mehrabalmomen.domain.model.audio

/**
 * Domain-neutral representation of a file download status.
 * Replaces platform-specific classes like WorkInfo in the Domain layer.
 */
data class DownloadStatus(
    val progress: Int,
    val state: State
) {
    enum class State {
        IDLE,
        DOWNLOADING,
        COMPLETED,
        FAILED
    }
}
