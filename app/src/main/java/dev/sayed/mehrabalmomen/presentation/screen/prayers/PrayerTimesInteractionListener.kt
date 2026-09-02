package dev.sayed.mehrabalmomen.presentation.screen.prayers

import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer

interface PrayerTimesInteractionListener {
    fun onClickBack()
    fun onClickEnablePrayer(
        prayerName: Prayer.PrayerName,
        isEnabled: Boolean
    )
}
