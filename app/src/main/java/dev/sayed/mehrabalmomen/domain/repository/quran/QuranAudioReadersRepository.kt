package dev.sayed.mehrabalmomen.domain.repository.quran

import dev.sayed.mehrabalmomen.domain.entity.quran.audio.QuranAudioReader
import kotlinx.coroutines.flow.Flow

interface QuranAudioReadersRepository {
    suspend fun getReaders(): List<QuranAudioReader>

    fun getDownloadedReciters(surahId: Int): Flow<List<QuranAudioReader>>

    suspend fun getDownloadedRecitersOnce(): List<QuranAudioReader>

    suspend fun isReciterDownloaded(reciterId: Int, surahId: Int): Boolean

    suspend fun getDownloadedReciter(reciterId: Int, surahId: Int): QuranAudioReader?

    fun downloadReciter(reciter: QuranAudioReader, surahId: Int)

    fun getDownloadWorkInfo(reciterId: Int, surahId: Int): Flow<List<androidx.work.WorkInfo>>
}
