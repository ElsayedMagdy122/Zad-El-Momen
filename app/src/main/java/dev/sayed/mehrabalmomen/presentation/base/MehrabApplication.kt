package dev.sayed.mehrabalmomen.presentation.base

import android.app.Application
import dev.sayed.mehrabalmomen.data.di.dataModule
import dev.sayed.mehrabalmomen.presentation.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.maplibre.android.MapLibre

class MehrabApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MehrabApplication)
            modules(presentationModule, dataModule)
        }
        MapLibre.getInstance(this)
    }
}