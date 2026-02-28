package dev.sayed.mehrabalmomen.data.local.quran.repository

import dev.sayed.mehrabalmomen.data.local.quran.BookmarkDao
import dev.sayed.mehrabalmomen.data.local.quran.mappers.toDomain
import dev.sayed.mehrabalmomen.data.local.quran.mappers.toEntity
import dev.sayed.mehrabalmomen.domain.entity.quran.Bookmark
import dev.sayed.mehrabalmomen.domain.repository.quran.BookmarkRepository

class BookmarkRepositoryImpl(
    private val dao: BookmarkDao
) : BookmarkRepository {

    override suspend fun addBookmark(bookmark: Bookmark) {
        dao.insertBookmark(bookmark.toEntity())
    }

    override fun getBookmarks():List<Bookmark> {
        return dao.getAllBookmarks().map { list ->
            list.toDomain()
        }
    }

    override suspend fun removeBookmark(surahId: Int, ayahId: Int) {
       dao.deleteBookmark(surahId, ayahId)
    }
}