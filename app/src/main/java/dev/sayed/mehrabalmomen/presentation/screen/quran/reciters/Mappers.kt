package dev.sayed.mehrabalmomen.presentation.screen.quran.reciters

import dev.sayed.mehrabalmomen.domain.entity.quran.audio.QuranAudioReader

fun QuranAudioReader.toUiState(
    isArabic: Boolean = true,
    isDownloaded: Boolean = false
): ReciterUiState {
    val displayName = if (isArabic) nameAr else nameEn
    val displayRewaya = if (isArabic) {
        rewaya?.nameAr.orEmpty()
    } else {
        rewaya?.nameEn.orEmpty()
    }

    return ReciterUiState(
        id = id,
        name = displayName,
        rewayaName = displayRewaya,
        baseAudioUrl = baseAudioUrl,
        isDownloaded = isDownloaded
    )
}
