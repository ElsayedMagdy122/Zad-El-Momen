package dev.sayed.mehrabalmomen.presentation.widget.prayer

import android.content.Context
import dev.sayed.mehrabalmomen.R

object PrayerWidgetFixture {
    fun ready(context: Context): PrayerWidgetUiState {
        val am = context.getString(R.string.am)
        val pm = context.getString(R.string.pm)
        val maghrib = context.getString(R.string.maghrib)

        return PrayerWidgetUiState(
            status = PrayerWidgetStatus.READY,
            nextPrayerName = maghrib,
            countdown = "01:42:18".localizedDigits(context),
            countdownProgress = 5_500,
            prayers = listOf(
                PrayerWidgetPrayer(
                    name = context.getString(R.string.fajr),
                    time = "05:12 $am".localizedDigits(context),
                    iconRes = R.drawable.shalat_shubuh,
                ),
                PrayerWidgetPrayer(
                    name = context.getString(R.string.dhuhr),
                    time = "12:08 $pm".localizedDigits(context),
                    iconRes = R.drawable.shalat_zhuhur,
                ),
                PrayerWidgetPrayer(
                    name = context.getString(R.string.asr),
                    time = "03:29 $pm".localizedDigits(context),
                    iconRes = R.drawable.shalat_ashar,
                ),
                PrayerWidgetPrayer(
                    name = maghrib,
                    time = "06:24 $pm".localizedDigits(context),
                    iconRes = R.drawable.shalat_maghrib,
                    isUpcoming = true,
                ),
                PrayerWidgetPrayer(
                    name = context.getString(R.string.isha),
                    time = "07:44 $pm".localizedDigits(context),
                    iconRes = R.drawable.shalat_isya,
                ),
            ),
        )
    }
}
