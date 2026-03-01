package dev.sayed.mehrabalmomen.data.di

import com.google.gson.Gson
import dev.sayed.mehrabalmomen.data.util.BillingManager
import dev.sayed.mehrabalmomen.presentation.utils.AlarmScheduler
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreModule = module {
    single { Gson() }
    single { BillingManager(get()) }
    single { AlarmScheduler(androidContext()) }
}