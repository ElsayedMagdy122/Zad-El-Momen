package dev.sayed.mehrabalmomen.presentation.utils

import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.no_data_available
import dev.sayed.mehrabalmomen.designsystem.resources.no_internet_connection
import dev.sayed.mehrabalmomen.designsystem.resources.request_timeout
import dev.sayed.mehrabalmomen.designsystem.resources.server_error
import dev.sayed.mehrabalmomen.designsystem.resources.server_unreachable
import dev.sayed.mehrabalmomen.designsystem.resources.slow_internet_connection
import dev.sayed.mehrabalmomen.designsystem.resources.something_went_wrong
import dev.sayed.mehrabalmomen.domain.exceptions.AppException
import dev.sayed.mehrabalmomen.domain.exceptions.NetworkException
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.presentation.base.UiText

/**
 * Maps common exceptions to localized UI text.
 */
fun Throwable.toUiText(): UiText {
    val res = when (this) {
        is NetworkException.NoInternetException -> Res.string.no_internet_connection
        is NetworkException.TimeoutException -> Res.string.request_timeout
        is NetworkException.SlowConnectionException -> Res.string.slow_internet_connection
        is NetworkException.ServerUnreachableException -> Res.string.server_unreachable
        is NetworkException.HttpException -> Res.string.server_error
        is AppException.EmptyDataException -> Res.string.no_data_available
        else -> Res.string.something_went_wrong
    }
    return UiText.Resource(res)
}

fun String.toLocalizedDigits(language: AppSettings.Language): String {
    return if (language == AppSettings.Language.ARABIC) {
        this.map {
            when (it) {
                in '0'..'9' -> '٠' + (it - '0')
                else -> it
            }
        }.joinToString("")
    } else {
        this
    }
}

fun Int.toLocalizedDigits(language: AppSettings.Language): String {
    return this.toString().toLocalizedDigits(language)
}

fun String.localizeAmPm(language: AppSettings.Language): String {
    return if (language == AppSettings.Language.ARABIC) {
        this.replace("AM", "ص").replace("PM", "م")
    } else {
        this
    }
}
