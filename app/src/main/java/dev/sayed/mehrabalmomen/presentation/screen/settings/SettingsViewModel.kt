package dev.sayed.mehrabalmomen.presentation.screen.settings

import SettingsUiState
import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.domain.repository.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import dev.sayed.mehrabalmomen.presentation.components.SelectionItem
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : BaseViewModel<SettingsUiState, SettingsEffect>(SettingsUiState()),
    SettingsInteractionListener {

    init {
        observeSettings()
        buildSections()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.observeAppSettings().collect { appSettings ->
                val language = appSettings.language.toUi()
                val theme = appSettings.theme.toUi()
                val madhab = appSettings.madhab.toUi()

                updateState { state ->
                    state.copy(
                        selectedLanguage = language,
                        selectedTheme = theme,
                        selectedMadhab = madhab
                    )
                }

                buildSections(
                    selectedLanguage = language,
                    selectedTheme = theme,
                    selectedMadhab = madhab
                )
            }
        }
    }

    fun onLanguageSelected(language: SettingsUiState.Language) {
        viewModelScope.launch {
            settingsRepository.saveLanguage(language.toDomain())
        }
    }

    fun onThemeSelected(theme: SettingsUiState.ThemeState) {
        viewModelScope.launch {
            settingsRepository.saveTheme(theme.toDomain())
        }
    }

    fun onMadhabSelected(madhab: SettingsUiState.MadhabState) {
        viewModelScope.launch {
            settingsRepository.saveMadhab(madhab.toDomain())
        }
    }

    private fun buildSections(
        selectedLanguage: SettingsUiState.Language = screenState.value.selectedLanguage,
        selectedTheme: SettingsUiState.ThemeState = screenState.value.selectedTheme,
        selectedMadhab: SettingsUiState.MadhabState = screenState.value.selectedMadhab
    ) {
        val sections = listOf(
            SettingsUiState.SettingsSectionUiState(
                titleRes = R.string.general,
                items = listOf(
                    SettingsUiState.SettingsItemUiState(
                        icon = R.drawable.ic_language,
                        title = R.string.language,
                        description = selectedLanguage.nameRes,
                        action = SettingsUiState.SettingsAction.LANGUAGE
                    ),
                    SettingsUiState.SettingsItemUiState(
                        icon = R.drawable.ic_theme,
                        title = R.string.theme,
                        description = selectedTheme.value,
                        action = SettingsUiState.SettingsAction.THEME
                    ),
                    SettingsUiState.SettingsItemUiState(
                        icon = R.drawable.ic_location,
                        title = R.string.location,
                        action = SettingsUiState.SettingsAction.LOCATION
                    )
                )
            ),
            SettingsUiState.SettingsSectionUiState(
                titleRes = R.string.prayer_times,
                items = listOf(
                    SettingsUiState.SettingsItemUiState(
                        icon = R.drawable.ic_madhab,
                        title = R.string.madhab,
                        description = selectedMadhab.value,
                        action = SettingsUiState.SettingsAction.MADHAB
                    )
                )
            ),
            SettingsUiState.SettingsSectionUiState(
                titleRes = R.string.support,
                items = listOf(
                    SettingsUiState.SettingsItemUiState(
                        icon = R.drawable.ic_help,
                        title = R.string.help_feedback,
                        action = SettingsUiState.SettingsAction.HELP_FEEDBACK
                    ),
                    SettingsUiState.SettingsItemUiState(
                        icon = R.drawable.ic_star_rate,
                        title = R.string.rate_app,
                        action = SettingsUiState.SettingsAction.RATE_APP
                    ),
                    SettingsUiState.SettingsItemUiState(
                        icon = R.drawable.ic_help,
                        title = R.string.about,
                        action = SettingsUiState.SettingsAction.ABOUT
                    )
                )
            )
        )
        updateState { it.copy(sections = sections) }
    }

    override fun onItemClick(action: SettingsUiState.SettingsAction) {
        val state = screenState.value
        when (action) {
            SettingsUiState.SettingsAction.LANGUAGE -> openDialog(
                SettingsUiState.SelectionDialogType.LANGUAGE,
                R.string.select_language,
                SettingsUiState.Language.entries.map { SelectionItem(it.nameRes) },
                SettingsUiState.Language.entries.indexOf(state.selectedLanguage)
            )

            SettingsUiState.SettingsAction.THEME -> openDialog(
                SettingsUiState.SelectionDialogType.THEME,
                R.string.select_theme,
                SettingsUiState.ThemeState.entries.map { SelectionItem(it.value) },
                SettingsUiState.ThemeState.entries.indexOf(state.selectedTheme)
            )

            SettingsUiState.SettingsAction.MADHAB -> openDialog(
                SettingsUiState.SelectionDialogType.MADHAB,
                R.string.select_madhab,
                SettingsUiState.MadhabState.entries.map { SelectionItem(it.value) },
                SettingsUiState.MadhabState.entries.indexOf(state.selectedMadhab)
            )

            SettingsUiState.SettingsAction.LOCATION -> onLocationClick()
            SettingsUiState.SettingsAction.CALCULATION_METHOD -> onCalculationMethodClick()
            SettingsUiState.SettingsAction.HELP_FEEDBACK -> onHelpFeedbackClick()
            SettingsUiState.SettingsAction.RATE_APP -> onRateAppClick()
            SettingsUiState.SettingsAction.ABOUT -> onAboutClick()
        }
    }

    private fun openDialog(
        type: SettingsUiState.SelectionDialogType,
        titleRes: Int,
        items: List<SelectionItem>,
        selectedIndex: Int
    ) {
        updateState {
            it.copy(
                dialog = SettingsUiState.SelectionDialogUiState(
                    titleRes = titleRes,
                    options = items,
                    selectedIndex = selectedIndex,
                    type = type
                )
            )
        }
    }

    override fun onDialogConfirm(index: Int) {
        val dialog = screenState.value.dialog ?: return

        when (dialog.type) {
            SettingsUiState.SelectionDialogType.LANGUAGE -> {
                val selected = SettingsUiState.Language.entries[index]
                onLanguageSelected(selected)
                updateState {
                    it.copy(selectedLanguage = selected, dialog = null)
                }
                buildSections(selectedLanguage = selected)
            }

            SettingsUiState.SelectionDialogType.THEME -> {
                val selected = SettingsUiState.ThemeState.entries[index]
                onThemeSelected(selected)
                updateState {
                    it.copy(selectedTheme = selected, dialog = null)
                }
                buildSections(selectedTheme = selected)
            }

            SettingsUiState.SelectionDialogType.MADHAB -> {
                val selected = SettingsUiState.MadhabState.entries[index]
                onMadhabSelected(selected)
                updateState {
                    it.copy(selectedMadhab = selected, dialog = null)
                }
                buildSections(selectedMadhab = selected)
            }
        }
    }

    override fun onDialogDismiss() {
        updateState { it.copy(dialog = null) }
    }

    override fun onLocationClick() {}
    override fun onCalculationMethodClick() {}
    override fun onHelpFeedbackClick() {}
    override fun onRateAppClick() {}
    override fun onAboutClick() {}
}