package dev.sayed.mehrabalmomen.presentation.screen.settings

import dev.sayed.mehrabalmomen.presentation.base.UiText

data class SettingsUiState(
    val sections: List<SettingsSectionUiState> = emptyList(),
    val selectedLanguage: Language = Language.ARABIC,
    val selectedTheme: ThemeState = ThemeState.SYSTEM,
    val selectedMadhab: MadhabState = MadhabState.SHAFI,
    val selectedCalculationMethod: CalculationMethod = CalculationMethod.EGYPTIAN,
    val location: LocationUiState = LocationUiState(),
    val dialog: SelectionDialogUiState? = null,
    val isSupportAvailable: Boolean = false,
    val selectedFontSize: QuranFontSize = QuranFontSize.SMALL,
    val selectedMoazen: Moazen = Moazen.AZAN_MAKKAH,
    val selectedTafseer: TafseerType = TafseerType.MOKHTASAR,
    val isCompanionEnabled: Boolean = true
) {
    data class LocationUiState(
        val country: String = "Unknown",
        val city: String = "Unknown"
    )

    data class SettingsSectionUiState(
        val title: UiText,
        val items: List<SettingsItemUiState>
    )

    data class SettingsItemUiState(
        val icon: Int = 0,
        val title: UiText,
        val description: UiText? = null,
        val action: SettingsAction,
        val value: Boolean? = null
    )

    enum class ThemeState {
        LIGHT,
        DARK,
        SYSTEM
    }

    enum class MadhabState {
        SHAFI,
        HANAFI
    }

    enum class Language {
        ENGLISH,
        ARABIC
    }

    enum class SelectionDialogType {
        LANGUAGE,
        THEME,
        MADHAB,
        CALCULATION_METHOD,
        SUPPORT,
        MOAZEN,
        TAFSEER,
        FONT_SIZE
    }

    data class SelectionDialogUiState(
        val title: UiText,
        val description: UiText? = null,
        val options: List<SelectionItem>,
        val selectedIndex: Int,
        val type: SelectionDialogType
    )

    enum class CalculationMethod {
        MUSLIM_WORLD_LEAGUE,
        EGYPTIAN,
        KARACHI,
        UMM_AL_QURA,
        DUBAI,
        QATAR,
        KUWAIT,
        MOONSIGHTING_COMMITTEE,
        SINGAPORE,
        NORTH_AMERICA
    }

    enum class SettingsAction {
        LANGUAGE,
        THEME,
        LOCATION,
        NOTIFICATIONS,
        CALCULATION_METHOD,
        MOAZEN,
        TEXT_FONT,
        TAFSEER,
        MADHAB,
        HELP_FEEDBACK,
        RATE_APP,
        ABOUT,
        FAQ,
        PRIVACY_POLICY,
        CONTACT_US,
        COMPANION,
        WIDGET
    }

    enum class QuranFontSize(val sizeSp: Int) {
        SMALL(20),
        MEDIUM(24),
        LARGE(28),
        EXTRA_LARGE(32)
    }

    enum class Moazen(val fileName: String, val id: Int) {
        AZAN_ABED_ALBASET("azan_abed_albaset.mp3", 0),
        AZAN_MAKKAH("azan_makkah.mp3", 1),
        AZAN_MANSOOR_AL_ZAHRANI("azan_mansoor_al_zahrani.mp3", 2),
        AZAN_MISHARY_ALAFASI("azan_mishary_alafasi.mp3", 3),
        AZAN_MOHAMMED_ALMENSHWY("azan_mohammed_almenshawy.mp3", 4),
        AZAN_NASSER_ALQATAMI("azan_nasser_alqatami.mp3", 5),
        AZAN_SUHAIB_KHATBA("azan_suhaib_khatba.mp3", 6)
    }

    enum class TafseerType(val fileName: String) {
        MOKHTASAR("tf_ab_mokhtasar_ar.json"),
        MOYASSAR("tf_moyasser.json")
    }
}

data class SelectionItem(
    val title: UiText,
    val isSelected: Boolean = false
)
