package dev.sayed.mehrabalmomen.presentation.screen.azkar

import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

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
    val iconRes: DrawableResource,
    val itemCount: Int
)

fun getIconForCategory(title: String): DrawableResource {
    return when (title) {
        AzkarType.MORNING.domainTitle -> AzkarType.MORNING.iconRes
        AzkarType.EVENING.domainTitle -> AzkarType.EVENING.iconRes
        AzkarType.AFTER_PRAYER.domainTitle -> AzkarType.AFTER_PRAYER.iconRes
        AzkarType.TASBIH.domainTitle -> AzkarType.TASBIH.iconRes
        AzkarType.SLEEP.domainTitle -> AzkarType.SLEEP.iconRes
        AzkarType.WAKE.domainTitle -> AzkarType.WAKE.iconRes
        AzkarType.QURAN_DUA.domainTitle -> AzkarType.QURAN_DUA.iconRes
        AzkarType.PROPHETS_DUA.domainTitle -> AzkarType.PROPHETS_DUA.iconRes
        else -> Res.drawable.ic_azkar_selected
    }
}

enum class AzkarType(
    val titleRes: StringResource,
    val iconRes: DrawableResource,
    val domainTitle: String
) {
    MORNING(
        Res.string.azkar_morning,
        Res.drawable.ic_morning,
        "أذكار الصباح"
    ),
    EVENING(
        Res.string.azkar_evening,
        Res.drawable.ic_moon,
        "أذكار المساء"
    ),
    AFTER_PRAYER(
        Res.string.azkar_after_prayer,
        Res.drawable.mosque_02,
        "أذكار بعد الصلاة المفروضة"
    ),
    TASBIH(
        Res.string.azkar_tasbih,
        Res.drawable.ic_tasbih1,
        "تسابيح"
    ),
    SLEEP(
        Res.string.azkar_sleep,
        Res.drawable.ic_sleep,
        "أذكار النوم"
    ),
    WAKE(
        Res.string.azkar_wake,
        Res.drawable.ic_sunrise,
        "أذكار الاستيقاظ"
    ),
    QURAN_DUA(
        Res.string.azkar_quran_dua,
        Res.drawable.ic_quran02,
        "أدعية قرآنية"
    ),
    PROPHETS_DUA(
        Res.string.azkar_prophets_dua,
        Res.drawable.ic_dua,
        "أدعية الأنبياء"
    )
}
