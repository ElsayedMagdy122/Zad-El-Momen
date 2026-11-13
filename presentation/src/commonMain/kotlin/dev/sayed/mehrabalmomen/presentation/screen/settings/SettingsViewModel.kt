package dev.sayed.mehrabalmomen.presentation.screen.settings

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.*
import dev.sayed.mehrabalmomen.domain.analytics.AnalyticsTracker
import dev.sayed.mehrabalmomen.domain.billing.BillingEvent
import dev.sayed.mehrabalmomen.domain.billing.BillingService
import dev.sayed.mehrabalmomen.domain.billing.SupportTiers
import dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.prayer.Madhab
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.domain.model.DonationPromptAction
import dev.sayed.mehrabalmomen.domain.model.audio.AudioSource
import dev.sayed.mehrabalmomen.domain.repository.audio.AudioPlayer
import dev.sayed.mehrabalmomen.domain.repository.companion.CompanionRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.domain.usecase.PrayerSchedulingUseCase
import dev.sayed.mehrabalmomen.domain.utils.getCurrentTimeMillis
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import dev.sayed.mehrabalmomen.presentation.base.UiText
import dev.sayed.mehrabalmomen.presentation.utils.NoOpWidgetHandler
import dev.sayed.mehrabalmomen.presentation.utils.WidgetHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val companionRepository: CompanionRepository,
    private val prayerSchedulingUseCase: PrayerSchedulingUseCase,
    private val billingService: BillingService,
    private val audioPlayer: dev.sayed.mehrabalmomen.domain.repository.audio.AudioPlayer,
    private val widgetHandler: WidgetHandler = NoOpWidgetHandler(),
    private val analyticsTracker: AnalyticsTracker
) : BaseViewModel<SettingsUiState, SettingsEffect>(SettingsUiState()),
    SettingsInteractionListener {

    private val supportProductIds = SupportTiers.ALL

    private val moazenResourceMap = mapOf(
        SettingsUiState.Moazen.AZAN_ABED_ALBASET to "azan_abed_albaset",
        SettingsUiState.Moazen.AZAN_MAKKAH to "azan_makkah",
        SettingsUiState.Moazen.AZAN_MANSOOR_AL_ZAHRANI to "azan_mansoor_al_zahrani",
        SettingsUiState.Moazen.AZAN_MISHARY_ALAFASI to "azan_mishary_alafasi",
        SettingsUiState.Moazen.AZAN_MOHAMMED_ALMENSHWY to "azan_mohammed_almenshawy",
        SettingsUiState.Moazen.AZAN_NASSER_ALQATAMI to "azan_nasser_alqatami",
        SettingsUiState.Moazen.AZAN_SUHAIB_KHATBA to "azan_suhaib_khatba"
    )

    init {
        observeSettings()
        observeBillingData()
        observeQuranFont()
        observeTafseer()
        observeSelectedMoazen()
        observeCompanionEnabled()
    }

    fun playPreview(index: Int) {
        stopPreview()

        val moazen = SettingsUiState.Moazen.entries.getOrNull(index) ?: return
        val resName = moazenResourceMap[moazen] ?: return

        try {
            audioPlayer.play(AudioSource.LocalResource(resName))
        } catch (_: Exception) {}
    }

    fun stopPreview() {
        try {
            audioPlayer.stop()
        } catch (_: Exception) {}
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
                            settingsRepository.onDonationPromptAction(
                                DonationPromptAction.DONATED,
                                getCurrentTimeMillis()
                            )
                        }
                        sendEffect(
                            SettingsEffect.ShowToast(
                                ToastDetails(
                                    title = Res.string.success,
                                    message = Res.string.thank_you_for_support,
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
                widgetHandler.updateWidgets()
                rebuildSections()
            }
        }
    }

    private fun observeQuranFont() {
        viewModelScope.launch {
            settingsRepository.observeQuranFontSize().collect { sizeSp ->
                updateState { state ->
                    state.copy(
                        selectedFontSize = SettingsUiState.QuranFontSize.entries.find { it.sizeSp == sizeSp }
                            ?: SettingsUiState.QuranFontSize.SMALL
                    )
                }
                rebuildSections()
            }
        }
    }

    private fun observeTafseer() {
        viewModelScope.launch {
            settingsRepository.observeTafseer().collect { fileName ->
                updateState { state ->
                    state.copy(
                        selectedTafseer = SettingsUiState.TafseerType.entries.find { it.fileName == fileName }
                            ?: SettingsUiState.TafseerType.MOKHTASAR
                    )
                }
                rebuildSections()
            }
        }
    }

    private fun observeSelectedMoazen() {
        viewModelScope.launch {
            settingsRepository.observeSelectedMoazen().collect { fileName ->
                val moazen = SettingsUiState.Moazen.entries.find { it.fileName == fileName }
                    ?: SettingsUiState.Moazen.AZAN_MAKKAH
                updateState { it.copy(selectedMoazen = moazen) }
                rebuildSections()
            }
        }
    }

    private fun observeCompanionEnabled() {
        viewModelScope.launch {
            companionRepository.observeCompanionEnabled().collect { isEnabled ->
                updateState { it.copy(isCompanionEnabled = isEnabled) }
                rebuildSections()
            }
        }
    }

    private fun rebuildSections() {
        val state = screenState.value

        val supportItems = listOf(
            SettingsUiState.SettingsItemUiState(
                icon = Res.drawable.ic_bug,
                title = UiText.Resource(Res.string.help_feedback),
                action = SettingsUiState.SettingsAction.HELP_FEEDBACK
            ),
            SettingsUiState.SettingsItemUiState(
                icon = Res.drawable.ic_contact_us,
                title = UiText.Resource(Res.string.contact_us),
                action = SettingsUiState.SettingsAction.CONTACT_US
            ),
            SettingsUiState.SettingsItemUiState(
                icon = Res.drawable.ic_help,
                title = UiText.Resource(Res.string.faq),
                action = SettingsUiState.SettingsAction.FAQ
            ),
            SettingsUiState.SettingsItemUiState(
                icon = Res.drawable.ic_buy_coffee,
                title = UiText.Resource(Res.string.support_developer),
                action = SettingsUiState.SettingsAction.ABOUT
            )
        )

        val sections = listOf(
            SettingsUiState.SettingsSectionUiState(
                title = UiText.Resource(Res.string.general),
                items = listOf(
                    SettingsUiState.SettingsItemUiState(
                        icon = Res.drawable.ic_language,
                        title = UiText.Resource(Res.string.language),
                        description = UiText.Resource(state.selectedLanguage.resId),
                        action = SettingsUiState.SettingsAction.LANGUAGE
                    ),
                    SettingsUiState.SettingsItemUiState(
                        icon = Res.drawable.ic_theme,
                        title = UiText.Resource(Res.string.theme),
                        description = UiText.Resource(state.selectedTheme.resId),
                        action = SettingsUiState.SettingsAction.THEME
                    ),
                    SettingsUiState.SettingsItemUiState(
                        icon = Res.drawable.ic_location,
                        title = UiText.Resource(Res.string.location),
                        action = SettingsUiState.SettingsAction.LOCATION,
                        description = UiText.DynamicString(state.location.country.plus(", ").plus(state.location.city))
                    ),
                    SettingsUiState.SettingsItemUiState(
                        icon = Res.drawable.ic_alarm_clock,
                        title = UiText.Resource(Res.string.notifications),
                        action = SettingsUiState.SettingsAction.NOTIFICATIONS,
                    ),
                    SettingsUiState.SettingsItemUiState(
                        icon = Res.drawable.ic_rafeek,
                        title = UiText.Resource(Res.string.companion_mode),
                        action = SettingsUiState.SettingsAction.COMPANION,
                        value = state.isCompanionEnabled
                    )
                )
            ),

            SettingsUiState.SettingsSectionUiState(
                title = UiText.Resource(Res.string.prayer),
                items = listOf(
                    SettingsUiState.SettingsItemUiState(
                        icon = Res.drawable.ic_calculation_method,
                        title = UiText.Resource(Res.string.calculation_method),
                        description = UiText.Resource(state.selectedCalculationMethod.resId),
                        action = SettingsUiState.SettingsAction.CALCULATION_METHOD
                    ),
                    SettingsUiState.SettingsItemUiState(
                        icon = Res.drawable.ic_mosque_02,
                        title = UiText.Resource(Res.string.madhab),
                        description = UiText.Resource(state.selectedMadhab.resId),
                        action = SettingsUiState.SettingsAction.MADHAB
                    ),
                    SettingsUiState.SettingsItemUiState(
                        icon = Res.drawable.ic_moazen,
                        title = UiText.Resource(Res.string.moazen),
                        description = getMoazenName(state.selectedMoazen),
                        action = SettingsUiState.SettingsAction.MOAZEN
                    ),
                    SettingsUiState.SettingsItemUiState(
                        icon = Res.drawable.ic_prayer_times_selected,
                        title = UiText.Resource(Res.string.prayer_times_widget),
                        description = UiText.Resource(Res.string.prayer_widget_desc),
                        action = SettingsUiState.SettingsAction.WIDGET
                    )
                )
            ),

            SettingsUiState.SettingsSectionUiState(
                title = UiText.Resource(Res.string.quran),
                items = listOf(
                    SettingsUiState.SettingsItemUiState(
                        icon = Res.drawable.ic_text_font,
                        title = UiText.Resource(Res.string.text_font),
                        description = UiText.Resource(state.selectedFontSize.resId),
                        action = SettingsUiState.SettingsAction.TEXT_FONT
                    ),
                    SettingsUiState.SettingsItemUiState(
                        icon = Res.drawable.ic_tafseer,
                        title = UiText.Resource(Res.string.al_tafseer),
                        description = getTafseerName(state.selectedTafseer),
                        action = SettingsUiState.SettingsAction.TAFSEER
                    )
                )
            ),

            SettingsUiState.SettingsSectionUiState(
                title = UiText.Resource(Res.string.support),
                items = supportItems
            ),

            SettingsUiState.SettingsSectionUiState(
                title = UiText.Resource(Res.string.legal),
                items = listOf(
                    SettingsUiState.SettingsItemUiState(
                        icon = Res.drawable.ic_privacy,
                        title = UiText.Resource(Res.string.privacy_and_policy),
                        action = SettingsUiState.SettingsAction.PRIVACY_POLICY
                    )
                )
            ),

            SettingsUiState.SettingsSectionUiState(
                title = UiText.Resource(Res.string.growth),
                items = listOf(
                    SettingsUiState.SettingsItemUiState(
                        icon = Res.drawable.ic_star_rate,
                        title = UiText.Resource(Res.string.rate_app),
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

    private fun getTafseerName(type: SettingsUiState.TafseerType): UiText {
        val resId = when (type) {
            SettingsUiState.TafseerType.MOKHTASAR -> Res.string.tafseer_mokhtasar
            SettingsUiState.TafseerType.MOYASSAR -> Res.string.tafseer_moyasser
        }
        return UiText.Resource(resId)
    }

    private fun openTafseerDialog() {
        val state = screenState.value

        openDialog(
            type = SettingsUiState.SelectionDialogType.TAFSEER,
            title = UiText.Resource(Res.string.choose_tafseer),
            description = UiText.Resource(Res.string.tafseer_description),
            options = SettingsUiState.TafseerType.entries.map {
                SelectionItem(UiText.Resource(it.resId))
            },
            selectedIndex = SettingsUiState.TafseerType.entries.indexOf(state.selectedTafseer)
        )
    }

    private fun openFontSizeDialog() {
        val state = screenState.value

        openDialog(
            type = SettingsUiState.SelectionDialogType.FONT_SIZE,
            title = UiText.Resource(Res.string.choose_font_size),
            description = UiText.Resource(Res.string.font_size_description),
            options = SettingsUiState.QuranFontSize.entries.map {
                SelectionItem(UiText.Resource(it.resId))
            },
            selectedIndex = SettingsUiState.QuranFontSize.entries.indexOf(state.selectedFontSize)
        )
    }

    private fun openLanguageDialog() {
        val state = screenState.value
        openDialog(
            type = SettingsUiState.SelectionDialogType.LANGUAGE,
            title = UiText.Resource(Res.string.choose_language),
            description = UiText.Resource(Res.string.language_description),
            options = SettingsUiState.Language.entries.map { SelectionItem(UiText.Resource(it.resId)) },
            selectedIndex = SettingsUiState.Language.entries.indexOf(state.selectedLanguage)
        )
    }

    private fun openThemeDialog() {
        val state = screenState.value
        openDialog(
            type = SettingsUiState.SelectionDialogType.THEME,
            title = UiText.Resource(Res.string.choose_theme),
            description = UiText.Resource(Res.string.theme_description),
            options = SettingsUiState.ThemeState.entries.map { SelectionItem(UiText.Resource(it.resId)) },
            selectedIndex = SettingsUiState.ThemeState.entries.indexOf(state.selectedTheme)
        )
    }

    private fun openMadhabDialog() {
        val state = screenState.value
        openDialog(
            type = SettingsUiState.SelectionDialogType.MADHAB,
            title = UiText.Resource(Res.string.choose_madhab),
            description = UiText.Resource(Res.string.madhab_description),
            options = SettingsUiState.MadhabState.entries.map { SelectionItem(UiText.Resource(it.resId)) },
            selectedIndex = SettingsUiState.MadhabState.entries.indexOf(state.selectedMadhab)
        )
    }

    private fun openCalculationMethodDialog() {
        val state = screenState.value
        openDialog(
            type = SettingsUiState.SelectionDialogType.CALCULATION_METHOD,
            title = UiText.Resource(Res.string.choose_calculation_method),
            description = UiText.Resource(Res.string.calculation_method_description),
            options = SettingsUiState.CalculationMethod.entries.map { SelectionItem(UiText.Resource(it.resId)) },
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
            val productId = supportProductIds.getOrNull(index) ?: ""
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
            SettingsUiState.SettingsAction.WIDGET -> sendEffect(SettingsEffect.RequestPinWidget)
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
            title = UiText.Resource(Res.string.choose_moazen),
            description = UiText.Resource(Res.string.moazen_description),
            options = SettingsUiState.Moazen.entries.map { moazen ->
                SelectionItem(
                    text = getMoazenName(moazen),
                    resId = moazenResourceMap[moazen]
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
                text = UiText.Resource(getSupportName(id))
            )
        }
        openDialog(
            type = SettingsUiState.SelectionDialogType.SUPPORT,
            title = UiText.Resource(Res.string.support_developer),
            description = UiText.Resource(Res.string.support_description),
            options = options,
            selectedIndex = -1
        )
    }

    fun requestPinWidget(onFallback: () -> Unit) {
        widgetHandler.requestPinWidget(onFallback)
    }

    private fun getSupportName(id: String): org.jetbrains.compose.resources.StringResource {
        return when (id) {
            SupportTiers.TIER_1 -> Res.string.support_1
            SupportTiers.TIER_5 -> Res.string.support_2
            SupportTiers.TIER_10 -> Res.string.support_3
            SupportTiers.TIER_25 -> Res.string.support_4
            SupportTiers.TIER_100 -> Res.string.support_5
            else -> Res.string.support_1
        }
    }

    override fun onDialogConfirm(index: Int) {
        val dialog = screenState.value.dialog ?: return
        dialogHandlers[dialog.type]?.invoke(index)
        updateState { it.copy(dialog = null) }
        rebuildSections()
    }

    override fun onDialogDismiss() {
        val dialog = screenState.value.dialog
        if (dialog?.type == SettingsUiState.SelectionDialogType.SUPPORT) {
            viewModelScope.launch {
                settingsRepository.onDonationPromptAction(
                    DonationPromptAction.DISMISSED,
                    getCurrentTimeMillis()
                )
            }
        }
        updateState { it.copy(dialog = null) }
    }

    override fun onLocationClick() {
        sendEffect(SettingsEffect.NavigateToLocation)
    }

    override fun onCalculationMethodClick() {}
    override fun onHelpFeedbackClick() {
        sendEffect(SettingsEffect.NavigateToHelpFeedback)
    }

    override fun onRateAppClick() {
        sendEffect(SettingsEffect.NavigateToRateApp)
    }

    override fun onAboutClick() {
        showSupportBottomSheet()
    }

    override fun onPrivacyAndPolicy() {
        sendEffect(SettingsEffect.NavigateToPrivacy)
    }

    override fun onContactUs() {
        sendEffect(SettingsEffect.NavigateToContactUs)
    }

    override fun onNotifications() {
        sendEffect(SettingsEffect.NavigateToNotifications)
    }
}
