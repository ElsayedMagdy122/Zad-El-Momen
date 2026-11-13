package dev.sayed.mehrabalmomen.presentation.utils

interface WidgetHandler {
    fun updateWidgets()
    fun requestPinWidget(onFallback: () -> Unit)
}

class NoOpWidgetHandler : WidgetHandler {
    override fun updateWidgets() {}
    override fun requestPinWidget(onFallback: () -> Unit) {
        onFallback()
    }
}
