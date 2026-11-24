package dev.sayed.mehrabalmomen.data.recitation.tracking

/**
 * Model-Specific Acoustic Cost & Phonetic Distance Matrix.
 */
object PhoneticCostEngine {
    
    fun isZeroCostMarker(code: Char): Boolean {
        return code == '\u0686' || // Qalqalah release burst
               code == '\u06DC' || // Sakt
               code == '\u0619' || // Ishmam
               code == '\u06EA' || // Imalah
               code == '\u0640'    // Tatweel
    }

    fun isEquivalentGlyph(asrCode: Char, refCode: Char): Boolean {
        if (asrCode == refCode) return true

        var a = asrCode
        var r = refCode
        if (a > r) {
            val temp = a
            a = r
            r = temp
        }

        if (a == 'م' && r == '۾') return true // Iqlab
        if (a == 'ن' && r == 'ں') return true // Ikhfaa
        if (a == 'و' && r == 'ۥ') return true // Waw
        if (a == 'ي' && r == 'ۦ') return true // Yaa

        if (isHamzaVariant(a) && isHamzaVariant(r)) return true

        if (a == 'ة' && r == 'ه') return true
        if (a == 'ت' && r == 'ة') return true

        return false
    }

    private fun isHamzaVariant(code: Char): Boolean =
        code == 'ء' || code == 'آ' || code == 'أ' || code == 'إ' || code == 'ٲ'

    private fun isAcousticConfusion(asrCode: Char, refCode: Char): Boolean {
        var a = asrCode
        var r = refCode
        if (a > r) {
            val temp = a
            a = r
            r = temp
        }

        return when (a) {
            'ا' -> r == 'َ'
            'و' -> r == 'ُ'
            'ُ' -> r == 'ۥ'
            'ي' -> r == 'ِ'
            'ِ' -> r == 'ۦ'
            'ت' -> r == 'ط'
            'ج' -> r == 'ز'
            'خ' -> r == 'غ'
            'د' -> r == 'ض'
            'ذ' -> r == 'ز' || r == 'ظ'
            'س' -> r == 'ص'
            'ق' -> r == 'ك'
            else -> false
        }
    }

    fun isTashkeel(code: Char): Boolean =
        code == 'َ' || code == 'ُ' || code == 'ِ'

    fun isMaddVowel(code: Char): Boolean =
        code == 'ا' || code == 'و' || code == 'ي' || code == 'ۥ' || code == 'ۦ'

    fun getSubstitutionCost(asrCode: Char, refCode: Char, acousticConfusionCost: Double = 0.25): Double {
        if (asrCode == refCode) return 0.0
        if (isEquivalentGlyph(asrCode, refCode)) return 0.0
        if (isAcousticConfusion(asrCode, refCode)) return acousticConfusionCost
        if (isTashkeel(asrCode) || isTashkeel(refCode)) return 1.00
        return 1.00
    }

    fun getDeletionCost(
        fullPhonemes: String,
        gRefIdx: Int,
        standardDeletionCost: Double = 1.0,
        acousticConfusionCost: Double = 0.25
    ): Double {
        if (gRefIdx < 0 || gRefIdx >= fullPhonemes.length) return standardDeletionCost
        val code = fullPhonemes[gRefIdx]

        if (isZeroCostMarker(code)) return 0.0
        if (isHamzaVariant(code)) return acousticConfusionCost
        if (gRefIdx > 0 && code == fullPhonemes[gRefIdx - 1]) return acousticConfusionCost

        return standardDeletionCost
    }

    fun getInsertionCost(
        asrText: String,
        asrIdx: Int,
        standardInsertionCost: Double = 1.0,
        acousticConfusionCost: Double = 0.25
    ): Double {
        if (asrIdx < 0 || asrIdx >= asrText.length) return standardInsertionCost
        val code = asrText[asrIdx]

        if (isZeroCostMarker(code)) return 0.0
        if (asrIdx > 0 && code == asrText[asrIdx - 1] && isMaddVowel(code)) {
            return acousticConfusionCost
        }

        return standardInsertionCost
    }
}
