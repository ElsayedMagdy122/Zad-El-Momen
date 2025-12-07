package dev.sayed.mehrabalmomen

import android.app.Application
import dev.sayed.mehrabalmomen.data.di.dataModule
import dev.sayed.mehrabalmomen.presentation.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MehrabApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MehrabApplication)
            modules(presentationModule, dataModule)
        }

    }
}