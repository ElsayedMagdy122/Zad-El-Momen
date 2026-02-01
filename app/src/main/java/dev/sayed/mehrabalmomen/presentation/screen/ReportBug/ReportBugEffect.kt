package dev.sayed.mehrabalmomen.presentation.screen.ReportBug

sealed class ReportBugEffect {
    object Success : ReportBugEffect()
    object LimitReached : ReportBugEffect()
    object InvalidInput : ReportBugEffect()
    data class Error(val message: Int) : ReportBugEffect()
}