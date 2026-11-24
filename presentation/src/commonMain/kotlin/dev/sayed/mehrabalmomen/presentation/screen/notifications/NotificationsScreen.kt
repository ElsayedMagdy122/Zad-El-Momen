package dev.sayed.mehrabalmomen.presentation.screen.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.component.Chip
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.domain.model.notification.AppNotification
import dev.sayed.mehrabalmomen.presentation.components.PrimaryDialog
import dev.sayed.mehrabalmomen.presentation.screen.notifications.components.NotificationItem
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.ic_notifications
import zadelmomen.presentation.generated.resources.ic_trash
import zadelmomen.presentation.generated.resources.no_data_yet

@Composable
fun NotificationsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToReciter: (Int) -> Unit,
    onNavigateToRadio: () -> Unit,
    viewModel: NotificationsViewModel = koinInject()
) {
    val state by viewModel.screenState.collectAsState()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                NotificationsEffect.NavigateBack -> onNavigateBack()
                is NotificationsEffect.NavigateToReciter -> onNavigateToReciter(effect.reciterId)
                NotificationsEffect.NavigateToRadio -> onNavigateToRadio()
                is NotificationsEffect.NavigateToUrl -> {
                    try {
                        uriHandler.openUri(effect.url)
                    } catch (_: Throwable) {}
                }
            }
        }
    }

    NotificationsContent(
        state = state,
        listener = viewModel
    )
}

@Composable
private fun NotificationsContent(
    state: NotificationsUiState,
    listener: NotificationsInteractionListener
) {
    var pendingDeleteNotification by remember { mutableStateOf<AppNotification?>(null) }
    var showClearAllDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 16.dp)
    ) {
        AppBar(
            title = "الإشعارات",
            onBackClick = listener::onBackClick
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Filter Chips Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Chip(
                text = "الكل (${state.notifications.size})",
                isSelected = state.filter == NotificationFilter.ALL,
                onClick = { listener.onFilterSelected(NotificationFilter.ALL) }
            )
            Chip(
                text = "غير مقروء (${state.unreadCount})",
                isSelected = state.filter == NotificationFilter.UNREAD,
                onClick = { listener.onFilterSelected(NotificationFilter.UNREAD) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Main List / Empty / Loading Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Theme.color.primary.primary)
                }
            } else if (state.filteredNotifications.isEmpty()) {
                EmptyNotificationsView()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = state.filteredNotifications,
                        key = { notification -> notification.id }
                    ) { notification ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.EndToStart) {
                                    pendingDeleteNotification = notification
                                    false
                                } else false
                            }
                        )

                        SwipeToDismissBox(
                            modifier = Modifier.animateItem(),
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            enableDismissFromEndToStart = true,
                            backgroundContent = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFFEE4E2))
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_trash),
                                        contentDescription = null,
                                        tint = Theme.color.semantic.error,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        ) {
                            NotificationItem(
                                notification = notification,
                                onClick = { listener.onNotificationClick(notification) }
                            )
                        }
                    }
                }
            }
        }

        // Bottom Action Buttons (Stacked Vertically)
        if (state.notifications.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (state.unreadCount > 0) {
                    PrimaryButton(
                        text = "تحديد الكل كمقروء",
                        onClick = listener::onMarkAllAsReadClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                PrimaryButton(
                    text = "مسح الكل",
                    onClick = { showClearAllDialog = true },
                    enabledContainerColor = Theme.color.surfaces.surfaceHigh,
                    enabledTextColor = Theme.color.semantic.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // Single item delete dialog
    pendingDeleteNotification?.let { notificationToDel ->
        NotificationRemoveDialog(
            onDismiss = { pendingDeleteNotification = null },
            onConfirm = {
                listener.onDeleteNotificationClick(notificationToDel.id)
                pendingDeleteNotification = null
            }
        )
    }

    // Clear all dialog
    if (showClearAllDialog) {
        ClearAllDialog(
            onDismiss = { showClearAllDialog = false },
            onConfirm = {
                listener.onClearAllClick()
                showClearAllDialog = false
            }
        )
    }
}

@Composable
fun NotificationRemoveDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    PrimaryDialog(onDismiss = onDismiss) {
        Column(
            modifier = modifier
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "حذف الإشعار",
                style = Theme.textStyle.title.medium,
                color = Theme.color.primary.shadePrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "هل أنت متأكد من رغبتك في حذف هذا الإشعار؟",
                style = Theme.textStyle.body.small,
                color = Theme.color.secondary.shadeSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "إلغاء",
                        style = Theme.textStyle.label.medium,
                        color = Theme.color.secondary.shadeSecondary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(onClick = onConfirm) {
                    Text(
                        text = "حذف",
                        style = Theme.textStyle.label.medium,
                        color = Theme.color.semantic.error
                    )
                }
            }
        }
    }
}

@Composable
fun ClearAllDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    PrimaryDialog(onDismiss = onDismiss) {
        Column(
            modifier = modifier
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "مسح جميع الإشعارات",
                style = Theme.textStyle.title.medium,
                color = Theme.color.primary.shadePrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "هل أنت متأكد من رغبتك في حذف جميع الإشعارات؟ لا يمكن التراجع عن هذا الإجراء.",
                style = Theme.textStyle.body.small,
                color = Theme.color.secondary.shadeSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "إلغاء",
                        style = Theme.textStyle.label.medium,
                        color = Theme.color.secondary.shadeSecondary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(onClick = onConfirm) {
                    Text(
                        text = "مسح الكل",
                        style = Theme.textStyle.label.medium,
                        color = Theme.color.semantic.error
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyNotificationsView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(Res.drawable.no_data_yet),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(96.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "لا توجد إشعارات حالياً",
                style = Theme.textStyle.title.small,
                color = Theme.color.primary.shadePrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "ستصلك الإشعارات الجديدة فور إضافتها من الباك إند",
                style = Theme.textStyle.body.small,
                color = Theme.color.secondary.shadeSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
