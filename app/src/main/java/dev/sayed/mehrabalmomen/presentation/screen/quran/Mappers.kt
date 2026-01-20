package dev.sayed.mehrabalmomen.presentation.screen.quran

import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.domain.entity.Surah

fun Surah.toUiState() = SurahUiState(
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

fun getSurahImage(surahId: Int): Int {
    return when (surahId) {
        1 -> R.drawable.sura_1
        2 -> R.drawable.sura_2
        3 -> R.drawable.sura_3
        4 -> R.drawable.sura_4
        5 -> R.drawable.sura_5
        6 -> R.drawable.sura_6
        7 -> R.drawable.sura_7
        8 -> R.drawable.sura_8
        9 -> R.drawable.sura_9
        10 -> R.drawable.sura_10
        11 -> R.drawable.sura_11
        12 -> R.drawable.sura_12
        13 -> R.drawable.sura_13
        14 -> R.drawable.sura_14
        15 -> R.drawable.sura_15
        16 -> R.drawable.sura_16
        17 -> R.drawable.sura_17
        18 -> R.drawable.sura_18
        19 -> R.drawable.sura_19
        20 -> R.drawable.sura_20
        21 -> R.drawable.sura_21
        22 -> R.drawable.sura_22
        23 -> R.drawable.sura_23
        24 -> R.drawable.sura_24
        25 -> R.drawable.sura_25
        26 -> R.drawable.sura_26
        27 -> R.drawable.sura_27
        28 -> R.drawable.sura_28
        29 -> R.drawable.sura_29
        30 -> R.drawable.sura_30
        31 -> R.drawable.sura_31
        32 -> R.drawable.sura_32
        33 -> R.drawable.sura_33
        34 -> R.drawable.sura_34
        35 -> R.drawable.sura_35
        36 -> R.drawable.sura_36
        37 -> R.drawable.sura_37
        38 -> R.drawable.sura_38
        39 -> R.drawable.sura_39
        40 -> R.drawable.sura_40
        41 -> R.drawable.sura_41
        42 -> R.drawable.sura_42
        43 -> R.drawable.sura_43
        44 -> R.drawable.sura_44
        45 -> R.drawable.sura_45
        46 -> R.drawable.sura_46
        47 -> R.drawable.sura_47
        48 -> R.drawable.sura_48
        49 -> R.drawable.sura_49
        50 -> R.drawable.sura_50
        51 -> R.drawable.sura_51
        52 -> R.drawable.sura_52
        53 -> R.drawable.sura_53
        54 -> R.drawable.sura_54
        55 -> R.drawable.sura_55
        56 -> R.drawable.sura_56
        57 -> R.drawable.sura_57
        58 -> R.drawable.sura_58
        59 -> R.drawable.sura_59
        60 -> R.drawable.sura_60
        61 -> R.drawable.sura_61
        62 -> R.drawable.sura_62
        63 -> R.drawable.sura_63
        64 -> R.drawable.sura_64
        65 -> R.drawable.sura_65
        66 -> R.drawable.sura_66
        67 -> R.drawable.sura_67
        68 -> R.drawable.sura_68
        69 -> R.drawable.sura_69
        70 -> R.drawable.sura_70
        71 -> R.drawable.sura_71
        72 -> R.drawable.sura_72
        73 -> R.drawable.sura_73
        74 -> R.drawable.sura_74
        75 -> R.drawable.sura_75
        76 -> R.drawable.sura_76
        77 -> R.drawable.sura_77
        78 -> R.drawable.sura_78
        79 -> R.drawable.sura_79
        80 -> R.drawable.sura_80
        81 -> R.drawable.sura_81
        82 -> R.drawable.sura_82
        83 -> R.drawable.sura_83
        84 -> R.drawable.sura_84
        85 -> R.drawable.sura_85
        86 -> R.drawable.sura_86
        87 -> R.drawable.sura_87
        88 -> R.drawable.sura_88
        89 -> R.drawable.sura_89
        90 -> R.drawable.sura_90
        91 -> R.drawable.sura_91
        92 -> R.drawable.sura_92
        93 -> R.drawable.sura_93
        94 -> R.drawable.sura_94
        95 -> R.drawable.sura_95
        96 -> R.drawable.sura_96
        97 -> R.drawable.sura_97
        98 -> R.drawable.sura_98
        99 -> R.drawable.sura_99
        100 -> R.drawable.sura_100
        101 -> R.drawable.sura_101
        102 -> R.drawable.sura_102
        103 -> R.drawable.sura_103
        104 -> R.drawable.sura_104
        105 -> R.drawable.sura_105
        106 -> R.drawable.sura_106
        107 -> R.drawable.sura_107
        108 -> R.drawable.sura_108
        109 -> R.drawable.sura_109
        110 -> R.drawable.sura_110
        111 -> R.drawable.sura_111
        112 -> R.drawable.sura_112
        113 -> R.drawable.sura_113
        114 -> R.drawable.sura_114
        else -> R.drawable.sura_1
    }
}