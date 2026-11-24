package dev.sayed.mehrabalmomen.presentation.screen.ReportBug

import dev.sayed.mehrabalmomen.presentation.base.UiText

sealed class ReportBugEffect {
    object Success : ReportBugEffect()
    object LimitReached : ReportBugEffect()
    object InvalidInput : ReportBugEffect()
    data class Error(val message: UiText) : ReportBugEffect()
}
