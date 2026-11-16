package dev.sayed.mehrabalmomen.data.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.media3.common.util.UnstableApi
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.firebase.analytics.FirebaseAnalytics
import dev.sayed.mehrabalmomen.data.azkar.local.AzkarLocalDataSource
import dev.sayed.mehrabalmomen.data.bugReport.remote.BugReportRemoteDataSource
import dev.sayed.mehrabalmomen.data.bugReport.remote.BugReportRemoteDataSourceImpl
import dev.sayed.mehrabalmomen.data.location.LocationRepositoryImpl
import dev.sayed.mehrabalmomen.data.platform.AndroidAlarmScheduler
import dev.sayed.mehrabalmomen.data.platform.AndroidNotificationScheduler
import dev.sayed.mehrabalmomen.data.platform.analytics.AndroidAnalyticsTracker
import dev.sayed.mehrabalmomen.data.platform.audio.AndroidAudioPlayer
import dev.sayed.mehrabalmomen.data.platform.sensor.CompassSensorManager
import dev.sayed.mehrabalmomen.data.platform.system.AndroidDeviceInfoProvider
import dev.sayed.mehrabalmomen.data.platform.system.AndroidPermissionProvider
import dev.sayed.mehrabalmomen.data.quran.audio.repository.QuranAudioReadersRepositoryImpl
import dev.sayed.mehrabalmomen.data.recitation.engine.RecitationModelDownloader
import dev.sayed.mehrabalmomen.data.settings.repositiory.BatteryOptimizationRepositoryImpl
import dev.sayed.mehrabalmomen.data.util.AndroidLogger
import dev.sayed.mehrabalmomen.data.util.AppDatabase
import dev.sayed.mehrabalmomen.data.util.BillingManager
import dev.sayed.mehrabalmomen.data.util.network.NetworkConnectionRepositoryImpl
import dev.sayed.mehrabalmomen.domain.analytics.AnalyticsTracker
import dev.sayed.mehrabalmomen.domain.billing.BillingService
import dev.sayed.mehrabalmomen.domain.repository.audio.AudioPlayer
import dev.sayed.mehrabalmomen.domain.repository.location.LocationRepository
import dev.sayed.mehrabalmomen.domain.repository.network.NetworkConnectionRepository
import dev.sayed.mehrabalmomen.domain.repository.notification.NotificationScheduler
import dev.sayed.mehrabalmomen.domain.repository.platform.DeviceInfoProvider
import dev.sayed.mehrabalmomen.domain.repository.platform.PermissionProvider
import dev.sayed.mehrabalmomen.domain.repository.prayer.AlarmScheduler
import dev.sayed.mehrabalmomen.domain.repository.qiblah.CompassSensorManager as DomainCompassSensorManager
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranAudioReadersRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.BatteryOptimizationRepository
import dev.sayed.mehrabalmomen.domain.utils.Logger
import okio.Path.Companion.toPath
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

@OptIn(UnstableApi::class)
actual val platformDataModule: Module = module {
    single {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { androidContext().filesDir.resolve("location_prefs.preferences_pb").absolutePath.toPath() }
        )
    }
    single(named("azkar_prefs")) {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { androidContext().filesDir.resolve("azkar_prefs.preferences_pb").absolutePath.toPath() }
        )
    }
    single(named("recitation_prefs")) {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { androidContext().filesDir.resolve("recitation_prefs.preferences_pb").absolutePath.toPath() }
        )
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "mehrab_database.db"
        )
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL("""
                    INSERT INTO reminders (title, description, hour, minute, recurrence, isEnabled, reminderType)
                    VALUES 
                    ('التنبيه قبل الصلاة', 'اقترب موعد الأذان، استعد للوضوء والصلاة', 0, 15, 'DAILY', 1, 'BEFORE_PRAYER'),
                    ('أذكار الصباح', 'لا تنس أذكار الصباح والتحصين', 7, 0, 'DAILY', 1, 'MORNING_AZKAR'),
                    ('أذكار المساء', 'لا تنس أذكار المساء لحفظك ونور صدرك', 17, 0, 'DAILY', 1, 'EVENING_AZKAR'),
                    ('الورد القرآني اليومي', 'حان وقت وردك القرآني اليومي', 20, 0, 'DAILY', 1, 'DAILY_WORD'),
                    ('سنن الجمعة', 'أكثر من الصلاة على النبي واقرأ سورة الكهف', 9, 0, 'WEEKLY_THURSDAY', 1, 'FRIDAY_SUNNAN')
                """.trimIndent())
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                db.execSQL("""
                    INSERT INTO reminders (title, description, hour, minute, recurrence, isEnabled, reminderType)
                    SELECT 'التنبيه قبل الصلاة', 'اقترب موعد الأذان، استعد للوضوء والصلاة', 0, 15, 'DAILY', 1, 'BEFORE_PRAYER'
                    WHERE NOT EXISTS (SELECT 1 FROM reminders WHERE reminderType = 'BEFORE_PRAYER')
                """.trimIndent())
            }
        })
        .fallbackToDestructiveMigration(true)
        .build()
    }

    single<DomainCompassSensorManager> { CompassSensorManager(androidContext()) }
    single<AlarmScheduler> { AndroidAlarmScheduler(androidContext()) }
    single<NotificationScheduler> { AndroidNotificationScheduler(androidContext()) }
    @OptIn(UnstableApi::class)
    factory<AudioPlayer> { AndroidAudioPlayer(androidContext()) }
    single<Logger> { AndroidLogger() }
    @Suppress("MissingPermission")
    single<AnalyticsTracker> { AndroidAnalyticsTracker(FirebaseAnalytics.getInstance(androidContext())) }
    single<PermissionProvider> { AndroidPermissionProvider(androidContext()) }
    single<DeviceInfoProvider> { AndroidDeviceInfoProvider() }
    single<BillingService> { BillingManager(androidContext()) }
    single<BatteryOptimizationRepository> { BatteryOptimizationRepositoryImpl(androidContext(), get()) }

    single<NetworkConnectionRepository> { NetworkConnectionRepositoryImpl(androidContext()) }
    single<LocationRepository> { LocationRepositoryImpl(androidContext(), get()) }
    single<BugReportRemoteDataSource> {
        BugReportRemoteDataSourceImpl(
            rpcService = get(),
            storageService = get(),
            deviceInfoProvider = get(),
            supabase = get(),
            context = androidContext()
        )
    }
    single<AzkarLocalDataSource> {
        val context = androidContext()
        AzkarLocalDataSource(
            azkarDao = get(),
            jsonProvider = {
                context.assets.open("azkar.json").bufferedReader().use { it.readText() }
            }
        )
    }
    single<QuranAudioReadersRepository> {
        QuranAudioReadersRepositoryImpl(
            readersRemoteDataSource = get(),
            downloadedReciterDao = get(),
            recitationPreferences = get(),
            context = androidContext()
        )
    }
    single {
        RecitationModelDownloader(
            context = androidContext(),
            supabase = get(),
            httpClient = get()
        )
    }
}
