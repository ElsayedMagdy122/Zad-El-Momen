package dev.sayed.mehrabalmomen.presentation.base

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

sealed interface UiText {
    data class DynamicString(val value: String) : UiText
    data class StringRes(
        val resource: StringResource,
        val args: List<Any> = emptyList()
    ) : UiText {
        constructor(resource: StringResource, vararg args: Any) : this(resource, args.toList())
    }

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringRes -> stringResource(resource, *args.toTypedArray())
        }
    }
}
