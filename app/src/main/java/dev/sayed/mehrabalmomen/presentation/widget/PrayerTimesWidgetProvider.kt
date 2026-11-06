package dev.sayed.mehrabalmomen.presentation.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.view.View
import android.widget.RemoteViews
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.domain.repository.prayer.PrayerRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.base.MainActivity
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits
import dev.sayed.mehrabalmomen.presentation.utils.format
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.context.GlobalContext
import java.util.Calendar
import java.util.Locale
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class PrayerTimesWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                for (widgetId in appWidgetIds) {
                    updateSingleWidget(context, appWidgetManager, widgetId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH_WIDGET) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, PrayerTimesWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }

    private suspend fun updateSingleWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val koin = GlobalContext.getOrNull()
        if (koin == null) return

        val settingsRepository = koin.get<SettingsRepository>()
        val prayerRepository = koin.get<PrayerRepository>()

        val appSettings = settingsRepository.observeAppSettings().first()
        val prayerSettings = appSettings.prayerSettings
        val location = prayerSettings.location
        val language = appSettings.language

        // Choose layout direction matching app language (RTL for Arabic, LTR for English)
        val layoutRes = if (language == AppSettings.Language.ARABIC) {
            R.layout.widget_prayer_times
        } else {
            R.layout.widget_prayer_times_ltr
        }
        val views = RemoteViews(context.packageName, layoutRes)

        // PendingIntent to launch MainActivity when clicking the widget
        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val mainPendingIntent = PendingIntent.getActivity(
            context,
            appWidgetId,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_root, mainPendingIntent)

        // Mosque background adaptation based on system dark/light mode
        val isNightMode = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        val mosqueBgRes = if (isNightMode) R.drawable.ic_mosque_dark_bg else R.drawable.ic_mosque_light_bg
        views.setImageViewResource(R.id.iv_widget_mosque_bg, mosqueBgRes)

        // Create localized Context according to the app's selected language
        val locale = Locale(language.code)
        val config = Configuration(context.resources.configuration).apply {
            setLocale(locale)
            setLayoutDirection(locale)
        }
        val localizedContext = context.createConfigurationContext(config)

        val isLocationValid = location.latitude != 0.0 || location.longitude != 0.0

        if (!isLocationValid) {
            views.setViewVisibility(R.id.layout_prayers_content, View.GONE)
            views.setViewVisibility(R.id.layout_empty_location, View.VISIBLE)
            views.setTextViewText(
                R.id.tv_widget_empty_text,
                localizedContext.getString(R.string.set_location_to_view_prayers)
            )
            appWidgetManager.updateAppWidget(appWidgetId, views)
            return
        }

        views.setViewVisibility(R.id.layout_prayers_content, View.VISIBLE)
        views.setViewVisibility(R.id.layout_empty_location, View.GONE)

        // 1. Hijri date placed prominently on start
        val hijriDate = getHijriDate(language)
        views.setTextViewText(R.id.tv_widget_hijri_date, hijriDate)

        val timeZone = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(timeZone).date

        val dailyPrayers = prayerRepository.getDailyPrayers(
            madhab = prayerSettings.madhab,
            calculationMethod = prayerSettings.calculationMethod,
            location = Location(location.latitude, location.longitude),
            date = today
        )

        val nextPrayer = prayerRepository.getNextPrayer(
            instant = Clock.System.now(),
            madhab = prayerSettings.madhab,
            calculationMethod = prayerSettings.calculationMethod,
            location = Location(location.latitude, location.longitude),
            date = today
        )

        // 2. Next prayer header summary localized
        val nextPrayerName = localizedContext.getString(getPrayerNameRes(nextPrayer.name))
        views.setTextViewText(
            R.id.tv_widget_next_prayer_header,
            "${localizedContext.getString(R.string.next_prayer)}: $nextPrayerName"
        )

        // 3. Bind 5 Prayers with full localization and small downward triangle indicator only
        for (prayer in dailyPrayers) {
            val isNext = prayer.name == nextPrayer.name
            val formatted = format(prayer.time, timeZone)
            val timeText = formatted.time.toLocalizedDigits(language)
            val amPmText = localizedContext.getString(if (formatted.isAm) R.string.am else R.string.pm)

            when (prayer.name) {
                Prayer.PrayerName.FAJR -> {
                    views.setTextViewText(R.id.tv_name_fajr, localizedContext.getString(R.string.fajr))
                    views.setTextViewText(R.id.tv_time_fajr, timeText)
                    views.setTextViewText(R.id.tv_ampm_fajr, amPmText)
                    views.setViewVisibility(
                        R.id.iv_indicator_fajr,
                        if (isNext) View.VISIBLE else View.INVISIBLE
                    )
                }
                Prayer.PrayerName.ZUHR -> {
                    views.setTextViewText(R.id.tv_name_dhuhr, localizedContext.getString(R.string.dhuhr))
                    views.setTextViewText(R.id.tv_time_dhuhr, timeText)
                    views.setTextViewText(R.id.tv_ampm_dhuhr, amPmText)
                    views.setViewVisibility(
                        R.id.iv_indicator_dhuhr,
                        if (isNext) View.VISIBLE else View.INVISIBLE
                    )
                }
                Prayer.PrayerName.ASR -> {
                    views.setTextViewText(R.id.tv_name_asr, localizedContext.getString(R.string.asr))
                    views.setTextViewText(R.id.tv_time_asr, timeText)
                    views.setTextViewText(R.id.tv_ampm_asr, amPmText)
                    views.setViewVisibility(
                        R.id.iv_indicator_asr,
                        if (isNext) View.VISIBLE else View.INVISIBLE
                    )
                }
                Prayer.PrayerName.MAGHRIB -> {
                    views.setTextViewText(R.id.tv_name_maghrib, localizedContext.getString(R.string.maghrib))
                    views.setTextViewText(R.id.tv_time_maghrib, timeText)
                    views.setTextViewText(R.id.tv_ampm_maghrib, amPmText)
                    views.setViewVisibility(
                        R.id.iv_indicator_maghrib,
                        if (isNext) View.VISIBLE else View.INVISIBLE
                    )
                }
                Prayer.PrayerName.ISHA -> {
                    views.setTextViewText(R.id.tv_name_isha, localizedContext.getString(R.string.isha))
                    views.setTextViewText(R.id.tv_time_isha, timeText)
                    views.setTextViewText(R.id.tv_ampm_isha, amPmText)
                    views.setViewVisibility(
                        R.id.iv_indicator_isha,
                        if (isNext) View.VISIBLE else View.INVISIBLE
                    )
                }
            }
        }

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getPrayerNameRes(prayerName: Prayer.PrayerName): Int {
        return when (prayerName) {
            Prayer.PrayerName.FAJR -> R.string.fajr
            Prayer.PrayerName.ZUHR -> R.string.dhuhr
            Prayer.PrayerName.ASR -> R.string.asr
            Prayer.PrayerName.MAGHRIB -> R.string.maghrib
            Prayer.PrayerName.ISHA -> R.string.isha
        }
    }

    private fun getHijriDate(language: AppSettings.Language): String {
        return try {
            val calendar = UmmalquraCalendar()
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)

            val monthsAr = listOf(
                "محرم", "صفر", "ربيع الأول", "ربيع الآخر",
                "جمادى الأولى", "جمادى الآخرة",
                "رجب", "شعبان", "رمضان",
                "شوال", "ذو القعدة", "ذو الحجة"
            )

            val monthsEn = listOf(
                "Muharram", "Safar", "Rabi Al-Awwal", "Rabi Al-Thani",
                "Jumada Al-Awwal", "Jumada Al-Thani",
                "Rajab", "Shaban", "Ramadan",
                "Shawwal", "Dhul Qadah", "Dhul Hijjah"
            )

            val monthName = if (language == AppSettings.Language.ARABIC) {
                monthsAr.getOrElse(month) { "" }
            } else {
                monthsEn.getOrElse(month) { "" }
            }

            val dayStr = day.toString().toLocalizedDigits(language)
            val yearStr = year.toString().toLocalizedDigits(language)

            "$dayStr $monthName $yearStr"
        } catch (_: Exception) {
            ""
        }
    }

    companion object {
        const val ACTION_REFRESH_WIDGET = "dev.sayed.mehrabalmomen.action.REFRESH_PRAYER_WIDGET"

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, PrayerTimesWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            if (appWidgetIds != null && appWidgetIds.isNotEmpty()) {
                val intent = Intent(context, PrayerTimesWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
                }
                context.sendBroadcast(intent)
            }
        }
    }
}
