package dev.sayed.mehrabalmomen.data.di

import androidx.media3.common.util.UnstableApi
import com.google.gson.Gson
import dev.sayed.mehrabalmomen.data.platform.AndroidAlarmScheduler
import dev.sayed.mehrabalmomen.data.platform.AndroidNotificationScheduler
import dev.sayed.mehrabalmomen.data.platform.audio.AndroidAudioPlayer
import dev.sayed.mehrabalmomen.data.util.AndroidLogger
import dev.sayed.mehrabalmomen.data.util.BillingManager
import dev.sayed.mehrabalmomen.domain.repository.audio.AudioPlayer
import dev.sayed.mehrabalmomen.domain.repository.notification.NotificationScheduler
import dev.sayed.mehrabalmomen.domain.repository.prayer.AlarmScheduler
import dev.sayed.mehrabalmomen.domain.utils.Logger
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

@OptIn(UnstableApi::class)
val coreModule = module {
    single { Gson() }
    single { BillingManager(get()) }
    single<AlarmScheduler> { AndroidAlarmScheduler(androidContext()) }
    single<NotificationScheduler> { AndroidNotificationScheduler(androidContext()) }
    factory<AudioPlayer> { AndroidAudioPlayer(androidContext()) }
    single<Logger> { AndroidLogger() }
}
