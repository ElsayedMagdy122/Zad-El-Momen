package dev.sayed.mehrabalmomen.data.di

import dev.sayed.mehrabalmomen.data.platform.audio.IosAudioPlayer
import dev.sayed.mehrabalmomen.data.platform.system.IosPermissionProvider
import dev.sayed.mehrabalmomen.domain.analytics.AnalyticsTracker
import dev.sayed.mehrabalmomen.domain.model.AlarmTask
import dev.sayed.mehrabalmomen.domain.model.ReminderNotification
import dev.sayed.mehrabalmomen.domain.repository.audio.AudioPlayer
import dev.sayed.mehrabalmomen.domain.repository.notification.NotificationScheduler
import dev.sayed.mehrabalmomen.domain.repository.platform.DeviceInfoProvider
import dev.sayed.mehrabalmomen.domain.repository.platform.PermissionProvider
import dev.sayed.mehrabalmomen.domain.repository.prayer.AlarmScheduler
import dev.sayed.mehrabalmomen.domain.utils.Logger
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<PermissionProvider> { IosPermissionProvider() }
    single<Logger> { object : Logger {
        override fun d(tag: String, message: String) = println("D/$tag: $message")
        override fun i(tag: String, message: String) = println("I/$tag: $message")
        override fun w(tag: String, message: String) = println("W/$tag: $message")
        override fun e(tag: String, message: String, throwable: Throwable?) = println("E/$tag: $message")
    }}
    single<AnalyticsTracker> { object : AnalyticsTracker {
        override fun logEvent(name: String, params: Map<String, String>) {}
        override fun logScreen(screenName: String) {}
    }}
    single<AlarmScheduler> { object : AlarmScheduler {
        override fun schedule(requestCode: Int, triggerAtMillis: Long, task: AlarmTask) {}
        override fun cancel(requestCode: Int, task: AlarmTask) {}
        override fun hasPermission(): Boolean = true
    }}
    single<NotificationScheduler> { object : NotificationScheduler {
        override fun showReminder(notification: ReminderNotification) {}
        override fun hasPermission(): Boolean = true
    }}
    factory<AudioPlayer> { IosAudioPlayer() }
    single<DeviceInfoProvider> { object : DeviceInfoProvider {
        override fun getDeviceModel(): String = "iPhone"
        override fun getOsVersion(): String = "iOS"
    }}
}
