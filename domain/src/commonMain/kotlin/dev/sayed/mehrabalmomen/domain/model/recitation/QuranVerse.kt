package dev.sayed.mehrabalmomen.domain.model.recitation

data class QuranVerse(
    val surah: Int,
    val ayah: Int,
    val textUthmani: String,
    val phonemeWords: List<String>,
    val displayWords: List<String>,
    val globalStartIndex: Int = 0
)
