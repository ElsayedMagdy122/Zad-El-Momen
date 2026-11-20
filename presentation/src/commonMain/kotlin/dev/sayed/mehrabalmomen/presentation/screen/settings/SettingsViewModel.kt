package dev.sayed.mehrabalmomen.presentation.screen.settings

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.domain.analytics.AnalyticsTracker
import dev.sayed.mehrabalmomen.domain.billing.BillingEvent
import dev.sayed.mehrabalmomen.domain.billing.BillingService
import dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.prayer.Madhab
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.domain.model.DonationPromptAction
import dev.sayed.mehrabalmomen.domain.model.audio.AudioSource
import dev.sayed.mehrabalmomen.domain.repository.audio.AudioPlayer
import dev.sayed.mehrabalmomen.domain.repository.companion.CompanionRepository
import dev.sayed.mehrabalmomen.domain.repository.platform.WidgetPinProvider
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.domain.usecase.PrayerSchedulingUseCase
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import dev.sayed.mehrabalmomen.presentation.base.UiText
import dev.sayed.mehrabalmomen.presentation.base.setAppLocale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.al_tafseer
import zadelmomen.presentation.generated.resources.calculation_method
import zadelmomen.presentation.generated.resources.calculation_method_description
import zadelmomen.presentation.generated.resources.choose_calculation_method
import zadelmomen.presentation.generated.resources.choose_font_size
import zadelmomen.presentation.generated.resources.choose_language
import zadelmomen.presentation.generated.resources.choose_madhab
import zadelmomen.presentation.generated.resources.choose_moazen
import zadelmomen.presentation.generated.resources.choose_tafseer
import zadelmomen.presentation.generated.resources.choose_theme
import zadelmomen.presentation.generated.resources.companion_mode
import zadelmomen.presentation.generated.resources.contact_us
import zadelmomen.presentation.generated.resources.faq
import zadelmomen.presentation.generated.resources.font_size_description
import zadelmomen.presentation.generated.resources.general
import zadelmomen.presentation.generated.resources.growth
import zadelmomen.presentation.generated.resources.help_feedback
import zadelmomen.presentation.generated.resources.ic_alarm_clock
import zadelmomen.presentation.generated.resources.ic_bug
import zadelmomen.presentation.generated.resources.ic_buy_coffee
import zadelmomen.presentation.generated.resources.ic_calculation_method
import zadelmomen.presentation.generated.resources.ic_check_circle
import zadelmomen.presentation.generated.resources.ic_contact_us
import zadelmomen.presentation.generated.resources.ic_help
import zadelmomen.presentation.generated.resources.ic_language
import zadelmomen.presentation.generated.resources.ic_location
import zadelmomen.presentation.generated.resources.ic_moazen
import zadelmomen.presentation.generated.resources.ic_mosque_02
import zadelmomen.presentation.generated.resources.ic_prayer_times_selected
import zadelmomen.presentation.generated.resources.ic_privacy
import zadelmomen.presentation.generated.resources.ic_rafeek
import zadelmomen.presentation.generated.resources.ic_star_rate
import zadelmomen.presentation.generated.resources.ic_tafseer
import zadelmomen.presentation.generated.resources.ic_text_font
import zadelmomen.presentation.generated.resources.ic_theme
import zadelmomen.presentation.generated.resources.language
import zadelmomen.presentation.generated.resources.language_description
import zadelmomen.presentation.generated.resources.legal
import zadelmomen.presentation.generated.resources.location
import zadelmomen.presentation.generated.resources.madhab
import zadelmomen.presentation.generated.resources.madhab_description
import zadelmomen.presentation.generated.resources.moazen
import zadelmomen.presentation.generated.resources.moazen_description
import zadelmomen.presentation.generated.resources.mozan_abed_albaset
import zadelmomen.presentation.generated.resources.mozan_makkah
import zadelmomen.presentation.generated.resources.mozan_mansoor_alzahrani
import zadelmomen.presentation.generated.resources.mozan_mishary_alafasi
import zadelmomen.presentation.generated.resources.mozan_mohammed_almenshawy
import zadelmomen.presentation.generated.resources.mozan_nasser_alqatami
import zadelmomen.presentation.generated.resources.mozan_suhaib_khatba
import zadelmomen.presentation.generated.resources.notifications
import zadelmomen.presentation.generated.resources.prayer
import zadelmomen.presentation.generated.resources.prayer_times_widget
import zadelmomen.presentation.generated.resources.prayer_widget_desc
import zadelmomen.presentation.generated.resources.privacy_and_policy
import zadelmomen.presentation.generated.resources.quran
import zadelmomen.presentation.generated.resources.rate_app
import zadelmomen.presentation.generated.resources.success
import zadelmomen.presentation.generated.resources.support
import zadelmomen.presentation.generated.resources.support_1
import zadelmomen.presentation.generated.resources.support_2
import zadelmomen.presentation.generated.resources.support_3
import zadelmomen.presentation.generated.resources.support_4
import zadelmomen.presentation.generated.resources.support_5
import zadelmomen.presentation.generated.resources.support_description
import zadelmomen.presentation.generated.resources.support_developer
import zadelmomen.presentation.generated.resources.tafseer_description
import zadelmomen.presentation.generated.resources.tafseer_mokhtasar
import zadelmomen.presentation.generated.resources.tafseer_moyasser
import zadelmomen.presentation.generated.resources.text_font
import zadelmomen.presentation.generated.resources.thank_you_for_support
import zadelmomen.presentation.generated.resources.theme
import zadelmomen.presentation.generated.resources.theme_description
import dev.sayed.mehrabalmomen.design_system.base.UiText as DesignUiText

private const val SUPPORT_1 = "support_1"
private const val SUPPORT_5 = "support_5"
private const val SUPPORT_10 = "support_10"
private const val SUPPORT_25 = "support_25"
private const val SUPPORT_100 = "support_100"

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val companionRepository: CompanionRepository,
    private val prayerSchedulingUseCase: PrayerSchedulingUseCase,
    private val billingService: BillingService,
    private val analyticsTracker: AnalyticsTracker,
    private val widgetPinProvider: WidgetPinProvider,
    private val audioPlayer: AudioPlayer
) : BaseViewModel<SettingsUiState, SettingsEffect>(SettingsUiState()),
    SettingsInteractionListener {

    private val supportProductIds = listOf(
        SUPPORT_1,
        SUPPORT_5,
        SUPPORT_10,
        SUPPORT_25,
        SUPPORT_100
    )

    init {
        observeSettings()
        observeBillingData()
        observeQuranFont()
        observeTafseer()
        observeSelectedMoazen()
        observeCompanionEnabled()
    }

    private fun observeBillingData() {
        viewModelScope.launch {
            billingService.products.collect { products ->
                val available = products.isNotEmpty()
                updateState {
                    it.copy(isSupportAvailable = available)
                }
                rebuildSections()
            }
        }
        viewModelScope.launch {
            billingService.events.collect { event ->
                when (event) {
                    BillingEvent.Success -> {
                        viewModelScope.launch {
                            settingsRepository.onDonationPromptAction(DonationPromptAction.DONATED, 0L)
                        }
                        sendEffect(
                            SettingsEffect.ShowToast(
                                ToastDetails(
                                    title = DesignUiText.ResString(Res.string.success),
                                    message = DesignUiText.ResString(Res.string.thank_you_for_support),
                                    icon = Res.drawable.ic_check_circle
                                )
                            )
                        )
                    }
                    BillingEvent.Pending -> {}
                    BillingEvent.ProcessingError, BillingEvent.GeneralError -> {}
                }
            }
        }
    }

    fun launchDonationFlow(activity: Any, productId: String) {
        billingService.donate(activity, productId)
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
                prayerSchedulingUseCase.rescheduleTodayPrayerAlarms()
            }
        }
    }

    private fun observeQuranFont() {
        viewModelScope.launch {
            settingsRepository.observeQuranFontSize().collect { size ->
                val fontSize = SettingsUiState.QuranFontSize.entries
                    .firstOrNull { it.sizeSp == size }
                    ?: SettingsUiState.QuranFontSize.MEDIUM

                updateState { it.copy(selectedFontSize = fontSize) }
                rebuildSections()
            }
        }
    }

    private fun observeTafseer() {
        viewModelScope.launch {
            settingsRepository.observeTafseer().collect { fileName ->
                val tafseer = SettingsUiState.TafseerType.entries
                    .firstOrNull { it.fileName == fileName }
                    ?: SettingsUiState.TafseerType.MOKHTASAR

                updateState { it.copy(selectedTafseer = tafseer) }
                rebuildSections()
            }
        }
    }

    private fun observeSelectedMoazen() {
        viewModelScope.launch {
            settingsRepository.observeSelectedMoazen().collect { fileName ->
                val moazen = SettingsUiState.Moazen.entries.firstOrNull { it.fileName == fileName }
                    ?: SettingsUiState.Moazen.AZAN_MAKKAH

                updateState { it.copy(selectedMoazen = moazen) }
                rebuildSections()
            }
        }
    }

    private fun observeCompanionEnabled() {
        viewModelScope.launch {
            companionRepository.observeCompanionEnabled().collect { enabled ->
                updateState { it.copy(isCompanionEnabled = enabled) }
                rebuildSections()
            }
        }
    }

    private fun rebuildSections() {
        val state = screenState.value

        val supportItems = listOf(
            SettingsUiState.SettingsItemUiState(
                icon = Res.drawable.ic_bug,
                title = UiText.StringRes(Res.string.help_feedback),
                action = SettingsUiState.SettingsAction.HELP_FEEDBACK
            ),
            SettingsUiState.SettingsItemUiState(
                icon = Res.drawable.ic_contact_us,
                title = UiText.StringRes(Res.string.contact_us),
                action = SettingsUiState.SettingsAction.CONTACT_US
            ),
            SettingsUiState.SettingsItemUiState(
                icon = Res.drawable.ic_help,
                title = UiText.StringRes(Res.string.faq),
                action = SettingsUiState.SettingsAction.FAQ
            ),
            SettingsUiState.SettingsItemUiState(
                icon = Res.drawable.ic_buy_coffee,
                title = UiText.StringRes(Res.string.support_developer),
                action = SettingsUiState.SettingsAction.ABOUT
            )
        )

        val sections = listOf(
            SettingsUiState.SettingsSectionUiState(
                title = UiText.StringRes(Res.string.general),
                items = listOf(
                    SettingsUiState.SettingsItemUiState(
                        icon = Res.drawable.ic_language,
                        title = UiText.StringRes(Res.string.language),
                        description = UiText.StringRes(state.selectedLanguage.resId),
                        action = SettingsUiState.SettingsAction.LANGUAGE
                    ),
                    SettingsUiState.SettingsItemUiState(
                        icon = Res.drawable.ic_theme,
                        title = UiText.StringRes(Res.string.theme),
                        description = UiText.StringRes(state.selectedTheme.resId),
                        action = SettingsUiState.SettingsAction.THEME
                    ),
                    SettingsUiState.SettingsItemUiState(
                        icon = Res.drawable.ic_location,
                        title = UiText.StringRes(Res.string.location),
                        description = UiText.DynamicString("${state.location.city}, ${state.location.country}"),
                        action = SettingsUiState.SettingsAction.LOCATION
                    ),
                    SettingsUiState.SettingsItemUiState(
                        icon = Res.drawable.ic_alarm_clock,
                        title = UiText.StringRes(Res.string.notifications),
                        action = SettingsUiState.SettingsAction.NOTIFICATIONS
                    ),
                    SettingsUiState.SettingsItemUiState(
                        icon = Res.drawable.ic_rafeek,
                        title = UiText.StringRes(Res.string.companion_mode),
                        value = state.isCompanionEnabled,
                        action = SettingsUiState.SettingsAction.COMPANION
                    )
                )
            ),

            SettingsUiState.SettingsSectionUiState(
                title = UiText.StringRes(Res.string.prayer),
                items = listOf(
                    SettingsUiState.SettingsItemUiState(
                        icon = Res.drawable.ic_calculation_method,
                        title = UiText.StringRes(Res.string.calculation_method),
                        description = UiText.StringRes(state.selectedCalculationMethod.resId),
                        action = SettingsUiState.SettingsAction.CALCULATION_METHOD
                    ),
                    SettingsUiState.SettingsItemUiState(
                        icon = Res.drawable.ic_mosque_02,
                        title = UiText.StringRes(Res.string.madhab),
                        description = UiText.StringRes(state.selectedMadhab.resId),
                        action = SettingsUiState.SettingsAction.MADHAB
                    ),
                    SettingsUiState.SettingsItemUiState(
                        icon = Res.drawable.ic_moazen,
                        title = UiText.StringRes(Res.string.moazen),
                        description = getMoazenName(state.selectedMoazen),
                        action = SettingsUiState.SettingsAction.MOAZEN
                    ),
                    SettingsUiState.SettingsItemUiState(
                        icon = Res.drawable.ic_prayer_times_selected,
                        title = UiText.StringRes(Res.string.prayer_times_widget),
                        description = UiText.StringRes(Res.string.prayer_widget_desc),
                        action = SettingsUiState.SettingsAction.WIDGET
                    )
                )
            ),

            SettingsUiState.SettingsSectionUiState(
                title = UiText.StringRes(Res.string.quran),
                items = listOf(
                    SettingsUiState.SettingsItemUiState(
                        icon = Res.drawable.ic_text_font,
                        title = UiText.StringRes(Res.string.text_font),
                        description = UiText.StringRes(state.selectedFontSize.resId),
                        action = SettingsUiState.SettingsAction.TEXT_FONT
                    ),
                    SettingsUiState.SettingsItemUiState(
                        icon = Res.drawable.ic_tafseer,
                        title = UiText.StringRes(Res.string.al_tafseer),
                        description = getTafseerName(state.selectedTafseer),
                        action = SettingsUiState.SettingsAction.TAFSEER
                    )
                )
            ),

            SettingsUiState.SettingsSectionUiState(
                title = UiText.StringRes(Res.string.support),
                items = supportItems
            ),

            SettingsUiState.SettingsSectionUiState(
                title = UiText.StringRes(Res.string.legal),
                items = listOf(
                    SettingsUiState.SettingsItemUiState(
                        icon = Res.drawable.ic_privacy,
                        title = UiText.StringRes(Res.string.privacy_and_policy),
                        action = SettingsUiState.SettingsAction.PRIVACY_POLICY
                    )
                )
            ),

            SettingsUiState.SettingsSectionUiState(
                title = UiText.StringRes(Res.string.growth),
                items = listOf(
                    SettingsUiState.SettingsItemUiState(
                        icon = Res.drawable.ic_star_rate,
                        title = UiText.StringRes(Res.string.rate_app),
                        action = SettingsUiState.SettingsAction.RATE_APP
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
        SettingsUiState.SettingsAction.CALCULATION_METHOD to { openCalculationMethodDialog() },
        SettingsUiState.SettingsAction.TEXT_FONT to { openFontSizeDialog() }
    )

    private fun getMoazenName(moazen: SettingsUiState.Moazen): UiText {
        val resId = when (moazen) {
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

    private fun getTafseerName(type: SettingsUiState.TafseerType): UiText {
        val resId = when (type) {
            SettingsUiState.TafseerType.MOKHTASAR -> Res.string.tafseer_mokhtasar
            SettingsUiState.TafseerType.MOYASSAR -> Res.string.tafseer_moyasser
        }
        return UiText.StringRes(resId)
    }

    fun playPreview(index: Int) {
        stopPreview()
        val moazen = SettingsUiState.Moazen.entries.getOrNull(index) ?: return
        val audioName = moazen.fileName.removeSuffix(".mp3")
        audioPlayer.play(AudioSource.LocalResource(audioName))
        updateState { it.copy(playingMoazenPreview = moazen) }
    }

    fun stopPreview() {
        audioPlayer.stop()
        updateState { it.copy(playingMoazenPreview = null) }
    }

    private fun openTafseerDialog() {
        val state = screenState.value

        openDialog(
            type = SettingsUiState.SelectionDialogType.TAFSEER,
            title = UiText.StringRes(Res.string.choose_tafseer),
            description = UiText.StringRes(Res.string.tafseer_description),
            options = SettingsUiState.TafseerType.entries.map {
                SelectionItem(UiText.StringRes(it.resId))
            },
            selectedIndex = SettingsUiState.TafseerType.entries.indexOf(state.selectedTafseer)
        )
    }

    private fun openFontSizeDialog() {
        val state = screenState.value

        openDialog(
            type = SettingsUiState.SelectionDialogType.FONT_SIZE,
            title = UiText.StringRes(Res.string.choose_font_size),
            description = UiText.StringRes(Res.string.font_size_description),
            options = SettingsUiState.QuranFontSize.entries.map {
                SelectionItem(UiText.StringRes(it.resId))
            },
            selectedIndex = SettingsUiState.QuranFontSize.entries.indexOf(state.selectedFontSize)
        )
    }

    private fun openLanguageDialog() {
        val state = screenState.value
        openDialog(
            type = SettingsUiState.SelectionDialogType.LANGUAGE,
            title = UiText.StringRes(Res.string.choose_language),
            description = UiText.StringRes(Res.string.language_description),
            options = SettingsUiState.Language.entries.map { SelectionItem(UiText.StringRes(it.resId)) },
            selectedIndex = SettingsUiState.Language.entries.indexOf(state.selectedLanguage)
        )
    }

    private fun openThemeDialog() {
        val state = screenState.value
        openDialog(
            type = SettingsUiState.SelectionDialogType.THEME,
            title = UiText.StringRes(Res.string.choose_theme),
            description = UiText.StringRes(Res.string.theme_description),
            options = SettingsUiState.ThemeState.entries.map { SelectionItem(UiText.StringRes(it.resId)) },
            selectedIndex = SettingsUiState.ThemeState.entries.indexOf(state.selectedTheme)
        )
    }

    private fun openMadhabDialog() {
        val state = screenState.value
        openDialog(
            type = SettingsUiState.SelectionDialogType.MADHAB,
            title = UiText.StringRes(Res.string.choose_madhab),
            description = UiText.StringRes(Res.string.madhab_description),
            options = SettingsUiState.MadhabState.entries.map { SelectionItem(UiText.StringRes(it.resId)) },
            selectedIndex = SettingsUiState.MadhabState.entries.indexOf(state.selectedMadhab)
        )
    }

    private fun openCalculationMethodDialog() {
        val state = screenState.value
        openDialog(
            type = SettingsUiState.SelectionDialogType.CALCULATION_METHOD,
            title = UiText.StringRes(Res.string.choose_calculation_method),
            description = UiText.StringRes(Res.string.calculation_method_description),
            options = SettingsUiState.CalculationMethod.entries.map { SelectionItem(UiText.StringRes(it.resId)) },
            selectedIndex = SettingsUiState.CalculationMethod.entries.indexOf(state.selectedCalculationMethod)
        )
    }

    private fun openDialog(
        type: SettingsUiState.SelectionDialogType,
        title: UiText,
        description: UiText? = null,
        options: List<SelectionItem>,
        selectedIndex: Int
    ) {
        updateState {
            it.copy(
                dialog = SettingsUiState.SelectionDialogUiState(
                    title = title,
                    description = description,
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
        },
        SettingsUiState.SelectionDialogType.SUPPORT to { index ->
            val productId = when (index) {
                0 -> SUPPORT_1
                1 -> SUPPORT_5
                2 -> SUPPORT_10
                3 -> SUPPORT_25
                4 -> SUPPORT_100
                else -> ""
            }
            sendEffect(SettingsEffect.LaunchDonation(productId))
        },
        SettingsUiState.SelectionDialogType.FONT_SIZE to { index ->
            val selected = SettingsUiState.QuranFontSize.entries[index]
            viewModelScope.launch(Dispatchers.Default) {
                settingsRepository.saveQuranFontSize(selected.sizeSp)
                updateState { it.copy(selectedFontSize = selected) }
                rebuildSections()
            }
        },
        SettingsUiState.SelectionDialogType.MOAZEN to { index ->
            val selected = SettingsUiState.Moazen.entries[index]
            updateState { it.copy(selectedMoazen = selected) }
            viewModelScope.launch {
                settingsRepository.saveSelectedMoazen(selected.fileName)
            }
            rebuildSections()
        },
        SettingsUiState.SelectionDialogType.TAFSEER to { index ->
            val selected = SettingsUiState.TafseerType.entries[index]
            updateState { it.copy(selectedTafseer = selected) }
            viewModelScope.launch {
                settingsRepository.saveTafseer(selected.fileName)
            }
        }
    )

    private fun saveLanguage(language: AppSettings.Language) {
        viewModelScope.launch {
            settingsRepository.saveLanguage(language)
            setAppLocale(language)
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
            SettingsUiState.SettingsAction.ABOUT -> {
                showSupportBottomSheet()
            }

            SettingsUiState.SettingsAction.FAQ -> sendEffect(SettingsEffect.NavigateToFAQ)
            SettingsUiState.SettingsAction.MOAZEN -> openMoazenDialog()
            SettingsUiState.SettingsAction.TAFSEER -> openTafseerDialog()
            SettingsUiState.SettingsAction.COMPANION -> toggleCompanion()
            SettingsUiState.SettingsAction.WIDGET -> {
                widgetPinProvider.requestPinWidget { title, message ->
                    sendEffect(
                        SettingsEffect.ShowToast(
                            ToastDetails(
                                title = DesignUiText.DynamicString(title),
                                message = DesignUiText.DynamicString(message),
                                icon = Res.drawable.ic_prayer_times_selected
                            )
                        )
                    )
                }
            }
            else -> {}
        }
    }

    private fun toggleCompanion() {
        viewModelScope.launch {
            companionRepository.updateCompanionEnabled(!screenState.value.isCompanionEnabled)
        }
    }

    private fun openMoazenDialog() {
        val state = screenState.value
        openDialog(
            type = SettingsUiState.SelectionDialogType.MOAZEN,
            title = UiText.StringRes(Res.string.choose_moazen),
            description = UiText.StringRes(Res.string.moazen_description),
            options = SettingsUiState.Moazen.entries.map { moazen ->
                SelectionItem(
                    text = getMoazenName(moazen)
                )
            },
            selectedIndex = SettingsUiState.Moazen.entries.indexOf(state.selectedMoazen)
        )
    }

    private fun showSupportBottomSheet() {
        val products = billingService.products.value
        val options = supportProductIds.map { id ->
            val donationProduct = products.find { it.id == id }
            val price = donationProduct?.price ?: "N/A"
            SelectionItem(
                description = price,
                text = UiText.StringRes(getSupportName(id))
            )
        }
        openDialog(
            type = SettingsUiState.SelectionDialogType.SUPPORT,
            title = UiText.StringRes(Res.string.support_developer),
            description = UiText.StringRes(Res.string.support_description),
            options = options,
            selectedIndex = -1
        )
    }

    private fun getSupportName(id: String): StringResource {
        return when (id) {
            SUPPORT_1 -> Res.string.support_1
            SUPPORT_5 -> Res.string.support_2
            SUPPORT_10 -> Res.string.support_3
            SUPPORT_25 -> Res.string.support_4
            SUPPORT_100 -> Res.string.support_5
            else -> Res.string.support_developer
        }
    }

    override fun onLocationClick() {
        sendEffect(SettingsEffect.NavigateToLocation)
    }

    override fun onDialogConfirm(index: Int) {
        val dialog = screenState.value.dialog ?: return
        dialogHandlers[dialog.type]?.invoke(index)
        updateState { it.copy(dialog = null) }
        rebuildSections()
    }

    override fun onDialogDismiss() {
        stopPreview()
        val dialog = screenState.value.dialog
        if (dialog?.type == SettingsUiState.SelectionDialogType.SUPPORT) {
            viewModelScope.launch {
                settingsRepository.onDonationPromptAction(
                    DonationPromptAction.DISMISSED,
                    kotlin.time.Clock.System.now().toEpochMilliseconds()
                )
            }
        }
        updateState { it.copy(dialog = null) }
    }
}
