package dev.sayed.mehrabalmomen.presentation.utils

import androidx.compose.runtime.Composable

@Composable
expect fun HandlePlatformEffects(effect: Any?, onEffectHandled: () -> Unit)

expect fun openStoreReview()
