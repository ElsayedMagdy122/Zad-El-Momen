package dev.sayed.mehrabalmomen.presentation.screen.SurahAyat


data class SurahAyatUiState(
    val ayat: List<AyaUi> = emptyList(),
    val tafseerUi: TafseerUi? = TafseerUi(),
   val arabicName:String = "",
   val englishName:String = "",
    val selectedAyaId: Int? = null,
    val selectedAyaText: String = "",
    val showActions: Boolean = false,
    val isLoading: Boolean = true,
    val scrollToAyaId: Int? = null,
    val targetAyahId: Int? = null,
    val showTafseerSheet: Boolean = false
)

data class AyaUi(
    val id: Int,
    val text: String
)

data class TafseerUi(
    val ayahUi: AyaUi? = null,
    val text: String? = null
)
