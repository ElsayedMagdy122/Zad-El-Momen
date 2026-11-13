package dev.sayed.mehrabalmomen.di

import dev.sayed.mehrabalmomen.presentation.utils.WidgetHandler
import dev.sayed.mehrabalmomen.presentation.widget.AndroidWidgetHandler
import org.koin.dsl.module

val appModule = module {
    single<WidgetHandler> { AndroidWidgetHandler(get()) }
}
