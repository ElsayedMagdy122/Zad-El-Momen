package dev.sayed.mehrabalmomen.presentation.screen.quran.SurahList

import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.madani
import dev.sayed.mehrabalmomen.designsystem.resources.mekki
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

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
    val surahImage: DrawableResource
)

enum class SurahType(val text: StringResource) {
    MAKKI(Res.string.mekki),
    MADANI(Res.string.madani)
}
