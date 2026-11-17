package dev.sayed.mehrabalmomen.presentation.di

import dev.sayed.mehrabalmomen.data.platform.IosWidgetPinProvider
import dev.sayed.mehrabalmomen.domain.repository.platform.WidgetPinProvider
import org.koin.dsl.module

val presentationPlatformModule = module {
    single<WidgetPinProvider> { IosWidgetPinProvider() }
}
