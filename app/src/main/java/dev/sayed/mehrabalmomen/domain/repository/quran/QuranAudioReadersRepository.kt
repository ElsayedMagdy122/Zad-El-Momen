package dev.sayed.mehrabalmomen.domain.repository.quran

import dev.sayed.mehrabalmomen.domain.entity.quran.audio.QuranAudioReader
import dev.sayed.mehrabalmomen.domain.model.audio.DownloadStatus
import dev.sayed.mehrabalmomen.domain.model.audio.ReciterPreference
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing Quran audio reciters and their download status.
 */
interface QuranAudioReadersRepository {

    /** Retrieves all available reciters. */
    suspend fun getReaders(): List<QuranAudioReader>

    /** Observes downloaded reciters for a specific surah. */
    fun getDownloadedReciters(surahId: Int): Flow<List<QuranAudioReader>>

    /** Retrieves all downloaded reciters across all surahs. */
    suspend fun getDownloadedRecitersOnce(): List<QuranAudioReader>

    /** Checks if a specific reciter's surah is downloaded. */
    suspend fun isReciterDownloaded(reciterId: Int, surahId: Int): Boolean

    /** Retrieves a specific downloaded reciter. */
    suspend fun getDownloadedReciter(reciterId: Int, surahId: Int): QuranAudioReader?

    /** Starts the download process for a reciter's surah. */
    fun downloadReciter(reciter: QuranAudioReader, surahId: Int)

    /** Observes the download progress and status for a specific task. */
    fun observeDownloadStatus(reciterId: Int, surahId: Int): Flow<DownloadStatus>

    /** Saves the last selected reciter preference. */
    suspend fun saveLastReciter(preference: ReciterPreference)

    /** Observes the last selected reciter preference. */
    fun observeLastReciter(): Flow<ReciterPreference?>
}
