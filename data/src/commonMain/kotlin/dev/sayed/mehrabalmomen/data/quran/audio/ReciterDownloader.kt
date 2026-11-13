package dev.sayed.mehrabalmomen.data.quran.audio

import dev.sayed.mehrabalmomen.domain.entity.quran.audio.QuranAudioReader
import dev.sayed.mehrabalmomen.domain.model.audio.DownloadStatus
import kotlinx.coroutines.flow.Flow

/**
 * Platform abstraction for background audio downloading.
 */
interface ReciterDownloader {
    fun downloadReciter(reciter: QuranAudioReader, surahId: Int)
    fun observeDownloadStatus(reciterId: Int, surahId: Int): Flow<DownloadStatus>
}
