package dev.sayed.mehrabalmomen.data.util

import android.util.Log
import dev.sayed.mehrabalmomen.domain.utils.Logger

class AndroidLogger : Logger {

    override fun d(
        tag: String,
        message: String
    ) {
        Log.d(tag, message)
    }

    override fun i(
        tag: String,
        message: String
    ) {
        Log.i(tag, message)
    }

    override fun w(
        tag: String,
        message: String
    ) {
        Log.w(tag, message)
    }

    override fun e(
        tag: String,
        message: String,
        throwable: Throwable?
    ) {
        Log.e(tag, message, throwable)
    }
}