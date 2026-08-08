package dev.sayed.mehrabalmomen.presentation.utils

import android.content.Context
import android.os.Build
import android.os.PowerManager
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.domain.exceptions.AppException
import dev.sayed.mehrabalmomen.domain.exceptions.NetworkException

fun isIgnoringBatteryOptimizations(context: Context): Boolean {
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        powerManager.isIgnoringBatteryOptimizations(context.packageName)
    } else {
        true
    }
}


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