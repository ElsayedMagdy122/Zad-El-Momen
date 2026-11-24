package dev.sayed.mehrabalmomen.presentation.base

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable

/**
 * A sealed class to represent text that can be displayed in the UI.
 * It can be a hardcoded string or a localized string resource.
 * This is essential for CMP where resource IDs (Int) are platform-specific.
 */
sealed class UiText {
    data class DynamicString(val value: String) : UiText()
    class StringResource(@StringRes val resId: Int, vararg val args: Any) : UiText()

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> localizedString(resId, *args)
        }
    }

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args)
        }
    }
}
