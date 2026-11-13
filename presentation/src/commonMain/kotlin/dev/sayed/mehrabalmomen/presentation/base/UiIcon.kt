package dev.sayed.mehrabalmomen.presentation.base

import org.jetbrains.compose.resources.DrawableResource

/**
 * A wrapper for icons to abstract away from raw Int resource IDs.
 * Uses Compose Multiplatform DrawableResource.
 */
data class UiIcon(
    val resource: DrawableResource
)
