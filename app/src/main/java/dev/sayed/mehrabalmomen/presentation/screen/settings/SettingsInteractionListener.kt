package dev.sayed.mehrabalmomen.presentation.screen.settings

import SettingsUiState


interface SettingsInteractionListener {

    fun onItemClick(action: SettingsUiState.SettingsAction)

    fun onDialogConfirm(index: Int)

    fun onDialogDismiss()

    fun onLocationClick()

    fun onCalculationMethodClick()

    fun onHelpFeedbackClick()

    fun onRateAppClick()

    fun onAboutClick()
}