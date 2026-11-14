package dev.sayed.mehrabalmomen.shared.di

import dev.sayed.mehrabalmomen.domain.di.domainModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            domainModule
        )
    }

fun initKoin() = initKoin {}
