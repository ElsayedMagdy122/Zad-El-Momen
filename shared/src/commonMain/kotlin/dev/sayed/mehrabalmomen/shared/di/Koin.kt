package dev.sayed.mehrabalmomen.shared.di

import dev.sayed.mehrabalmomen.data.di.dataModule
import dev.sayed.mehrabalmomen.domain.di.domainModule
import dev.sayed.mehrabalmomen.presentation.di.presentationModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            presentationModule,
            domainModule,
            *dataModule.toTypedArray()
        )
    }

// iOS specific init
fun initKoin() = initKoin {}
