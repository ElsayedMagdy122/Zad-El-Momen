import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.presentation.components.SelectionItem


data class SettingsUiState(
    val sections: List<SettingsSectionUiState> = emptyList(),

    val selectedLanguage: Language = Language.ARABIC,
    val selectedTheme: ThemeState = ThemeState.SYSTEM,
    val selectedMadhab: MadhabState = MadhabState.SHAFI,
    val selectedCalculationMethod: CalculationMethod = CalculationMethod.EGYPTIAN,
    val location: LocationUiState = LocationUiState(),
    val dialog: SelectionDialogUiState? = null
) {
    data class LocationUiState(
        val country: String= "Unknown",
        val city: String = "Unknown"
    )
    data class SettingsSectionUiState(
        val titleRes: Int,
        val items: List<SettingsItemUiState>
    )

    data class SettingsItemUiState(
        val icon: Int,
        val title: Int,
        val description: Int = 0,
        val descriptionText: String? = null,
        val action: SettingsAction
    )

    enum class ThemeState(val value: Int) {
        LIGHT(R.string.light),
        DARK(R.string.dark),
        SYSTEM(R.string.system_default)
    }

    enum class MadhabState(val value: Int) {
        SHAFI(R.string.shafi),
        HANAFI(R.string.hanafi)
    }

    enum class Language(val nameRes: Int) {
        ENGLISH(R.string.english),
        ARABIC(R.string.arabic)
    }

    enum class SelectionDialogType {
        LANGUAGE,
        THEME,
        MADHAB,
        CALCULATION_METHOD
    }

    data class SelectionDialogUiState(
        val titleRes: Int,
        val descriptionRes: Int? = null,
        val options: List<SelectionItem>,
        val selectedIndex: Int,
        val type: SelectionDialogType
    )
    enum class CalculationMethod(val value: Int) {
        MUSLIM_WORLD_LEAGUE(value = (R.string.muslim_world_league)),
        EGYPTIAN(value = (R.string.egyptian)),
        KARACHI(value = (R.string.karachi)),
        UMM_AL_QURA(value =(R.string.umm_al_qura)),
        DUBAI(value = (R.string.dubai)),
        QATAR(value = (R.string.qatar)),
        KUWAIT(value = (R.string.kuwait)),
        MOONSIGHTING_COMMITTEE(value =(R.string.moonsighting_committee)),
        SINGAPORE(value = (R.string.singapore)),
        NORTH_AMERICA(value = (R.string.north_america))
    }
    enum class SettingsAction {
        LANGUAGE,
        THEME,
        LOCATION,
        CALCULATION_METHOD,
        MADHAB,
        HELP_FEEDBACK,
        RATE_APP,
        ABOUT
    }
}
