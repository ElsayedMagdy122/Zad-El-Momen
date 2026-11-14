package dev.sayed.mehrabalmomen.data.util.helpers

import dev.sayed.mehrabalmomen.domain.exceptions.AppException
import dev.sayed.mehrabalmomen.domain.exceptions.NetworkException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException

suspend fun Throwable.toAppException(): AppException {

    if (this is CancellationException || this.cause is CancellationException) throw this

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
