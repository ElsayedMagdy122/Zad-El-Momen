package dev.sayed.mehrabalmomen.presentation.screen.quran.SurahList

import dev.sayed.mehrabalmomen.designsystem.resources.*
import dev.sayed.mehrabalmomen.domain.entity.quran.text.Surah
import org.jetbrains.compose.resources.DrawableResource

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
        42 -> Res.drawable.sura_42
        43 -> Res.drawable.sura_43
        44 -> Res.drawable.sura_44
        45 -> Res.drawable.sura_45
        46 -> Res.drawable.sura_46
        47 -> Res.drawable.sura_47
        48 -> Res.drawable.sura_48
        49 -> Res.drawable.sura_49
        50 -> Res.drawable.sura_50
        51 -> Res.drawable.sura_51
        52 -> Res.drawable.sura_52
        53 -> Res.drawable.sura_53
        54 -> Res.drawable.sura_54
        55 -> Res.drawable.sura_55
        56 -> Res.drawable.sura_56
        57 -> Res.drawable.sura_57
        58 -> Res.drawable.sura_58
        59 -> Res.drawable.sura_59
        60 -> Res.drawable.sura_60
        61 -> Res.drawable.sura_61
        62 -> Res.drawable.sura_62
        63 -> Res.drawable.sura_63
        64 -> Res.drawable.sura_64
        65 -> Res.drawable.sura_65
        66 -> Res.drawable.sura_66
        67 -> Res.drawable.sura_67
        68 -> Res.drawable.sura_68
        69 -> Res.drawable.sura_69
        70 -> Res.drawable.sura_70
        71 -> Res.drawable.sura_71
        72 -> Res.drawable.sura_72
        73 -> Res.drawable.sura_73
        74 -> Res.drawable.sura_74
        75 -> Res.drawable.sura_75
        76 -> Res.drawable.sura_76
        77 -> Res.drawable.sura_77
        78 -> Res.drawable.sura_78
        79 -> Res.drawable.sura_79
        80 -> Res.drawable.sura_80
        81 -> Res.drawable.sura_81
        82 -> Res.drawable.sura_82
        83 -> Res.drawable.sura_83
        84 -> Res.drawable.sura_84
        85 -> Res.drawable.sura_85
        86 -> Res.drawable.sura_86
        87 -> Res.drawable.sura_87
        88 -> Res.drawable.sura_88
        89 -> Res.drawable.sura_89
        90 -> Res.drawable.sura_90
        91 -> Res.drawable.sura_91
        92 -> Res.drawable.sura_92
        93 -> Res.drawable.sura_93
        94 -> Res.drawable.sura_94
        95 -> Res.drawable.sura_95
        96 -> Res.drawable.sura_96
        97 -> Res.drawable.sura_97
        98 -> Res.drawable.sura_98
        99 -> Res.drawable.sura_99
        100 -> Res.drawable.sura_100
        101 -> Res.drawable.sura_101
        102 -> Res.drawable.sura_102
        103 -> Res.drawable.sura_103
        104 -> Res.drawable.sura_104
        105 -> Res.drawable.sura_105
        106 -> Res.drawable.sura_106
        107 -> Res.drawable.sura_107
        108 -> Res.drawable.sura_108
        109 -> Res.drawable.sura_109
        110 -> Res.drawable.sura_110
        111 -> Res.drawable.sura_111
        112 -> Res.drawable.sura_112
        113 -> Res.drawable.sura_113
        114 -> Res.drawable.sura_114
        else -> Res.drawable.sura_1
    }
}
