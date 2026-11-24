package dev.sayed.mehrabalmomen.presentation.screen.quran.tasmee3

import kotlin.text.iterator

/**
 * Converts Western digits (1, 2, 3...) to authentic Arabic numerals (١، ٢، ٣...).
 */
fun toArabicNumerals(number: Int): String {
    val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    val str = number.toString()
    val builder = StringBuilder()
    for (char in str) {
        if (char in '0'..'9') {
            builder.append(arabicDigits[char - '0'])
        } else {
            builder.append(char)
        }
    }
    return builder.toString()
}

/**
 * Cleans word display text by stripping ornate braces, brackets, and extra spaces.
 */
fun cleanWordText(word: String): String {
    return word.replace("{", "")
        .replace("}", "")
        .replace("﴿", "")
        .replace("﴾", "")
        .trim()
}

/**
 * Returns the authentic Uthmani Ayah end glyph from the Hafs font.
 * In the Hafs font (R.font.hafs), glyphs from U+FC00 upwards represent the authentic
 * ornamental Ayah frame with the verse number embedded inside it (e.g. Ayah 1 -> \uFC00, Ayah 2 -> \uFC01, etc.).
 */
fun getAyahEndGlyph(ayahNumber: Int): String {
    return if (ayahNumber in 1..286) {
        val charCode = 0xFC00 + (ayahNumber - 1)
        "${charCode.toChar()}"
    } else {
        toArabicNumerals(ayahNumber)
    }
}

