package dev.sayed.mehrabalmomen.data.local.quran

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.sayed.mehrabalmomen.data.local.quran.dto.BookmarkEntity

@Dao
interface BookmarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("SELECT * FROM bookmark_table ORDER BY bookmarkedAt DESC")
    fun getAllBookmarks(): List<BookmarkEntity>

    @Query("""
        DELETE FROM bookmark_table
        WHERE surahId = :surahId
        AND ayahId = :ayahId
    """)
    suspend fun deleteBookmark(
        surahId: Int,
        ayahId: Int
    )
}