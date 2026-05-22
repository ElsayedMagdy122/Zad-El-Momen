package dev.sayed.mehrabalmomen.data.util

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth

class AnonymousAuthManager(
    private val client: SupabaseClient
) {
    suspend fun getUserId(): String {
        client.auth.awaitInitialization()

        client.auth.currentUserOrNull()?.let { return it.id }

        client.auth.signInAnonymously()

        return client.auth.currentUserOrNull()?.id
            ?: error("Anonymous sign-in failed — enable it in Supabase Dashboard → Authentication → Providers → Anonymous")
    }
}