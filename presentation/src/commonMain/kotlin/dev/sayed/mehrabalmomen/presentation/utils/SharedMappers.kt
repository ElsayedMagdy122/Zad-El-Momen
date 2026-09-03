package dev.sayed.mehrabalmomen.presentation.utils

import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.design_system.utils.UiIcon
import dev.sayed.mehrabalmomen.design_system.utils.UiText

expect fun Prayer.toUiIcon(prayerName: Prayer.PrayerName): UiIcon
expect fun Prayer.toUiName(prayerName: Prayer.PrayerName): UiText
