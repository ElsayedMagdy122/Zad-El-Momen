package dev.sayed.mehrabalmomen.presentation.di

import dev.sayed.mehrabalmomen.domain.repository.platform.WidgetPinProvider
import dev.sayed.mehrabalmomen.presentation.widget.WidgetPinProviderImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val presentationPlatformModule = module {
    single<WidgetPinProvider> { WidgetPinProviderImpl(androidContext()) }
}
