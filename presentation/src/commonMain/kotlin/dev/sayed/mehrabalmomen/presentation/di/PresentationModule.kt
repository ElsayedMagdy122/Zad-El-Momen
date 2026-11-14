package dev.sayed.mehrabalmomen.presentation.di

import dev.sayed.mehrabalmomen.domain.repository.audio.AudioPlayer
import org.koin.core.qualifier.named
import org.koin.dsl.module

val presentationModule = module {
    factory(named("quran")) { get<AudioPlayer>() }
    single(named("radio")) { get<AudioPlayer>() }
}
