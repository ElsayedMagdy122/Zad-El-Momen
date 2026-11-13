package dev.sayed.mehrabalmomen.di

import dev.sayed.mehrabalmomen.data.di.dataModule
import dev.sayed.mehrabalmomen.presentation.di.presentationModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            dataModule + presentationModule
        )
    }

// For iOS
fun initKoin() = initKoin {}
