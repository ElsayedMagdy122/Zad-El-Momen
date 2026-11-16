package dev.sayed.mehrabalmomen.presentation.base

import androidx.annotation.DrawableRes

/**
 * A wrapper for icons to abstract away from raw Int resource IDs.
 */
data class UiIcon(
    @DrawableRes val resId: Int
)
