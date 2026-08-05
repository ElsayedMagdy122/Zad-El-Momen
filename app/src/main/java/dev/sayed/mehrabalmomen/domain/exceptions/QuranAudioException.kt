package dev.sayed.mehrabalmomen.domain.exceptions

open class QuranAudioException(
    message: String? = null,
    cause: Throwable? = null
) : AppException(message, cause) {

    class AudioFileNotFoundException(
        val readerId: Int, 
        val surahId: Int? = null
    ) : QuranAudioException("Audio file not found for reader $readerId")

    class StorageFullException : 
        QuranAudioException("Insufficient storage space to download audio files")

    class DownloadFailedException(
        cause: Throwable? = null
    ) : QuranAudioException("Failed to download audio file", cause)
}