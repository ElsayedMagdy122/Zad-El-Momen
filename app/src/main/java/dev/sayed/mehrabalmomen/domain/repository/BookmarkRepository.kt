package dev.sayed.mehrabalmomen.domain.repository

import dev.sayed.mehrabalmomen.domain.entity.quran.Bookmark

interface BookmarkRepository {
    suspend fun addBookmark(bookmark: Bookmark)
    fun getBookmarks(): List<Bookmark>
    suspend fun removeBookmark(surahId: Int, ayahId: Int)
}