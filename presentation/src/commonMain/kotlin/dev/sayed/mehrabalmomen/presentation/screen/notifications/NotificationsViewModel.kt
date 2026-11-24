package dev.sayed.mehrabalmomen.presentation.screen.notifications

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.domain.model.notification.AppNotification
import dev.sayed.mehrabalmomen.domain.model.notification.NotificationType
import dev.sayed.mehrabalmomen.domain.repository.notification.AppNotificationRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val repository: AppNotificationRepository
) : BaseViewModel<NotificationsUiState, NotificationsEffect>(NotificationsUiState()),
    NotificationsInteractionListener {

    init {
        observeNotifications()
        observeUnreadCount()
    }

    private fun observeNotifications() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            repository.observeNotifications().collectLatest { notifications ->
                updateState { state ->
                    val filtered = applyFilter(notifications, state.filter)
                    state.copy(
                        notifications = notifications,
                        filteredNotifications = filtered,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun observeUnreadCount() {
        viewModelScope.launch {
            repository.getUnreadCount().collectLatest { unreadCount ->
                updateState { it.copy(unreadCount = unreadCount) }
            }
        }
    }

    private fun applyFilter(
        list: List<AppNotification>,
        filter: NotificationFilter
    ): List<AppNotification> {
        return when (filter) {
            NotificationFilter.ALL -> list
            NotificationFilter.UNREAD -> list.filter { !it.isRead }
        }
    }

    override fun onNotificationClick(notification: AppNotification) {
        viewModelScope.launch {
            if (!notification.isRead) {
                repository.markAsRead(notification.id)
            }
            when (notification.type) {
                NotificationType.NEW_RECITER -> {
                    val reciterId = notification.targetId?.toIntOrNull() ?: 1
                    sendEffect(NotificationsEffect.NavigateToReciter(reciterId))
                }
                NotificationType.NEW_RADIO -> {
                    sendEffect(NotificationsEffect.NavigateToRadio)
                }
                NotificationType.APP_UPDATE -> {
                    val url = notification.deepLink ?: "https://play.google.com/store/apps/details?id=dev.sayed.mehrabalmomen"
                    sendEffect(NotificationsEffect.NavigateToUrl(url))
                }
                NotificationType.GENERAL -> {
                    notification.deepLink?.let { url ->
                        sendEffect(NotificationsEffect.NavigateToUrl(url))
                    }
                }
            }
        }
    }

    override fun onMarkAllAsReadClick() {
        viewModelScope.launch {
            repository.markAllAsRead()
        }
    }

    override fun onDeleteNotificationClick(id: String) {
        viewModelScope.launch {
            repository.deleteNotification(id)
        }
    }

    override fun onClearAllClick() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    override fun onFilterSelected(filter: NotificationFilter) {
        updateState { state ->
            val filtered = applyFilter(state.notifications, filter)
            state.copy(filter = filter, filteredNotifications = filtered)
        }
    }

    override fun onBackClick() {
        sendEffect(NotificationsEffect.NavigateBack)
    }
}
