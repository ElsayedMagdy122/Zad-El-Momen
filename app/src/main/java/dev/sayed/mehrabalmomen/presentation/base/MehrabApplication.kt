package dev.sayed.mehrabalmomen.presentation.base

import android.app.Application
import android.os.StrictMode
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.google.firebase.perf.performance
import dev.sayed.mehrabalmomen.BuildConfig
import dev.sayed.mehrabalmomen.data.di.dataModule
import dev.sayed.mehrabalmomen.domain.di.domainModule
import dev.sayed.mehrabalmomen.presentation.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.maplibre.android.MapLibre
import java.util.Locale

class MehrabApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MehrabApplication)
            modules(presentationModule, domainModule, *dataModule.toTypedArray())
        }
        val language = Locale.getDefault().language
        Firebase.messaging.subscribeToTopic("lang_$language")
        MapLibre.getInstance(this)
        if (BuildConfig.DEBUG) {
            setupStrictMode()
            Firebase.performance.isPerformanceCollectionEnabled = false
        } else {

            Firebase.performance.isPerformanceCollectionEnabled = true
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