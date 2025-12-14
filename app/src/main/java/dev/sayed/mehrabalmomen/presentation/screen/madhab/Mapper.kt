package dev.sayed.mehrabalmomen.presentation.screen.madhab

import dev.sayed.mehrabalmomen.domain.entity.Madhab

fun MadhabUiState.MadhabState.toDomain():
       Madhab {
    return when (this) {
        MadhabUiState.MadhabState.SHAFI -> Madhab.SHAFI
        MadhabUiState.MadhabState.HANAFI -> Madhab.HANAFI
    }
}