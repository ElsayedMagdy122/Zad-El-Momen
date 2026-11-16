package dev.sayed.mehrabalmomen.presentation.screen.settings

import dev.sayed.mehrabalmomen.presentation.base.UiText
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.mozan_abed_albaset
import zadelmomen.presentation.generated.resources.mozan_makkah
import zadelmomen.presentation.generated.resources.mozan_mansoor_alzahrani
import zadelmomen.presentation.generated.resources.mozan_mishary_alafasi
import zadelmomen.presentation.generated.resources.mozan_mohammed_almenshawy
import zadelmomen.presentation.generated.resources.mozan_nasser_alqatami
import zadelmomen.presentation.generated.resources.mozan_suhaib_khatba

fun getMoazenName(moazen: SettingsUiState.Moazen): UiText {
    val resId = when(moazen) {
        SettingsUiState.Moazen.AZAN_ABED_ALBASET -> Res.string.mozan_abed_albaset
        SettingsUiState.Moazen.AZAN_MAKKAH -> Res.string.mozan_makkah
        SettingsUiState.Moazen.AZAN_MANSOOR_AL_ZAHRANI -> Res.string.mozan_mansoor_alzahrani
        SettingsUiState.Moazen.AZAN_MISHARY_ALAFASI -> Res.string.mozan_mishary_alafasi
        SettingsUiState.Moazen.AZAN_MOHAMMED_ALMENSHWY -> Res.string.mozan_mohammed_almenshawy
        SettingsUiState.Moazen.AZAN_NASSER_ALQATAMI -> Res.string.mozan_nasser_alqatami
        SettingsUiState.Moazen.AZAN_SUHAIB_KHATBA -> Res.string.mozan_suhaib_khatba
    }
    return UiText.StringRes(resId)
}
