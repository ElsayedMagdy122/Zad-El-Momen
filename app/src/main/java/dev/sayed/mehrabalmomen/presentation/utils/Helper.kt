package dev.sayed.mehrabalmomen.presentation.utils

import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.domain.exceptions.AppException
import dev.sayed.mehrabalmomen.domain.exceptions.NetworkException
import dev.sayed.mehrabalmomen.presentation.base.UiText

/**
 * Maps common exceptions to localized UI text.
 */
fun Throwable.toUiText(): UiText {
    val resId = when (this) {
        is NetworkException.NoInternetException -> R.string.no_internet_connection
        is NetworkException.TimeoutException -> R.string.request_timeout
        is NetworkException.SlowConnectionException -> R.string.slow_internet_connection
        is NetworkException.ServerUnreachableException -> R.string.server_unreachable
        is NetworkException.HttpException -> R.string.server_error
        is AppException.EmptyDataException -> R.string.no_data_available
        else -> R.string.something_went_wrong
    }
    return UiText.StringResource(resId)
}

@Deprecated("Use toUiText() instead", ReplaceWith("toUiText()"))
fun Throwable.toUiErrorMessage(): Int {
    return when (this) {
        is NetworkException.NoInternetException -> R.string.no_internet_connection
        is NetworkException.TimeoutException -> R.string.request_timeout
        is NetworkException.SlowConnectionException -> R.string.slow_internet_connection
        is NetworkException.ServerUnreachableException -> R.string.server_unreachable
        is NetworkException.HttpException -> R.string.server_error
        is AppException.EmptyDataException -> R.string.no_data_available
        else -> R.string.something_went_wrong
    }
}
