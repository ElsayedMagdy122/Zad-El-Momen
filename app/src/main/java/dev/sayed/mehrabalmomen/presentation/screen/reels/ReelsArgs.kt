package dev.sayed.mehrabalmomen.presentation.screen.reels

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import dev.sayed.mehrabalmomen.presentation.navigation.Route

class ReelsArgs(private val savedStateHandle: SavedStateHandle) {
    val initialReelId: Int = savedStateHandle.toRoute<Route.ReelsScreen>().initialReelId
}