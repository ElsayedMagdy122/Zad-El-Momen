package dev.sayed.mehrabalmomen.domain.repository.quran

import dev.sayed.mehrabalmomen.domain.entity.quran.bookmark.Bookmark

interface BookmarkRepository {
    suspend fun addBookmark(bookmark: Bookmark)
    suspend fun getBookmarks(): List<Bookmark>
    suspend fun removeBookmark(surahId: Int, ayahId: Int)
}