package dev.sayed.mehrabalmomen.presentation.screen.prayers

import androidx.compose.runtime.Composable

@Composable
actual fun HandlePrayerTimesPlatformEffects(
    viewModel: PrayerTimesViewModel
) {
    // iOS doesn't have battery optimization whitelist or exact alarm permissions
}
