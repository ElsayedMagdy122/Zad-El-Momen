package dev.sayed.mehrabalmomen.data.quran.audio.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dev.sayed.mehrabalmomen.data.quran.audio.local.entity.DownloadedReciterEntity
import dev.sayed.mehrabalmomen.data.quran.audio.local.entity.VerseTimingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadedReciterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownloadedReciter(reciter: DownloadedReciterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerseTimings(timings: List<VerseTimingEntity>)

    @Transaction
    suspend fun saveDownloadedReciterWithTimings(
        reciter: DownloadedReciterEntity,
        timings: List<VerseTimingEntity>
    ) {
        insertDownloadedReciter(reciter)
        insertVerseTimings(timings)
    }

    @Query("SELECT * FROM downloaded_reciters WHERE id = :reciterId AND surahId = :surahId")
    suspend fun getDownloadedReciter(reciterId: Int, surahId: Int): DownloadedReciterEntity?

    @Query("SELECT * FROM verse_timings WHERE readerId = :reciterId AND surahId = :surahId ORDER BY verseNumber ASC")
    suspend fun getVerseTimings(reciterId: Int, surahId: Int): List<VerseTimingEntity>

    @Query("SELECT * FROM downloaded_reciters")
    fun getAllDownloadedReciters(): Flow<List<DownloadedReciterEntity>>

    @Query("SELECT * FROM downloaded_reciters WHERE surahId = :surahId")
    fun getDownloadedRecitersForSurah(surahId: Int): Flow<List<DownloadedReciterEntity>>

    @Query("SELECT * FROM downloaded_reciters")
    suspend fun getAllDownloadedRecitersOnce(): List<DownloadedReciterEntity>

    @Query("DELETE FROM downloaded_reciters WHERE id = :reciterId AND surahId = :surahId")
    suspend fun deleteDownloadedReciter(reciterId: Int, surahId: Int)

    @Query("DELETE FROM verse_timings WHERE readerId = :reciterId AND surahId = :surahId")
    suspend fun deleteVerseTimings(reciterId: Int, surahId: Int)
}
