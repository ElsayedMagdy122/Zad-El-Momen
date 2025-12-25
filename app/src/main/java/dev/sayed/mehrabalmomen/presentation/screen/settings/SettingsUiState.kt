import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.presentation.components.SelectionItem


data class SettingsUiState(
    val sections: List<SettingsSectionUiState> = emptyList(),

    val selectedLanguage: Language = Language.ARABIC,
    val selectedTheme: ThemeState = ThemeState.SYSTEM,
    val selectedMadhab: MadhabState = MadhabState.SHAFI,

    val dialog: SelectionDialogUiState? = null
) {

    data class SettingsSectionUiState(
        val titleRes: Int,
        val items: List<SettingsItemUiState>
    )

    data class SettingsItemUiState(
        val icon: Int,
        val title: Int,
        val description: Int = 0,
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
        MADHAB
    }

    data class SelectionDialogUiState(
        val titleRes: Int,
        val options: List<SelectionItem>,
        val selectedIndex: Int,
        val type: SelectionDialogType
    )

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
