package dev.sayed.mehrabalmomen.presentation.screen.SurahAyat

data class SurahAyatUiState(
    val ayat: List<AyaUi> = emptyList(),
    val surahName:String ="",
    val selectedAyaId: Int? = null,
    val selectedAyaText: String = "",
    val showActions: Boolean = false,
    val isLoading: Boolean = true
)

data class AyaUi(
    val id: Int,
    val text: String
)
