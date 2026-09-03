package dev.sayed.mehrabalmomen.presentation.utils

import dev.sayed.mehrabalmomen.domain.exceptions.AppException
import dev.sayed.mehrabalmomen.domain.exceptions.NetworkException
import dev.sayed.mehrabalmomen.design_system.utils.UiText
import zad_el_momen.presentation.generated.resources.Res
import zad_el_momen.presentation.generated.resources.*

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
    return UiText.StringResource(res)
}
