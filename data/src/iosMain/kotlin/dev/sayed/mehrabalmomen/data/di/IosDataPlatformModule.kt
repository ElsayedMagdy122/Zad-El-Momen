package dev.sayed.mehrabalmomen.data.di

import dev.sayed.mehrabalmomen.data.platform.IosAlarmScheduler
import dev.sayed.mehrabalmomen.data.platform.IosAudioPlayer
import dev.sayed.mehrabalmomen.data.platform.IosBillingService
import dev.sayed.mehrabalmomen.data.platform.IosDeviceInfoProvider
import dev.sayed.mehrabalmomen.data.platform.IosNotificationScheduler
import dev.sayed.mehrabalmomen.data.platform.IosPermissionProvider
import dev.sayed.mehrabalmomen.domain.billing.BillingService
import dev.sayed.mehrabalmomen.domain.repository.audio.AudioPlayer
import dev.sayed.mehrabalmomen.domain.repository.notification.NotificationScheduler
import dev.sayed.mehrabalmomen.domain.repository.platform.DeviceInfoProvider
import dev.sayed.mehrabalmomen.domain.repository.platform.PermissionProvider
import dev.sayed.mehrabalmomen.domain.repository.prayer.AlarmScheduler
import org.koin.dsl.module

val iosDataPlatformModule = module {
    single<AudioPlayer> { IosAudioPlayer() }
    single<AlarmScheduler> { IosAlarmScheduler() }
    single<NotificationScheduler> { IosNotificationScheduler() }
    single<DeviceInfoProvider> { IosDeviceInfoProvider() }
    single<PermissionProvider> { IosPermissionProvider() }
    single<BillingService> { IosBillingService() }
}
