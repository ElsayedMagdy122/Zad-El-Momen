package dev.sayed.mehrabalmomen.data.util.helpers

import dev.sayed.mehrabalmomen.domain.exceptions.AppException
import dev.sayed.mehrabalmomen.domain.exceptions.NetworkException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.io.IOException
import kotlin.coroutines.cancellation.CancellationException

suspend fun Throwable.toAppException(): AppException {

    if (this is CancellationException || this.cause is CancellationException) throw this

    if (this is AppException) return this

    val msg = message?.lowercase().orEmpty()
    if (msg.contains("unable to resolve host") || msg.contains("no address associated") || msg.contains("network is unreachable")) {
        return NetworkException.NoInternetException()
    }

    return when (this) {

        is SocketTimeoutException ->
            NetworkException.TimeoutException()

        is IOException ->
            NetworkException.ServerUnreachableException()

        is ClientRequestException ->
            NetworkException.HttpException(
                code = response.status.value,
                body = response.bodyAsText()
            )

        is ServerResponseException ->
            NetworkException.HttpException(
                code = response.status.value,
                body = response.bodyAsText()
            )

        else ->
            AppException.UnknownException(this)
    }
}
