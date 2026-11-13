package dev.sayed.mehrabalmomen.presentation.base

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.google.firebase.perf.performance
import dev.sayed.mehrabalmomen.BuildConfig
import dev.sayed.mehrabalmomen.di.initKoin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.maplibre.android.MapLibre
import java.util.Locale

class MehrabApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MehrabApplication)
        }

        applicationScope.launch {
            val language = Locale.getDefault().language
            Firebase.messaging.subscribeToTopic("lang_$language")
        }
        MapLibre.getInstance(this@MehrabApplication)

        if (BuildConfig.DEBUG) {
            Firebase.performance.isPerformanceCollectionEnabled = false
        } else {
            Firebase.performance.isPerformanceCollectionEnabled = true
        }
    }
}
