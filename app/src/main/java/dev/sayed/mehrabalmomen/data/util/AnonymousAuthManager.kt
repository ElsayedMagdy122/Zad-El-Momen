package dev.sayed.mehrabalmomen.data.util

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth

class AnonymousAuthManager(
    private val client: SupabaseClient
) {

    suspend fun ensureAnonymousLogin() {

        val currentUser = client.auth.currentUserOrNull()

        if (currentUser != null) return

        client.auth.signInAnonymously()
    }

    fun getUserId(): String? {
        return client.auth.currentUserOrNull()?.id
    }
}