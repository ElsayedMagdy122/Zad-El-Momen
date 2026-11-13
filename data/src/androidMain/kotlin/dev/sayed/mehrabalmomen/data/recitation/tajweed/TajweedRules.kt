package dev.sayed.mehrabalmomen.data.recitation.tajweed

import dev.sayed.mehrabalmomen.data.recitation.tracking.RecitationSpeed

data class LangName(val ar: String, val en: String)

enum class TajweedDurationStatus {
    valid, underheld, overheld
}

data class AcousticThreshold(
    val minSeconds: Double,
    val targetSeconds: Double,
    val maxSeconds: Double
) {
    fun evaluate(measuredSeconds: Double): TajweedDurationStatus {
        if (measuredSeconds.isNaN() || measuredSeconds.isInfinite() || measuredSeconds < 0.0) {
            return TajweedDurationStatus.valid
        }
        if (measuredSeconds < minSeconds) return TajweedDurationStatus.underheld
        if (measuredSeconds > maxSeconds) return TajweedDurationStatus.overheld
        return TajweedDurationStatus.valid
    }
}

abstract class TajweedRule(
    val name: LangName,
    val goldenLen: Number
) {
    abstract fun getThreshold(speed: RecitationSpeed): AcousticThreshold

    fun checkDurationStatus(durationSeconds: Double, speed: RecitationSpeed = RecitationSpeed.normal): TajweedDurationStatus =
        getThreshold(speed).evaluate(durationSeconds)

    fun getRequiredDuration(speed: RecitationSpeed = RecitationSpeed.normal): Double =
        getThreshold(speed).targetSeconds

    fun getMinAllowedDuration(speed: RecitationSpeed = RecitationSpeed.normal): Double =
        getThreshold(speed).minSeconds

    fun getMaxAllowedDuration(speed: RecitationSpeed = RecitationSpeed.normal): Double =
        getThreshold(speed).maxSeconds
}

class NormalMaddRule : TajweedRule(LangName("المد الطبيعي", "Normal Madd"), 2) {
    override fun getThreshold(speed: RecitationSpeed): AcousticThreshold = when (speed) {
        RecitationSpeed.fast -> AcousticThreshold(0.075, 0.240, 0.600)
        RecitationSpeed.normal -> AcousticThreshold(0.150, 0.300, 0.700)
        RecitationSpeed.slow -> AcousticThreshold(0.180, 0.360, 0.850)
    }
}

class MonfaselMaddRule(harakat: Int = 4) : TajweedRule(LangName("المد المنفصل", "Monfasel Madd"), harakat) {
    override fun getThreshold(speed: RecitationSpeed): AcousticThreshold = when (speed) {
        RecitationSpeed.fast -> AcousticThreshold(0.075, 0.240, 0.800)
        RecitationSpeed.normal -> AcousticThreshold(0.450, 0.600, 1.400)
        RecitationSpeed.slow -> AcousticThreshold(0.650, 0.900, 1.800)
    }
}

class MottaselMaddRule(harakat: Int = 4) : TajweedRule(LangName("المد المتصل", "Mottasel Madd"), harakat) {
    override fun getThreshold(speed: RecitationSpeed): AcousticThreshold = when (speed) {
        RecitationSpeed.fast -> AcousticThreshold(0.350, 0.480, 1.200)
        RecitationSpeed.normal -> AcousticThreshold(0.450, 0.600, 1.500)
        RecitationSpeed.slow -> AcousticThreshold(0.650, 0.900, 1.800)
    }
}

class AaredMaddRule(harakat: Int = 4) : TajweedRule(LangName("المد العارض للسكون", "Aared Madd"), harakat) {
    override fun getThreshold(speed: RecitationSpeed): AcousticThreshold = when (speed) {
        RecitationSpeed.fast -> AcousticThreshold(0.120, 0.240, 1.800)
        RecitationSpeed.normal -> AcousticThreshold(0.300, 0.600, 2.200)
        RecitationSpeed.slow -> AcousticThreshold(0.500, 1.080, 2.500)
    }
}

class LazemMaddRule : TajweedRule(LangName("المد اللازم", "Lazem Madd"), 6) {
    override fun getThreshold(speed: RecitationSpeed): AcousticThreshold = when (speed) {
        RecitationSpeed.fast -> AcousticThreshold(0.540, 0.720, 4.000)
        RecitationSpeed.normal -> AcousticThreshold(0.680, 0.900, 4.000)
        RecitationSpeed.slow -> AcousticThreshold(0.850, 1.080, 4.500)
    }
}

class MushaddadGhunnahRule(
    name: LangName = LangName("النون أو الميم المشددة", "Mushaddad Noon/Meem")
) : TajweedRule(name, 2) {
    override fun getThreshold(speed: RecitationSpeed): AcousticThreshold = when (speed) {
        RecitationSpeed.fast -> AcousticThreshold(0.180, 0.240, 0.850)
        RecitationSpeed.normal -> AcousticThreshold(0.225, 0.300, 1.250)
        RecitationSpeed.slow -> AcousticThreshold(0.270, 0.360, 1.600)
    }
}

class ShaddahRule : TajweedRule(LangName("الشدة", "Shaddah"), 1.5) {
    override fun getThreshold(speed: RecitationSpeed): AcousticThreshold = when (speed) {
        RecitationSpeed.fast -> AcousticThreshold(0.190, 0.240, 0.650)
        RecitationSpeed.normal -> AcousticThreshold(0.200, 0.250, 0.850)
        RecitationSpeed.slow -> AcousticThreshold(0.240, 0.300, 1.000)
    }
}

class MottaselMaddPauseRule(harakat: Int = 4) : TajweedRule(LangName("المد المتصل موقوفا عليه", "Mottasel Madd (Pause)"), harakat) {
    override fun getThreshold(speed: RecitationSpeed): AcousticThreshold = when (speed) {
        RecitationSpeed.fast -> AcousticThreshold(0.350, 0.480, 1.500)
        RecitationSpeed.normal -> AcousticThreshold(0.450, 0.600, 2.000)
        RecitationSpeed.slow -> AcousticThreshold(0.650, 0.900, 2.500)
    }
}

class LeenMaddRule(harakat: Int = 2) : TajweedRule(LangName("مد اللين", "Leen Madd"), harakat) {
    override fun getThreshold(speed: RecitationSpeed): AcousticThreshold = when (speed) {
        RecitationSpeed.fast -> AcousticThreshold(0.120, 0.240, 1.800)
        RecitationSpeed.normal -> AcousticThreshold(0.300, 0.600, 2.200)
        RecitationSpeed.slow -> AcousticThreshold(0.500, 1.080, 2.500)
    }
}

