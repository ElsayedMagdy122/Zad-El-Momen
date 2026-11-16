package dev.sayed.mehrabalmomen.data.util

import dev.sayed.mehrabalmomen.domain.utils.Logger

class KmpLogger : Logger {
    override fun d(tag: String, message: String) {
        println("[$tag] DEBUG: $message")
    }

    override fun i(tag: String, message: String) {
        println("[$tag] INFO: $message")
    }

    override fun w(tag: String, message: String) {
        println("[$tag] WARN: $message")
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        println("[$tag] ERROR: $message")
        throwable?.printStackTrace()
    }
}
