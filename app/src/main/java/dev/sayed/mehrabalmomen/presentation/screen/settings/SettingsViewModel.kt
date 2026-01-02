package dev.sayed.mehrabalmomen.presentation.screen.settings

import SettingsUiState
import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.data.AzanManager
import dev.sayed.mehrabalmomen.domain.entity.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.Madhab
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.domain.repository.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import dev.sayed.mehrabalmomen.presentation.components.SelectionItem
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val azanManager: AzanManager
) : BaseViewModel<SettingsUiState, SettingsEffect>(SettingsUiState()),
    SettingsInteractionListener {

    init {
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.observeAppSettings().collect { appSettings ->
                updateState { state ->
                    state.copy(
                        selectedLanguage = appSettings.language.toUi(),
                        selectedTheme = appSettings.theme.toUi(),
                        selectedMadhab = appSettings.prayerSettings.madhab.toUi(),
                        selectedCalculationMethod = appSettings.prayerSettings.calculationMethod.toUi(),
                        location = SettingsUiState.LocationUiState(
                            country = appSettings.prayerSettings.location.country,
                            city = appSettings.prayerSettings.location.state
                        )
                    )
                }
                rebuildSections()
                azanManager.rescheduleTodayPrayerAlarms()
            }
        }
    }

    private fun rebuildSections() {
        val state = screenState.value
        val sections = listOf(
            SettingsUiState.SettingsSectionUiState(
                titleRes = R.string.general,
                items = listOf(
                    SettingsUiState.SettingsItemUiState(
                        icon = R.drawable.ic_language,
                        title = R.string.language,
                        description = state.selectedLanguage.nameRes,
                        action = SettingsUiState.SettingsAction.LANGUAGE
                    ),
                    SettingsUiState.SettingsItemUiState(
                        icon = R.drawable.ic_theme,
                        title = R.string.theme,
                        description = state.selectedTheme.value,
                        action = SettingsUiState.SettingsAction.THEME
                    ),
                    SettingsUiState.SettingsItemUiState(
                        icon = R.drawable.ic_location,
                        title = R.string.location,
                        action = SettingsUiState.SettingsAction.LOCATION,
                        descriptionText = state.location.country.plus(", ")
                            .plus(state.location.city)

                    )
                )
            ),
            SettingsUiState.SettingsSectionUiState(
                titleRes = R.string.prayer_times,
                items = listOf(
                    SettingsUiState.SettingsItemUiState(
                        icon = R.drawable.ic_calculation_method,
                        title = R.string.calculation_method,
                        description = state.selectedCalculationMethod.value,
                        action = SettingsUiState.SettingsAction.CALCULATION_METHOD
                    ),
                    SettingsUiState.SettingsItemUiState(
                        icon = R.drawable.ic_madhab,
                        title = R.string.madhab,
                        description = state.selectedMadhab.value,
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

    private val dialogOpeners: Map<SettingsUiState.SettingsAction, () -> Unit> = mapOf(
        SettingsUiState.SettingsAction.LANGUAGE to { openLanguageDialog() },
        SettingsUiState.SettingsAction.THEME to { openThemeDialog() },
        SettingsUiState.SettingsAction.MADHAB to { openMadhabDialog() },
        SettingsUiState.SettingsAction.CALCULATION_METHOD to { openCalculationMethodDialog() }
    )

    private fun openLanguageDialog() {
        val state = screenState.value
        openDialog(
            type = SettingsUiState.SelectionDialogType.LANGUAGE,
            titleRes = R.string.choose_language,
            descriptionRes = R.string.language_description,
            options = SettingsUiState.Language.entries.map { SelectionItem(it.nameRes) },
            selectedIndex = SettingsUiState.Language.entries.indexOf(state.selectedLanguage)
        )
    }

    private fun openThemeDialog() {
        val state = screenState.value
        openDialog(
            type = SettingsUiState.SelectionDialogType.THEME,
            titleRes = R.string.choose_theme,
            descriptionRes = R.string.theme_description,
            options = SettingsUiState.ThemeState.entries.map { SelectionItem(it.value) },
            selectedIndex = SettingsUiState.ThemeState.entries.indexOf(state.selectedTheme)
        )
    }

    private fun openMadhabDialog() {
        val state = screenState.value
        openDialog(
            type = SettingsUiState.SelectionDialogType.MADHAB,
            titleRes = R.string.choose_madhab,
            descriptionRes = R.string.madhab_description,
            options = SettingsUiState.MadhabState.entries.map { SelectionItem(it.value) },
            selectedIndex = SettingsUiState.MadhabState.entries.indexOf(state.selectedMadhab)
        )
    }

    private fun openCalculationMethodDialog() {
        val state = screenState.value
        openDialog(
            type = SettingsUiState.SelectionDialogType.CALCULATION_METHOD,
            titleRes = R.string.choose_calculation_method,
            descriptionRes = R.string.calculation_method_description,
            options = SettingsUiState.CalculationMethod.entries.map { SelectionItem(it.value) },
            selectedIndex = SettingsUiState.CalculationMethod.entries.indexOf(state.selectedCalculationMethod)
        )
    }

    private fun openDialog(
        type: SettingsUiState.SelectionDialogType,
        titleRes: Int,
        descriptionRes: Int? = null,
        options: List<SelectionItem>,
        selectedIndex: Int
    ) {
        updateState {
            it.copy(
                dialog = SettingsUiState.SelectionDialogUiState(
                    titleRes = titleRes,
                    descriptionRes = descriptionRes,
                    options = options,
                    selectedIndex = selectedIndex,
                    type = type
                )
            )
        }
    }

    private val dialogHandlers: Map<SettingsUiState.SelectionDialogType, (Int) -> Unit> = mapOf(
        SettingsUiState.SelectionDialogType.LANGUAGE to { index ->
            val selected = SettingsUiState.Language.entries[index]
            saveLanguage(selected.toDomain())
            updateState { it.copy(selectedLanguage = selected) }
        },
        SettingsUiState.SelectionDialogType.THEME to { index ->
            val selected = SettingsUiState.ThemeState.entries[index]
            saveTheme(selected.toDomain())
            updateState { it.copy(selectedTheme = selected) }
        },
        SettingsUiState.SelectionDialogType.MADHAB to { index ->
            val selected = SettingsUiState.MadhabState.entries[index]
            saveMadhab(selected.toDomain())
            updateState { it.copy(selectedMadhab = selected) }
        },
        SettingsUiState.SelectionDialogType.CALCULATION_METHOD to { index ->
            val selected = SettingsUiState.CalculationMethod.entries[index]
            saveCalculationMethod(selected.toDomain())
            updateState { it.copy(selectedCalculationMethod = selected) }
        }
    )

    private fun saveLanguage(language: AppSettings.Language) {
        viewModelScope.launch {
            settingsRepository.saveLanguage(language)
        }
    }

    private fun saveTheme(theme: AppSettings.Theme) {
        viewModelScope.launch {
            settingsRepository.saveTheme(theme)
        }
    }

    private fun saveMadhab(madhab: Madhab) {
        viewModelScope.launch {
            settingsRepository.saveMadhab(madhab)
        }
    }

    private fun saveCalculationMethod(method: CalculationMethod) {
        viewModelScope.launch {
            settingsRepository.saveCalculationMethod(method)
        }
    }

    override fun onItemClick(action: SettingsUiState.SettingsAction) {
        dialogOpeners[action]?.invoke() ?: when (action) {
            SettingsUiState.SettingsAction.LOCATION -> sendEffect(SettingsEffect.NavigateToLocation)
            SettingsUiState.SettingsAction.HELP_FEEDBACK -> sendEffect(SettingsEffect.NavigateToHelpFeedback)
            SettingsUiState.SettingsAction.RATE_APP -> sendEffect(SettingsEffect.NavigateToRateApp)
            SettingsUiState.SettingsAction.ABOUT -> sendEffect(SettingsEffect.NavigateToAbout)
            else -> {}
        }
    }

    override fun onDialogConfirm(index: Int) {
        val dialog = screenState.value.dialog ?: return
        dialogHandlers[dialog.type]?.invoke(index)
        updateState { it.copy(dialog = null) }
        rebuildSections()
    }

    override fun onDialogDismiss() {
        updateState { it.copy(dialog = null) }
    }


    override fun onLocationClick() {
        sendEffect(SettingsEffect.NavigateToLocation)
    }

    override fun onCalculationMethodClick() {}
    override fun onHelpFeedbackClick() {}
    override fun onRateAppClick() {
        sendEffect(SettingsEffect.NavigateToRateApp)
    }

    override fun onAboutClick() {}
}