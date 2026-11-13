package dev.sayed.mehrabalmomen.presentation.screen.settings

import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.*
import dev.sayed.mehrabalmomen.presentation.base.UiText

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
    return UiText.Resource(resId)
}
