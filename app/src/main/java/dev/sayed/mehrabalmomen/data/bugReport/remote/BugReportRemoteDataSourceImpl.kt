package dev.sayed.mehrabalmomen.data.bugReport.remote

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.core.net.toUri
import dev.sayed.mehrabalmomen.data.bugReport.mapper.toInsertDto
import dev.sayed.mehrabalmomen.data.util.helpers.ImageCompressor
import dev.sayed.mehrabalmomen.domain.model.BugReportRequest
import dev.sayed.mehrabalmomen.domain.repository.platform.DeviceInfoProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.security.MessageDigest
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class BugReportRemoteDataSourceImpl(
    private val rpcService: BugReportRpcService,
    private val storageService: BugReportStorageService,
    private val deviceInfoProvider: DeviceInfoProvider,
    private val supabase: SupabaseClient,
    private val context: Context
) : BugReportRemoteDataSource {

    @OptIn(ExperimentalTime::class)
    override suspend fun submit(report: BugReportRequest) {
        val deviceId = getDeviceId()
        val dayStamp = currentDayStamp()

        enforceDailyLimit(deviceId, dayStamp)

        val imageUrl = uploadImageIfNeeded(report, deviceId)

        val dto = report.toInsertDto(
            deviceId = deviceId,
            imageUrl = imageUrl,
            dayStamp = dayStamp,
            deviceName = deviceInfoProvider.getDeviceModel(),
            osVersion = deviceInfoProvider.getOsVersion()
        )

        supabase.from(TABLE_REPORTS).insert(dto)
    }

    private suspend fun enforceDailyLimit(
        deviceId: String,
        dayStamp: Long
    ) {
        val count = rpcService.getDailyCount(deviceId, dayStamp)
        if (count >= DAILY_LIMIT) {
            throw DailyLimitExceededException("You have reached the daily limit of $DAILY_LIMIT reports")
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun uploadImageIfNeeded(
        report: BugReportRequest,
        deviceId: String
    ): String? {
        val imageUrlString = report.imageUrl ?: return null

        val uri = try {
            imageUrlString.toUri()
        } catch (e: Exception) {
            return null
        }

        val compressedBytes = try {
            ImageCompressor.compressFromUri(
                context = context,
                uri = uri,
                maxWidth = 1280,
                maxHeight = 1280,
                quality = 78
            )
        } catch (e: Exception) {
            return null
        }

        return storageService.uploadImage(
            fileName = "${deviceId}_${Clock.System.now().toEpochMilliseconds()}.jpg",
            bytes = compressedBytes
        )
    }

    @SuppressLint("HardwareIds")
    private fun getDeviceId(): String {
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: error("Cannot get device ID")
        return androidId.toSHA256()
    }

    fun String.toSHA256(): String {
        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(this.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    @OptIn(ExperimentalTime::class)
    private fun currentDayStamp(): Long =
        Clock.System.now()
            .toLocalDateTime(TimeZone.UTC)
            .date
            .toEpochDays()
            .toLong()

    private companion object {
        const val DAILY_LIMIT = 5
        const val TABLE_REPORTS = "reports"
    }
}

class DailyLimitExceededException(message: String) : Exception(message)
