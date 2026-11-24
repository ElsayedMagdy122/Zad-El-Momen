package dev.sayed.mehrabalmomen.presentation.screen.quran.reciters

import dev.sayed.mehrabalmomen.domain.entity.quran.audio.QuranAudioReader

@OptIn(kotlin.time.ExperimentalTime::class)
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

    val thirtyDaysInMillis = 30L * 24L * 60L * 60L * 1000L
    val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
    val isNew = createdAt?.let { (now - it.toEpochMilliseconds()) <= thirtyDaysInMillis } ?: false

    return ReciterUiState(
        id = id,
        nameAr = nameAr,
        nameEn = nameEn,
        rewayaName = displayRewaya,
        baseAudioUrl = baseAudioUrl,
        downloadState = isDownloaded,
        isSelected = isSelected,
        isNew = isNew
    )
}
