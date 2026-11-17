package dev.sayed.mehrabalmomen.data.platform

import dev.sayed.mehrabalmomen.domain.repository.platform.WidgetPinProvider

class IosWidgetPinProvider : WidgetPinProvider {
    override fun requestPinWidget(onShowToast: (title: String, message: String) -> Unit) {
        onShowToast(
            "ويدجت مواقيت الصلاة",
            "يمكنك إضافة الويدجت مباشرة من شاشة الآيفون الرئيسية."
        )
    }
}
