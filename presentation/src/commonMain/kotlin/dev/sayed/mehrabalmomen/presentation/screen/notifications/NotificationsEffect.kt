package dev.sayed.mehrabalmomen.presentation.screen.notifications

sealed interface NotificationsEffect {
    data object NavigateBack : NotificationsEffect
    data class NavigateToReciter(val reciterId: Int) : NotificationsEffect
    data object NavigateToRadio : NotificationsEffect
    data class NavigateToUrl(val url: String) : NotificationsEffect
}
