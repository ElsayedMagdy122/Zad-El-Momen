package dev.sayed.mehrabalmomen.presentation.screen.quran.reciters

import dev.sayed.mehrabalmomen.domain.entity.quran.audio.QuranAudioReader

fun QuranAudioReader.toUiState(
    isArabic: Boolean = true,
    isDownloaded: DownloadState = DownloadState.NOT_DOWNLOADED,
    isSelected: Boolean = false
): ReciterUiState {
    val displayRewaya = if (isArabic) {
        rewaya?.nameAr.orEmpty()
    } else {
        rewaya?.nameEn.orEmpty()
    }

    return ReciterUiState(
        id = id,
        nameAr = nameAr,
        nameEn= nameEn,
        rewayaName = displayRewaya,
        baseAudioUrl = baseAudioUrl,
        downloadState = isDownloaded,
        isSelected = isSelected
    )
}
