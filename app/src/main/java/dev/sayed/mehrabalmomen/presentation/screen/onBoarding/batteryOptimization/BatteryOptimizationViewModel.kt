package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.domain.repository.settings.BatteryOptimizationRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.launch

class BatteryOptimizationViewModel(
    private val batteryOptimizationRepository: BatteryOptimizationRepository,
    private val settingsRepository: SettingsRepository
) :
    BaseViewModel<BatteryOptimizationUiState, BatteryOptimizationEffect>(
        BatteryOptimizationUiState()
    ), BatteryOptimizationInteractionListener {

    fun loadInstructions(manufacturer: String, isArabic: Boolean) {
        tryToCall(
            block = { batteryOptimizationRepository.getBrandInstructions(manufacturer, isArabic) },
            onSuccess = { instructions ->
                updateState { it.copy(instructions = instructions) }
            },
            onError = { e -> }
        )
    }

    override fun onOpenSettingsClicked() {
        sendEffect(BatteryOptimizationEffect.OpenSettings)
    }

    override fun onSkipForNowClicked() {
        viewModelScope.launch {
            settingsRepository.setOnboardingComplete()
            sendEffect(BatteryOptimizationEffect.NavigateToHome)
        }
    }

    override fun onBackClicked() {
        sendEffect(BatteryOptimizationEffect.NavigateBack)
    }

    override fun onLearnMoreClick() {
        sendEffect(BatteryOptimizationEffect.NavigateToLearnMore)
    }
}