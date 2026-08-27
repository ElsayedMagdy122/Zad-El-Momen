package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions

interface PermissionsInteractionListener {
    fun onClickAllowLocationAccess()
    fun onClickAllowNotificationAccess()
    fun onClickAllowAlarmAccess()
    fun onClickAllowBackgroundAccess()
    fun onClickNext()
}
