package dev.sayed.mehrabalmomen.presentation.widget.prayer

import androidx.annotation.DrawableRes

data class PrayerWidgetPrayer(
    val name: String,
    val time: String,
    @param:DrawableRes val iconRes: Int,
    val isUpcoming: Boolean = false,
)
