package dev.sayed.mehrabalmomen.design_system.base

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

sealed class UiText {
    data class DynamicString(val value: String) : UiText()
    class ResString(val res: StringResource, vararg val args: Any) : UiText()

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is ResString -> stringResource(res, *args)
        }
    }
}

