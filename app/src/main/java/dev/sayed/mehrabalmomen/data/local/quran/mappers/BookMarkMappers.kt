package dev.sayed.mehrabalmomen.data.local.quran.mappers

import dev.sayed.mehrabalmomen.data.local.quran.dto.BookmarkEntity
import dev.sayed.mehrabalmomen.domain.entity.quran.Bookmark

fun Bookmark.toEntity(): BookmarkEntity {
    return BookmarkEntity(
        surahId = this.surahId,
        ayahId = this.ayahId,
        arabicName = this.arabicName,
        englishName = this.englishName,
        text = this.text,
        bookmarkedAt = this.bookmarkedAt
    )
}

fun BookmarkEntity.toDomain(): Bookmark {
    return Bookmark(
        surahId = this.surahId,
        ayahId = this.ayahId,
        text = this.text,
        bookmarkedAt = this.bookmarkedAt,
        arabicName = this.arabicName,
        englishName = this.englishName
    )
}