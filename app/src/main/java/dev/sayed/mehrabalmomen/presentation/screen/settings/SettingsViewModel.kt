package dev.sayed.mehrabalmomen.presentation.screen.settings

import SettingsUiState
import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.BuildConfig
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.data.util.BillingManager
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.prayer.Madhab
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.domain.usecase.PrayerSchedulingUseCase
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val prayerSchedulingUseCase: PrayerSchedulingUseCase,
    private val billingManager: BillingManager
) : BaseViewModel<SettingsUiState, SettingsEffect>(SettingsUiState()),
    SettingsInteractionListener {

    private val supportProductIds = listOf(
        BuildConfig.SUPPORT_1,
        BuildConfig.SUPPORT_5,
        BuildConfig.SUPPORT_10,
        BuildConfig.SUPPORT_25,
        BuildConfig.SUPPORT_100
    )
    private val moazenResMap = mapOf(
        SettingsUiState.Moazen.AZAN_ABED_ALBASET to R.raw.azan_abed_albaset,
        SettingsUiState.Moazen.AZAN_MAKKAH to R.raw.azan_makkah,
        SettingsUiState.Moazen.AZAN_MANSOOR_AL_ZAHRANI to R.raw.azan_mansoor_al_zahrani,
        SettingsUiState.Moazen.AZAN_MISHARY_ALAFASI to R.raw.azan_mishary_alafasi,
        SettingsUiState.Moazen.AZAN_MOHAMMED_ALMENSHWY to R.raw.azan_mohammed_almenshawy,
        SettingsUiState.Moazen.AZAN_NASSER_ALQATAMI to R.raw.azan_nasser_alqatami,
        SettingsUiState.Moazen.AZAN_SUHAIB_KHATBA to R.raw.azan_suhaib_khatba
    )

    init {
        observeSettings()
        observeBillingData()
        observeQuranFont()
        observeTafseer()
        observeSelectedMoazen()
        billingManager.queryProducts(supportProductIds)
    }

    private var previewPlayer: MediaPlayer? = null


    fun playPreview(index: Int, context: Context) {
        stopPreview()

        val moazen = SettingsUiState.Moazen.values().getOrNull(index)
        if (moazen == null) {
            return
        }

        val resId = moazenResMap[moazen]
        if (resId == null) {
            return
        }

        try {
            previewPlayer = MediaPlayer.create(context.applicationContext, resId)
            previewPlayer?.setOnCompletionListener { stopPreview() }
            previewPlayer?.start()
        } catch (e: Exception) {
        }
    }

    fun stopPreview() {
        try {
            previewPlayer?.stop()
            previewPlayer?.release()
        } catch (_: Exception) {
        }
        previewPlayer = null
    }


    private fun observeBillingData() {
        viewModelScope.launch {
            billingManager.productDetails.collect { products ->
                val available = products.isNotEmpty()
                updateState {
                    it.copy(isSupportAvailable = available)
                }
                rebuildSections()
            }
        }
        viewModelScope.launch {
            billingManager.purchaseSuccess.collect {
                sendEffect(
                    SettingsEffect.ShowToast(
                        ToastDetails(
                            title = R.string.success,
                            message = R.string.thank_you_for_support,
                            icon = R.drawable.ic_check_circle
                        )
                    )
                )
            }
        }
    }

    fun launchDonationFlow(activity: Activity, productId: String) {
        billingManager.launchBillingFlow(activity, productId)
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

    private fun rebuildSections() {
        val state = screenState.value
        val supportItems = mutableListOf(
            SettingsUiState.SettingsItemUiState(
                icon = R.drawable.ic_bug,
                title = R.string.help_feedback,
                action = SettingsUiState.SettingsAction.HELP_FEEDBACK
            ),
            SettingsUiState.SettingsItemUiState(
                icon = R.drawable.ic_star_rate,
                title = R.string.rate_app,
                action = SettingsUiState.SettingsAction.RATE_APP
            )
        )
        if (state.isSupportAvailable) {
            supportItems.add(
                SettingsUiState.SettingsItemUiState(
                    icon = R.drawable.ic_buy_coffee,
                    title = R.string.support_developer,
                    action = SettingsUiState.SettingsAction.ABOUT
                )
            )
        }
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
                titleRes = R.string.prayer,
                items = listOf(
                    SettingsUiState.SettingsItemUiState(
                        icon = R.drawable.ic_calculation_method,
                        title = R.string.calculation_method,
                        description = state.selectedCalculationMethod.value,
                        action = SettingsUiState.SettingsAction.CALCULATION_METHOD
                    ),
                    SettingsUiState.SettingsItemUiState(
                        icon = R.drawable.ic_mosque_02,
                        title = R.string.madhab,
                        description = state.selectedMadhab.value,
                        action = SettingsUiState.SettingsAction.MADHAB
                    ),
                    SettingsUiState.SettingsItemUiState(
                        icon = R.drawable.ic_moazen,
                        title = R.string.moazen,
                        description = getMoazenName(state.selectedMoazen),
                        action = SettingsUiState.SettingsAction.MOAZEN
                    )
                )
            ),
            SettingsUiState.SettingsSectionUiState(
                titleRes = R.string.quran,
                items = listOf(
                    SettingsUiState.SettingsItemUiState(
                        icon = R.drawable.ic_text_font,
                        title = R.string.text_font,
                        description = state.selectedFontSize.value,
                        action = SettingsUiState.SettingsAction.TEXT_FONT
                    ),
                    SettingsUiState.SettingsItemUiState(
                        icon = R.drawable.ic_tafseer,
                        title = R.string.al_tafseer,
                        description = getTafseerName(state.selectedTafseer),
                        action = SettingsUiState.SettingsAction.TAFSEER
                    )
                )

            ),
            SettingsUiState.SettingsSectionUiState(
                titleRes = R.string.support,
                items = supportItems
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

    private fun getTafseerName(type: SettingsUiState.TafseerType): Int {
        return when (type) {
            SettingsUiState.TafseerType.MOKHTASAR -> R.string.tafseer_mokhtasar
            SettingsUiState.TafseerType.MOYASSAR -> R.string.tafseer_moyasser
        }
    }

    private fun openTafseerDialog() {
        val state = screenState.value

        openDialog(
            type = SettingsUiState.SelectionDialogType.TAFSEER,
            titleRes = R.string.choose_tafseer,
            descriptionRes = R.string.tafseer_description,
            options = SettingsUiState.TafseerType.entries.map {
                SelectionItem(it.value)
            },
            selectedIndex = SettingsUiState.TafseerType.entries.indexOf(state.selectedTafseer)
        )
    }

    private fun openFontSizeDialog() {
        val state = screenState.value

        openDialog(
            type = SettingsUiState.SelectionDialogType.FONT_SIZE,
            titleRes = R.string.choose_font_size,
            descriptionRes = R.string.font_size_description,
            options = SettingsUiState.QuranFontSize.entries.map {
                SelectionItem(it.value)
            },
            selectedIndex = SettingsUiState.QuranFontSize.entries.indexOf(state.selectedFontSize)
        )
    }

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
        },
        SettingsUiState.SelectionDialogType.SUPPORT to { index ->
            val productId = when (index) {
                0 -> BuildConfig.SUPPORT_1
                1 -> BuildConfig.SUPPORT_5
                2 -> BuildConfig.SUPPORT_10
                3 -> BuildConfig.SUPPORT_25
                4 -> BuildConfig.SUPPORT_100
                else -> ""
            }
            sendEffect(SettingsEffect.LaunchDonation(productId))
        },
        SettingsUiState.SelectionDialogType.FONT_SIZE to { index ->
            val selected = SettingsUiState.QuranFontSize.entries[index]
            viewModelScope.launch(Dispatchers.IO) {
                settingsRepository.saveQuranFontSize(selected.sizeSp)
                updateState { it.copy(selectedFontSize = selected) }
                rebuildSections()
            }
        },
        SettingsUiState.SelectionDialogType.MOAZEN to { index ->
            val selected = SettingsUiState.Moazen.values()[index]
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

            SettingsUiState.SettingsAction.MOAZEN -> openMoazenDialog()
            SettingsUiState.SettingsAction.TAFSEER -> openTafseerDialog()
            else -> {}
        }
    }

    private fun openMoazenDialog() {
        val state = screenState.value
        openDialog(
            type = SettingsUiState.SelectionDialogType.MOAZEN,
            titleRes = R.string.choose_moazen,
            descriptionRes = R.string.moazen_description,
            options = SettingsUiState.Moazen.entries.map { moazen ->
                SelectionItem(
                    text = getMoazenName(moazen),
                    resId = moazenResMap[moazen]
                )
            },
            selectedIndex = SettingsUiState.Moazen.entries.indexOf(state.selectedMoazen)
        )
    }

    private fun showSupportBottomSheet() {
        val products = billingManager.productDetails.value
        val options = supportProductIds.map { id ->
            val details = products.find { it.productId == id }
            val price = details?.oneTimePurchaseOfferDetails?.formattedPrice ?: "N/A"
            SelectionItem(
                description = price,
                text = getSupportName(id)
            )
        }
        openDialog(
            type = SettingsUiState.SelectionDialogType.SUPPORT,
            titleRes = R.string.support_developer,
            descriptionRes = R.string.support_description,
            options = options,
            selectedIndex = -1
        )

    }

    private fun getSupportName(id: String): Int {
        return when (id) {
            BuildConfig.SUPPORT_1 -> R.string.support_1
            BuildConfig.SUPPORT_5 -> R.string.support_2
            BuildConfig.SUPPORT_10 -> R.string.support_3
            BuildConfig.SUPPORT_25 -> R.string.support_4
            BuildConfig.SUPPORT_100 -> R.string.support_5
            else -> 0
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
    override fun onHelpFeedbackClick() {
        sendEffect(SettingsEffect.NavigateToHelpFeedback)
    }

    override fun onRateAppClick() {
        sendEffect(SettingsEffect.NavigateToRateApp)
    }

    override fun onAboutClick() {
        showSupportBottomSheet()
    }
}