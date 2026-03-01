package dev.sayed.mehrabalmomen.data.util.helpers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

suspend fun <T> safeCall(block: suspend () -> T): T {
    return try {
        block()
    } catch (e: Throwable) {
        throw e.toAppException()
    }
}

fun <T> Flow<T>.safeFlow(): Flow<T> =
    catch { e ->
        throw e.toAppException()
    }