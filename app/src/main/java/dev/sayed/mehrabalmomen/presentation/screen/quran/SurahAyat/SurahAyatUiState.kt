package dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat

import dev.sayed.mehrabalmomen.domain.entity.quran.audio.QuranAudioVerseTiming

data class SurahAyatUiState(
    val ayat: List<AyaUi> = emptyList(),
    val tafseerUi: TafseerUi? = TafseerUi(),
    val arabicName: String = "",
    val englishName: String = "",
    val selectedAyaId: Int? = null,
    val selectedAyaText: String = "",
    val showActions: Boolean = false,
    val isLoading: Boolean = true,
    val scrollToAyaId: Int? = null,
    val targetAyahId: Int? = null,
    val showTafseerSheet: Boolean = false,
    val fontSize: QuranFontSize = QuranFontSize.MEDIUM,
    val messageState : Boolean = true,

    val showTilawahBox: Boolean = false,
    val selectedReaderId: Int? = null,
    val selectedReaderNameAr: String? = null,
    val selectedReaderNameEn: String? = null,
    val currentAudioAyahId: Int = 1,
    val isAudioPlaying: Boolean = false,
    val isAudioLoading: Boolean = false,
    val repeatCount: Int = 0,
    val currentRepeatIteration: Int = 0,
    val isContinuousReading: Boolean = true,
    val timings: List<QuranAudioVerseTiming> = emptyList(),
    val currentAudioUrl: String? = null
)

data class AyaUi(
    val id: Int,
    val text: String
)

enum class QuranFontSize(val sizeSp: Int) {
    SMALL(20),
    MEDIUM(24),
    LARGE(28),
    EXTRA_LARGE(32)
}

data class TafseerUi(
    val ayahUi: AyaUi? = null,
    val text: String? = null
)
