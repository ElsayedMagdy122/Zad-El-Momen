package dev.sayed.mehrabalmomen.presentation.screen.settings

import dev.sayed.mehrabalmomen.R

 fun getMoazenName(moazen: SettingsUiState.Moazen): Int {
    return when(moazen) {
        SettingsUiState.Moazen.AZAN_ABED_ALBASET -> R.string.mozan_abed_albaset
        SettingsUiState.Moazen.AZAN_MAKKAH -> R.string.mozan_makkah
        SettingsUiState.Moazen.AZAN_MANSOOR_AL_ZAHRANI -> R.string.mozan_mansoor_alzahrani
        SettingsUiState.Moazen.AZAN_MISHARY_ALAFASI -> R.string.mozan_mishary_alafasi
        SettingsUiState.Moazen.AZAN_MOHAMMED_ALMENSHWY -> R.string.mozan_mohammed_almenshawy
        SettingsUiState.Moazen.AZAN_NASSER_ALQATAMI -> R.string.mozan_nasser_alqatami
        SettingsUiState.Moazen.AZAN_SUHAIB_KHATBA -> R.string.mozan_suhaib_khatba
    }
}