package dev.sayed.mehrabalmomen.presentation.base

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.StrictMode
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.google.firebase.perf.performance
import dev.sayed.mehrabalmomen.BuildConfig
import dev.sayed.mehrabalmomen.data.di.dataModule
import dev.sayed.mehrabalmomen.domain.di.domainModule
import dev.sayed.mehrabalmomen.presentation.di.presentationModule
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetSettingsRefreshObserver
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetUpdateCoordinator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.GlobalContext.startKoin
import org.maplibre.android.MapLibre
import java.util.Locale

class MehrabApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MehrabApplication)
            modules(presentationModule, domainModule, *dataModule.toTypedArray())
        }
        startPrayerWidgetSettingsObserver()
        registerPrayerWidgetDateRefresh()
        val language = Locale.getDefault().language
        Firebase.messaging.subscribeToTopic("lang_$language")
        MapLibre.getInstance(this)
        if (BuildConfig.DEBUG) {
        //    setupStrictMode()
            Firebase.performance.isPerformanceCollectionEnabled = false
        } else {

            Firebase.performance.isPerformanceCollectionEnabled = true
        }
    }

    /**
     * Starts the application-lifetime observer that refreshes widgets after saved settings change.
     *
     * @return no value; after completion, the observer is running in [applicationScope].
     */
    private fun startPrayerWidgetSettingsObserver() {
        GlobalContext.get().get<PrayerWidgetSettingsRefreshObserver>().start(applicationScope)
    }

    /**
     * Registers a process-lifetime receiver for runtime date changes while the app is alive.
     *
     * Manifest delivery of `DATE_CHANGED` is not reliable on modern Android, so exact local
     * midnight remains the authoritative widget rollover path. This receiver is only a live-process
     * helper.
     *
     * @return no value; after completion, date changes can trigger an installed-widget refresh.
     */
    private fun registerPrayerWidgetDateRefresh() {
        val receiver = object : BroadcastReceiver() {
            /**
             * Refreshes installed widgets when Android reports that the civil date changed.
             *
             * @param context context supplied by Android for this runtime broadcast.
             * @param intent broadcast intent whose action must be [Intent.ACTION_DATE_CHANGED].
             * @return no value; after completion, installed widgets are refreshed asynchronously.
             */
            override fun onReceive(context: Context, intent: Intent?) {
                if (intent?.action != Intent.ACTION_DATE_CHANGED) return

                applicationScope.launch {
                    GlobalContext.get()
                        .get<PrayerWidgetUpdateCoordinator>()
                        .refreshAllIfInstalled()
                }
            }
        }
        val filter = IntentFilter(Intent.ACTION_DATE_CHANGED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            registerReceiver(receiver, filter)
        }
    }

    private fun setupStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .penaltyDeath()
                .build()
        )

        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectLeakedClosableObjects()
                .detectLeakedSqlLiteObjects()
                .detectActivityLeaks()
                .penaltyLog()
                .build()
        )
    }

}
