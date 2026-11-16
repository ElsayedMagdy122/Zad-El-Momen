package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions

import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel

class PermissionsViewModel(
    private val settingsRepository: SettingsRepository
) : BaseViewModel<PermissionsUiState, PermissionsEffect>(PermissionsUiState()),
    PermissionsInteractionListener {

    override fun onClickAllowLocationAccess() {}
    override fun onClickAllowNotificationAccess() {}
    override fun onClickAllowAlarmAccess() {}
    override fun onClickAllowBackgroundAccess() {}

    override fun onClickNext() {
        tryToCall(
            block = {
                settingsRepository.setOnboardingComplete()
            },
            onSuccess = {
                sendEffect(PermissionsEffect.NavigateToHome)
            },
            onError = {}
        )
    }
}
