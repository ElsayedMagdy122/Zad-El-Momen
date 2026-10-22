package dev.sayed.mehrabalmomen.presentation.screen.azkar

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.sayed.mehrabalmomen.R

data class AzkarUiState(
    val isLoading: Boolean = false,
    val categories: List<AzkarCategoryUiModel> = emptyList(),
    val isAddCategoryBottomSheetOpen: Boolean = false,
    val newCategoryTitle: String = "",
    val streak: Int = 0,
    val completedCountToday: Int = 0,
    val remainingCount: Int = 0,
    val completionPercentage: Int = 0,
    val totalAzkarCount: Int = 0,
    val historyMap: Map<String, Int> = emptyMap(),
    val selectedFilter: AzkarTimeFilter = AzkarTimeFilter.YEAR
)

enum class AzkarTimeFilter {
    DAY, WEEK, MONTH, YEAR
}

data class AzkarCategoryUiModel(
    val title: String,
    @DrawableRes val iconRes: Int,
    val itemCount: Int
)

fun getIconForCategory(title: String): Int {
    return when (title) {
        AzkarType.MORNING.domainTitle -> AzkarType.MORNING.iconRes
        AzkarType.EVENING.domainTitle -> AzkarType.EVENING.iconRes
        AzkarType.AFTER_PRAYER.domainTitle -> AzkarType.AFTER_PRAYER.iconRes
        AzkarType.TASBIH.domainTitle -> AzkarType.TASBIH.iconRes
        AzkarType.SLEEP.domainTitle -> AzkarType.SLEEP.iconRes
        AzkarType.WAKE.domainTitle -> AzkarType.WAKE.iconRes
        AzkarType.QURAN_DUA.domainTitle -> AzkarType.QURAN_DUA.iconRes
        AzkarType.PROPHETS_DUA.domainTitle -> AzkarType.PROPHETS_DUA.iconRes
        else -> R.drawable.ic_sunrise
    }
}

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
