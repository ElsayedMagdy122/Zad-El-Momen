package dev.sayed.mehrabalmomen.presentation.screen.azkar

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.domain.entity.AzkarCategory


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
        R.drawable.shalat_zhuhur,
        "أذكار الصباح"
    ),
    EVENING(
        R.string.azkar_evening,
        R.drawable.shalat_isya,
        "أذكار المساء"
    ),
    AFTER_PRAYER(
        R.string.azkar_after_prayer,
        R.drawable.mosque_02,
        "أذكار بعد الصلاة المفروضة"
    ),
    TASBIH(
        R.string.azkar_tasbih,
        R.drawable.ic_tasbih,
        "تسابيح"
    ),
    SLEEP(
        R.string.azkar_sleep,
        R.drawable.ic_sleeping1,
        "أذكار النوم"
    ),
    WAKE(
        R.string.azkar_wake,
        R.drawable.ic_rise,
        "أذكار الاستيقاظ"
    ),
    QURAN_DUA(
        R.string.azkar_quran_dua,
        R.drawable.ic_quran_dua,
        "أدعية قرآنية"
    ),
    PROPHETS_DUA(
        R.string.azkar_prophets_dua,
        R.drawable.ic_dua_hands,
        "أدعية الأنبياء"
    )
}
