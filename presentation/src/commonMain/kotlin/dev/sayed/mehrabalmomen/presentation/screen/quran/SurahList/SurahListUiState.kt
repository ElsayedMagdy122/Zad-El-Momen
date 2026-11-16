package dev.sayed.mehrabalmomen.presentation.screen.quran.SurahList

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.madani
import zadelmomen.presentation.generated.resources.mekki
import zadelmomen.presentation.generated.resources.sura_1

data class SurahListUiState(
    val surahList: List<SurahUiState> = emptyList(),
    val isLoading: Boolean = true
)

data class SurahUiState(
    val id: Int,
    val name: String,
    val nameArabic: String,
    val nameEnglish: String,
    val ayahNumbers: Int,
    val surahType: SurahType,
    val surahImage: DrawableResource = Res.drawable.sura_1
)

enum class SurahType(val text: StringResource) {
    MAKKI(Res.string.mekki),
    MADANI(Res.string.madani)
}
