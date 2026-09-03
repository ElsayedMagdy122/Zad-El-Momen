package dev.sayed.mehrabalmomen.domain.repository.prayer

import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.design_system.utils.UiIcon
import dev.sayed.mehrabalmomen.design_system.utils.UiText

interface PrayerResourceProvider {
    fun getPrayerName(name: Prayer.PrayerName): UiText
    fun getPrayerIcon(name: Prayer.PrayerName): UiIcon
}
