package dev.sayed.mehrabalmomen.presentation.screen.quran.SurahList

import dev.sayed.mehrabalmomen.domain.entity.quran.text.Surah
import org.jetbrains.compose.resources.DrawableResource
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.*

fun Surah.toUiState() =
    SurahUiState(
        id = surahNumber,
        name = nameEnglish,
        nameArabic = nameArabic,
        nameEnglish = nameEnglish,
        ayahNumbers = ayahCount,
        surahType = type.toUiState(),
        surahImage = getSurahImage(surahNumber)
    )

fun Surah.SurahType.toUiState(): SurahType {
    return when (this) {
        Surah.SurahType.MAKKI -> SurahType.MAKKI
        Surah.SurahType.MADANI -> SurahType.MADANI
    }
}

fun getSurahImage(surahId: Int): DrawableResource {
    return when (surahId) {
        1 -> Res.drawable.sura_1
        2 -> Res.drawable.sura_2
        3 -> Res.drawable.sura_3
        4 -> Res.drawable.sura_4
        5 -> Res.drawable.sura_5
        6 -> Res.drawable.sura_6
        7 -> Res.drawable.sura_7
        8 -> Res.drawable.sura_8
        9 -> Res.drawable.sura_9
        10 -> Res.drawable.sura_10
        11 -> Res.drawable.sura_11
        12 -> Res.drawable.sura_12
        13 -> Res.drawable.sura_13
        14 -> Res.drawable.sura_14
        15 -> Res.drawable.sura_15
        16 -> Res.drawable.sura_16
        17 -> Res.drawable.sura_17
        18 -> Res.drawable.sura_18
        19 -> Res.drawable.sura_19
        20 -> Res.drawable.sura_20
        21 -> Res.drawable.sura_21
        22 -> Res.drawable.sura_22
        23 -> Res.drawable.sura_23
        24 -> Res.drawable.sura_24
        25 -> Res.drawable.sura_25
        26 -> Res.drawable.sura_26
        27 -> Res.drawable.sura_27
        28 -> Res.drawable.sura_28
        29 -> Res.drawable.sura_29
        30 -> Res.drawable.sura_30
        31 -> Res.drawable.sura_31
        32 -> Res.drawable.sura_32
        33 -> Res.drawable.sura_33
        34 -> Res.drawable.sura_34
        35 -> Res.drawable.sura_35
        36 -> Res.drawable.sura_36
        37 -> Res.drawable.sura_37
        38 -> Res.drawable.sura_38
        39 -> Res.drawable.sura_39
        40 -> Res.drawable.sura_40
        41 -> Res.drawable.sura_41
        else -> Res.drawable.sura_1
    }
}
