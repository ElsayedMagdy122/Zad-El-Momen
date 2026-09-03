package dev.sayed.mehrabalmomen.data.di

import io.github.jan.supabase.annotations.SupabaseInternal

@OptIn(SupabaseInternal::class)
val dataModule = listOf(
    platformModule,
    coreModule,
    localModule,
    remoteModule,
    repositoryModule
)
