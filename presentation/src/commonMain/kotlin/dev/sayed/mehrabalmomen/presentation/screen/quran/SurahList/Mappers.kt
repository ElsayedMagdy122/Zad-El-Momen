package dev.sayed.mehrabalmomen.presentation.screen.quran.SurahList

import dev.sayed.mehrabalmomen.domain.entity.quran.text.Surah
import org.jetbrains.compose.resources.DrawableResource
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.sura_1
import zadelmomen.presentation.generated.resources.sura_2
import zadelmomen.presentation.generated.resources.sura_3
import zadelmomen.presentation.generated.resources.sura_4
import zadelmomen.presentation.generated.resources.sura_5
import zadelmomen.presentation.generated.resources.sura_6
import zadelmomen.presentation.generated.resources.sura_7
import zadelmomen.presentation.generated.resources.sura_8
import zadelmomen.presentation.generated.resources.sura_9
import zadelmomen.presentation.generated.resources.sura_10
import zadelmomen.presentation.generated.resources.sura_11
import zadelmomen.presentation.generated.resources.sura_12
import zadelmomen.presentation.generated.resources.sura_13
import zadelmomen.presentation.generated.resources.sura_14
import zadelmomen.presentation.generated.resources.sura_15
import zadelmomen.presentation.generated.resources.sura_16
import zadelmomen.presentation.generated.resources.sura_17
import zadelmomen.presentation.generated.resources.sura_18
import zadelmomen.presentation.generated.resources.sura_19
import zadelmomen.presentation.generated.resources.sura_20
import zadelmomen.presentation.generated.resources.sura_21
import zadelmomen.presentation.generated.resources.sura_22
import zadelmomen.presentation.generated.resources.sura_23
import zadelmomen.presentation.generated.resources.sura_24
import zadelmomen.presentation.generated.resources.sura_25
import zadelmomen.presentation.generated.resources.sura_26
import zadelmomen.presentation.generated.resources.sura_27
import zadelmomen.presentation.generated.resources.sura_28
import zadelmomen.presentation.generated.resources.sura_29
import zadelmomen.presentation.generated.resources.sura_30
import zadelmomen.presentation.generated.resources.sura_31
import zadelmomen.presentation.generated.resources.sura_32
import zadelmomen.presentation.generated.resources.sura_33
import zadelmomen.presentation.generated.resources.sura_34
import zadelmomen.presentation.generated.resources.sura_35
import zadelmomen.presentation.generated.resources.sura_36
import zadelmomen.presentation.generated.resources.sura_37
import zadelmomen.presentation.generated.resources.sura_38
import zadelmomen.presentation.generated.resources.sura_39
import zadelmomen.presentation.generated.resources.sura_40
import zadelmomen.presentation.generated.resources.sura_41
import zadelmomen.presentation.generated.resources.sura_42
import zadelmomen.presentation.generated.resources.sura_43
import zadelmomen.presentation.generated.resources.sura_44
import zadelmomen.presentation.generated.resources.sura_45
import zadelmomen.presentation.generated.resources.sura_46
import zadelmomen.presentation.generated.resources.sura_47
import zadelmomen.presentation.generated.resources.sura_48
import zadelmomen.presentation.generated.resources.sura_49
import zadelmomen.presentation.generated.resources.sura_50
import zadelmomen.presentation.generated.resources.sura_51
import zadelmomen.presentation.generated.resources.sura_52
import zadelmomen.presentation.generated.resources.sura_53
import zadelmomen.presentation.generated.resources.sura_54
import zadelmomen.presentation.generated.resources.sura_55
import zadelmomen.presentation.generated.resources.sura_56
import zadelmomen.presentation.generated.resources.sura_57
import zadelmomen.presentation.generated.resources.sura_58
import zadelmomen.presentation.generated.resources.sura_59
import zadelmomen.presentation.generated.resources.sura_60
import zadelmomen.presentation.generated.resources.sura_61
import zadelmomen.presentation.generated.resources.sura_62
import zadelmomen.presentation.generated.resources.sura_63
import zadelmomen.presentation.generated.resources.sura_64
import zadelmomen.presentation.generated.resources.sura_65
import zadelmomen.presentation.generated.resources.sura_66
import zadelmomen.presentation.generated.resources.sura_67
import zadelmomen.presentation.generated.resources.sura_68
import zadelmomen.presentation.generated.resources.sura_69
import zadelmomen.presentation.generated.resources.sura_70
import zadelmomen.presentation.generated.resources.sura_71
import zadelmomen.presentation.generated.resources.sura_72
import zadelmomen.presentation.generated.resources.sura_73
import zadelmomen.presentation.generated.resources.sura_74
import zadelmomen.presentation.generated.resources.sura_75
import zadelmomen.presentation.generated.resources.sura_76
import zadelmomen.presentation.generated.resources.sura_77
import zadelmomen.presentation.generated.resources.sura_78
import zadelmomen.presentation.generated.resources.sura_79
import zadelmomen.presentation.generated.resources.sura_80
import zadelmomen.presentation.generated.resources.sura_81
import zadelmomen.presentation.generated.resources.sura_82
import zadelmomen.presentation.generated.resources.sura_83
import zadelmomen.presentation.generated.resources.sura_84
import zadelmomen.presentation.generated.resources.sura_85
import zadelmomen.presentation.generated.resources.sura_86
import zadelmomen.presentation.generated.resources.sura_87
import zadelmomen.presentation.generated.resources.sura_88
import zadelmomen.presentation.generated.resources.sura_89
import zadelmomen.presentation.generated.resources.sura_90
import zadelmomen.presentation.generated.resources.sura_91
import zadelmomen.presentation.generated.resources.sura_92
import zadelmomen.presentation.generated.resources.sura_93
import zadelmomen.presentation.generated.resources.sura_94
import zadelmomen.presentation.generated.resources.sura_95
import zadelmomen.presentation.generated.resources.sura_96
import zadelmomen.presentation.generated.resources.sura_97
import zadelmomen.presentation.generated.resources.sura_98
import zadelmomen.presentation.generated.resources.sura_99
import zadelmomen.presentation.generated.resources.sura_100
import zadelmomen.presentation.generated.resources.sura_101
import zadelmomen.presentation.generated.resources.sura_102
import zadelmomen.presentation.generated.resources.sura_103
import zadelmomen.presentation.generated.resources.sura_104
import zadelmomen.presentation.generated.resources.sura_105
import zadelmomen.presentation.generated.resources.sura_106
import zadelmomen.presentation.generated.resources.sura_107
import zadelmomen.presentation.generated.resources.sura_108
import zadelmomen.presentation.generated.resources.sura_109
import zadelmomen.presentation.generated.resources.sura_110
import zadelmomen.presentation.generated.resources.sura_111
import zadelmomen.presentation.generated.resources.sura_112
import zadelmomen.presentation.generated.resources.sura_113
import zadelmomen.presentation.generated.resources.sura_114

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
