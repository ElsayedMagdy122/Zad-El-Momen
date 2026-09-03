package dev.sayed.mehrabalmomen.presentation.utils

import androidx.compose.runtime.Composable

@Composable
expect fun CompassSensorHandler(onDirectionChanged: (Float) -> Unit)
