/*
 * The MIT License
 *
 * Copyright 2015 Mouaffak A. Sarhan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.msarhan.ummalqura.calendar

import java.util.*

/**
 * @author Mouaffak A. Sarhan.
 */
class UmmalquraDateFormatSymbols {

    var locale: Locale? = null
    private var months: Array<String>? = null
    private var shortMonths: Array<String>? = null

    constructor() {
        initializeData(Locale.getDefault(Locale.Category.FORMAT))
    }

    constructor(locale: Locale) {
        initializeData(locale)
    }

    fun getMonths(): Array<String> {
        return months!!.copyOf()
    }

    fun getShortMonths(): Array<String> {
        return shortMonths!!.copyOf()
    }

    private fun initializeData(desiredLocale: Locale) {
        locale = desiredLocale

        val resource = ResourceBundle.getBundle(
            "com.github.msarhan.ummalqura.calendar.text.UmmalquraFormatData",
            locale!!,
            object : ResourceBundle.Control() {
                override fun getFallbackLocale(baseName: String, locale: Locale): Locale {
                    return Locale.ENGLISH
                }
            }
        )

        if (resource is ListResourceBundle) {
            months = resource.getStringArray("MonthNames")
            shortMonths = resource.getStringArray("MonthAbbreviations")
        } else if (resource is PropertyResourceBundle) {
            months = resource.getString("MonthNames").split(",").toTypedArray()
            shortMonths = resource.getString("MonthAbbreviations").split(",").toTypedArray()
        }
    }
}
