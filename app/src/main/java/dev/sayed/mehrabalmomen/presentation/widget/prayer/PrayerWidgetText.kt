package dev.sayed.mehrabalmomen.presentation.widget.prayer

import android.content.Context

fun String.localizedDigits(context: Context): String {
    val locale = context.resources.configuration.locales[0]
    if (locale.language != "ar") return this

    return map { char ->
        when (char) {
            in '0'..'9' -> '٠' + (char - '0')
            else -> char
        }
    }.joinToString(separator = "")
}
