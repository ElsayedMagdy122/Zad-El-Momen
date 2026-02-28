package dev.sayed.mehrabalmomen.presentation.screen.azkar

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.domain.entity.azkar.AzkarCategory


data class AzkarUiState(
    val isLoading: Boolean = false,
    val categories: List<AzkarCategoryUiModel> = emptyList()
)
fun AzkarCategory.toUiModel(): AzkarCategoryUiModel {
    return AzkarCategoryUiModel(
        type = title.toAzkarType()
    )
}

data class AzkarCategoryUiModel(
    val type: AzkarType
)

enum class AzkarType(
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int,
    val domainTitle: String
) {
    MORNING(
        R.string.azkar_morning,
        R.drawable.ic_morning,
        "أذكار الصباح"
    ),
    EVENING(
        R.string.azkar_evening,
        R.drawable.ic_moon,
        "أذكار المساء"
    ),
    AFTER_PRAYER(
        R.string.azkar_after_prayer,
        R.drawable.mosque_02,
        "أذكار بعد الصلاة المفروضة"
    ),
    TASBIH(
        R.string.azkar_tasbih,
        R.drawable.ic_tasbih1,
        "تسابيح"
    ),
    SLEEP(
        R.string.azkar_sleep,
        R.drawable.ic_sleep,
        "أذكار النوم"
    ),
    WAKE(
        R.string.azkar_wake,
        R.drawable.ic_sunrise,
        "أذكار الاستيقاظ"
    ),
    QURAN_DUA(
        R.string.azkar_quran_dua,
        R.drawable.ic_quran02,
        "أدعية قرآنية"
    ),
    PROPHETS_DUA(
        R.string.azkar_prophets_dua,
        R.drawable.ic_dua,
        "أدعية الأنبياء"
    )
}
