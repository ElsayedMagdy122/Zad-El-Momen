package dev.sayed.mehrabalmomen.presentation.screen.settings

import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.*
import dev.sayed.mehrabalmomen.presentation.base.UiText
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

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
        val icon: DrawableResource,
        val title: UiText,
        val description: UiText? = null,
        val action: SettingsAction,
        val value: Boolean? = null
    )

    enum class ThemeState(val resId: StringResource) {
        LIGHT(Res.string.light),
        DARK(Res.string.dark),
        SYSTEM(Res.string.system_default)
    }

    enum class MadhabState(val resId: StringResource) {
        SHAFI(Res.string.shafi),
        HANAFI(Res.string.hanafi)
    }

    enum class Language(val resId: StringResource) {
        ENGLISH(Res.string.english),
        ARABIC(Res.string.arabic)
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

    enum class CalculationMethod(val resId: StringResource) {
        MUSLIM_WORLD_LEAGUE(Res.string.muslim_world_league),
        EGYPTIAN(Res.string.egyptian),
        KARACHI(Res.string.karachi),
        UMM_AL_QURA(Res.string.umm_al_qura),
        DUBAI(Res.string.dubai),
        QATAR(Res.string.qatar),
        KUWAIT(Res.string.kuwait),
        MOONSIGHTING_COMMITTEE(Res.string.moonsighting_committee),
        SINGAPORE(Res.string.singapore),
        NORTH_AMERICA(Res.string.north_america)
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

    enum class QuranFontSize(val resId: StringResource, val sizeSp: Int) {
        SMALL(Res.string.small, 20),
        MEDIUM(Res.string.medium, 24),
        LARGE(Res.string.large, 28),
        EXTRA_LARGE(Res.string.extra_large, 32)
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

    enum class TafseerType(val resId: StringResource, val fileName: String) {
        MOKHTASAR(Res.string.tafseer_mokhtasar, "tf_ab_mokhtasar_ar.json"),
        MOYASSAR(Res.string.tafseer_moyasser, "tf_moyasser.json")
    }
}
