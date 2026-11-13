package dev.sayed.mehrabalmomen.presentation.base

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * A sealed class to represent text that can be displayed in the UI.
 * Compatible with Compose Multiplatform.
 */
sealed class UiText {
    data class DynamicString(val value: String) : UiText()
    class Resource(val res: StringResource, vararg val args: Any) : UiText()

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is Resource -> stringResource(res, *args)
        }
    }
}
