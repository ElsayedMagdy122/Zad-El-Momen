package dev.sayed.mehrabalmomen.domain.repository.platform

interface WidgetPinProvider {
    fun requestPinWidget(onShowToast: (title: String, message: String) -> Unit)
}
