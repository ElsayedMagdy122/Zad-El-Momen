package dev.sayed.mehrabalmomen.data.quran.audio.remote

import dev.sayed.mehrabalmomen.data.quran.audio.remote.dto.*
import dev.sayed.mehrabalmomen.domain.entity.quran.audio.*
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
private fun String?.toInstantOrNull(): Instant? {
    return this?.let {
        runCatching { Instant.parse(it) }.getOrNull()
    }
}

fun QuranAudioRewayatDto.toEntity(): QuranAudioRewayat = QuranAudioRewayat(
    id = id,
    nameAr = nameAr,
    nameEn = nameEn
)

@OptIn(ExperimentalTime::class)
fun QuranAudioReaderDto.toEntity(rewaya: QuranAudioRewayat? = null): QuranAudioReader = QuranAudioReader(
    id = id,
    nameAr = nameAr,
    nameEn = nameEn,
    baseAudioUrl = baseAudioUrl,
    surahsCount = surahsCount,
    rewaya = rewaya
)

@OptIn(ExperimentalTime::class)
fun QuranAudioSurahDto.toEntity(): QuranAudioSurah = QuranAudioSurah(
    id = id,
    nameAr = nameAr,
    nameEn = nameEn,
    versesCount = versesCount,
    createdAt = createdAt.toInstantOrNull()
)

@OptIn(ExperimentalTime::class)
fun QuranAudioVerseTimingDto.toEntity(): QuranAudioVerseTiming = QuranAudioVerseTiming(
    readerId = readerId,
    surahId = surahId,
    verseNumber = verseNumber,
    startTimeMs = startTimeMs,
    endTimeMs = endTimeMs,
    createdAt = createdAt.toInstantOrNull()
)

fun buildAudioTrack(reader: QuranAudioReaderDto, surahId: Int): QuranAudioTrack {
    val formattedSurahId = surahId.toString().padStart(3, '0')
    val cleanBaseUrl = reader.baseAudioUrl.trimEnd('/')
    val fullAudioUrl = "$cleanBaseUrl/$formattedSurahId.mp3"
    val trackId = "${reader.id}$formattedSurahId".toLongOrNull() ?: 0L

    return QuranAudioTrack(
        id = trackId,
        readerId = reader.id,
        surahId = surahId,
        audioUrl = fullAudioUrl
    )
}