package dev.sayed.mehrabalmomen.domain.exceptions

open class AppException(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause){
    class EmptyDataException :
        AppException("No data available")

    class UnknownException(
        cause: Throwable
    ) : AppException(cause.message, cause)
}
