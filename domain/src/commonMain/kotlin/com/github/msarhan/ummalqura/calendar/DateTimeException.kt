package com.github.msarhan.ummalqura.calendar

/**
 * @author Mouaffak A. Sarhan
 */
class DateTimeException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, ex: Throwable?) : super(message, ex)
}
