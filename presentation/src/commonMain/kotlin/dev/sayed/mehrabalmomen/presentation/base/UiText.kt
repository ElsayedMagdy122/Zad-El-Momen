package dev.sayed.mehrabalmomen.presentation.base

import androidx.compose.runtime.Composable

sealed class UiText {
    data class DynamicString(val value: String) : UiText()

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
        }
    }
}
