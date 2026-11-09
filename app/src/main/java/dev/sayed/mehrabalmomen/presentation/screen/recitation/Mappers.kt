package dev.sayed.mehrabalmomen.presentation.screen.recitation

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
