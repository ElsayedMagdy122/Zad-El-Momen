package dev.sayed.mehrabalmomen.presentation.screen.ReportBug

import org.jetbrains.compose.resources.StringResource

sealed class ReportBugEffect {
    object Success : ReportBugEffect()
    object LimitReached : ReportBugEffect()
    object InvalidInput : ReportBugEffect()
    data class Error(val message: StringResource) : ReportBugEffect()
}
