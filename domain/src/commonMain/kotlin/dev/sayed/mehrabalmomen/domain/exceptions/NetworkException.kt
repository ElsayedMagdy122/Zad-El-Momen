package dev.sayed.mehrabalmomen.domain.exceptions

open class NetworkException(
    message: String? = null,
    cause: Throwable? = null
) : AppException(message, cause){
    class NoInternetException :
        NetworkException("No internet connection")

    class TimeoutException :
        NetworkException("Connection timeout")

    class SlowConnectionException :
        NetworkException("Slow internet connection")

    class ServerUnreachableException :
        NetworkException("Cannot reach server")

    class HttpException(
        val code: Int,
        val body: String?
    ) : NetworkException("Http error $code")

    class UnknownNetworkException(
        cause: Throwable
    ) : NetworkException(cause.message, cause)
}

