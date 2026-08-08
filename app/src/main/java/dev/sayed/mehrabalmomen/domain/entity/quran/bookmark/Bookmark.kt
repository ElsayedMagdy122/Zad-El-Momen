package dev.sayed.mehrabalmomen.domain.entity.quran.bookmark

data class Bookmark(
    val surahId: Int,
    val ayahId: Int,
    val arabicName: String,
    val englishName: String,
    val text: String,
    val bookmarkedAt: Long = System.currentTimeMillis()
)