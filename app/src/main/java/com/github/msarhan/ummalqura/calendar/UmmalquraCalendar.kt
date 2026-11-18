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
class UmmalquraCalendar : GregorianCalendar {

    companion object {
        const val MUHARRAM = 0
        const val SAFAR = 1
        const val RABI_AWWAL = 2
        const val RABI_THANI = 3
        const val JUMADA_AWWAL = 4
        const val JUMADA_THANI = 5
        const val RAJAB = 6
        const val SHAABAN = 7
        const val RAMADHAN = 8
        const val SHAWWAL = 9
        const val THUL_QIDAH = 10
        const val THUL_HIJJAH = 11

        fun lengthOfMonth(year: Int, month: Int): Int {
            return UmmalquraGregorianConverter.getDaysInMonth(year, month)
        }

        fun lengthOfYear(year: Int): Int {
            var total = 0
            for (m in MUHARRAM..THUL_HIJJAH) {
                total += lengthOfMonth(year, m)
            }
            return total
        }
    }

    protected var hFields: IntArray? = null

    constructor() : super()
    constructor(aLocale: Locale) : super(aLocale)
    constructor(zone: TimeZone, aLocale: Locale) : super(zone, aLocale)
    constructor(year: Int, month: Int, dayOfMonth: Int) : this(year, month, dayOfMonth, 0, 0, 0)
    constructor(year: Int, month: Int, dayOfMonth: Int, hourOfDay: Int, minute: Int) : this(year, month, dayOfMonth, hourOfDay, minute, 0)
    constructor(year: Int, month: Int, dayOfMonth: Int, hourOfDay: Int, minute: Int, second: Int) : super() {
        set(YEAR, year)
        set(MONTH, month)
        set(DAY_OF_MONTH, dayOfMonth)
        set(HOUR_OF_DAY, hourOfDay)
        set(MINUTE, minute)
        set(SECOND, second)
    }

    override fun get(field: Int): Int {
        if (field == YEAR || field == MONTH || field == DAY_OF_MONTH) {
            return hFields!![field]
        }
        return super.get(field)
    }

    private var isSetYear = false
    private var isSetMonth = false
    private var isSetDayOfMonth = false

    override fun set(field: Int, value: Int) {
        if (field == YEAR || field == MONTH || field == DAY_OF_MONTH) {
            val cal = Calendar.getInstance().apply { timeInMillis = this@UmmalquraCalendar.timeInMillis }
            val hDateInfo = UmmalquraGregorianConverter.toHijri(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH)
            )
            if (field == YEAR) {
                hDateInfo[0] = value
                isSetYear = true
            } else if (field == MONTH) {
                hDateInfo[1] = value
                isSetMonth = true
            } else {
                hDateInfo[2] = value
                isSetDayOfMonth = true
            }

            adjustFields(hDateInfo)

            val gDateInfo = UmmalquraGregorianConverter.toGregorian(hDateInfo[0], hDateInfo[1], hDateInfo[2])

            super.set(YEAR, gDateInfo[0])
            super.set(MONTH, gDateInfo[1])
            super.set(DAY_OF_MONTH, gDateInfo[2])

            complete()
        } else {
            super.set(field, value)
        }
    }

    private fun adjustFields(hDateInfo: IntArray) {
        val maxDayInMonth = lengthOfMonth(hDateInfo[0], hDateInfo[1])
        if (hDateInfo[2] > maxDayInMonth) {
            if (!isSetMonth && !isSetYear) {
                hDateInfo[1] = 0
                hDateInfo[0] = 1300
            } else {
                hDateInfo[2] = maxDayInMonth
            }
        }
    }

    fun lengthOfMonth(): Int {
        return lengthOfMonth(get(YEAR), get(MONTH))
    }

    fun lengthOfYear(): Int {
        return lengthOfYear(get(YEAR))
    }

    override fun getDisplayName(field: Int, style: Int, locale: Locale): String? {
        if (field == MONTH) {
            val symbols = UmmalquraDateFormatSymbols(locale)
            val strings = getFieldStrings(field, style, symbols)
            if (strings != null) {
                val fieldValue = get(field)
                if (fieldValue < strings.size) {
                    return strings[fieldValue]
                }
            }
            return null
        }
        return super.getDisplayName(field, style, locale)
    }

    override fun getDisplayNames(field: Int, style: Int, locale: Locale): Map<String, Int>? {
        if (field == MONTH) {
            if (style == ALL_STYLES) {
                val shortNames = getDisplayNamesImpl(field, SHORT, locale)
                val longNames = getDisplayNamesImpl(field, LONG, locale)
                if (shortNames == null) {
                    return longNames
                }
                if (longNames != null) {
                    shortNames.putAll(longNames)
                }
                return shortNames
            }
            return getDisplayNamesImpl(field, style, locale)
        }
        return super.getDisplayNames(field, style, locale)
    }

    private fun getDisplayNamesImpl(field: Int, style: Int, locale: Locale): MutableMap<String, Int>? {
        val symbols = UmmalquraDateFormatSymbols(locale)
        val strings = getFieldStrings(field, style, symbols)
        if (strings != null) {
            val names = HashMap<String, Int>()
            for (i in strings.indices) {
                if (strings[i].isEmpty()) {
                    continue
                }
                names[strings[i]] = i
            }
            return names
        }
        return null
    }

    private fun getFieldStrings(field: Int, style: Int, symbols: UmmalquraDateFormatSymbols): Array<String>? {
        if (field == MONTH) {
            if (SHORT == style) {
                return symbols.getShortMonths()
            }
            if (LONG == style) {
                return symbols.getMonths()
            }
        }
        return null
    }

    override fun equals(obj: Any?): Boolean {
        return obj is UmmalquraCalendar && super.equals(obj)
    }

    override fun hashCode(): Int {
        return super.hashCode() xor 622
    }

    override fun computeFields() {
        super.computeFields()
        if (hFields == null) {
            hFields = IntArray(super.fields.size)
        }
        val cal = Calendar.getInstance().apply { timeInMillis = this@UmmalquraCalendar.timeInMillis }
        val hDateInfo = UmmalquraGregorianConverter.toHijri(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
        hFields!![YEAR] = hDateInfo[0]
        hFields!![MONTH] = hDateInfo[1]
        hFields!![DAY_OF_MONTH] = hDateInfo[2]
    }
}
