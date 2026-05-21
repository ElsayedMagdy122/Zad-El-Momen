package dev.sayed.mehrabalmomen.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.data.util.AnonymousAuthManager
import kotlinx.coroutines.launch

class AnonymousAuthViewModel (
    private val authManager: AnonymousAuthManager
) : ViewModel() {

    init {
        viewModelScope.launch {
            authManager.ensureAnonymousLogin()
        }
    }
}