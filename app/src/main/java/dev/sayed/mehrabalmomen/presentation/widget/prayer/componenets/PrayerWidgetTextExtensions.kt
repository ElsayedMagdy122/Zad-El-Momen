package dev.sayed.mehrabalmomen.presentation.widget.prayer.componenets

internal fun String.asHoursAndMinutes(): String {
    val lastSeparator = lastIndexOf(':')
    return if (lastSeparator > 0) substring(0, lastSeparator) else this
}
