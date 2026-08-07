package dev.sayed.mehrabalmomen.presentation.screen.quran.reciters_search

sealed interface RecitersSearchEffect {
    data object NavigateBack : RecitersSearchEffect
    data class ReciterSelected(
        val readerId: Int,
        val nameAr: String,
        val nameEn: String
    ) : RecitersSearchEffect
}
