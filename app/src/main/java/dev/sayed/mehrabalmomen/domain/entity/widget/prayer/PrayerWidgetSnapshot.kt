package dev.sayed.mehrabalmomen.domain.entity.widget.prayer

/** Represents every domain outcome that the prayer widget presentation can render. */
sealed interface PrayerWidgetSnapshot {
    /**
     * Contains calculated widget data when all required permissions are available.
     *
     * @property content calculated prayer information ready for presentation.
     */
    data class Ready(
        val content: PrayerWidgetContent,
    ) : PrayerWidgetSnapshot

    /**
     * Preserves calculated content while indicating that exact alarm access is required.
     *
     * @property content calculated prayer information that can still be rendered.
     */
    data class PermissionRequired(
        val content: PrayerWidgetContent,
    ) : PrayerWidgetSnapshot

    /** Indicates that prayer calculation cannot begin until the user configures a location. */
    data object NeedsLocation : PrayerWidgetSnapshot

    /**
     * Represents a controlled calculation or infrastructure failure.
     *
     * @property error category that presentation code can map to an error state.
     */
    data class Error(
        val error: PrayerWidgetError,
    ) : PrayerWidgetSnapshot
}
