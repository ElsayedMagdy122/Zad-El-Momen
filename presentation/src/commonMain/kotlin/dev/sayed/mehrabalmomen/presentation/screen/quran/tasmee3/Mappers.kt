package dev.sayed.mehrabalmomen.presentation.screen.quran.tasmee3

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

fun cleanWordText(word: String): String {
    return word.replace("{", "")
        .replace("}", "")
        .replace("﴿", "")
        .replace("﴾", "")
        .trim()
}

fun getAyahEndGlyph(ayahNumber: Int): String {
    return if (ayahNumber in 1..286) {
        val charCode = 0xFC00 + (ayahNumber - 1)
        "${charCode.toChar()}"
    } else {
        toArabicNumerals(ayahNumber)
    }
}
