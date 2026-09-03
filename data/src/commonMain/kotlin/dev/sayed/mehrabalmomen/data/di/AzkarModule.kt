package dev.sayed.mehrabalmomen.data.di

import dev.sayed.mehrabalmomen.data.azkar.repository.AzkarRepositoryImpl
import dev.sayed.mehrabalmomen.domain.repository.azkar.AzkarRepository
import org.koin.dsl.module

val azkarModule = module {
    single<AzkarRepository> { AzkarRepositoryImpl(get()) }
}
