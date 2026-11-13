package dev.sayed.mehrabalmomen.data.bugReport.remote

import dev.sayed.mehrabalmomen.data.bugReport.mapper.toInsertDto
import dev.sayed.mehrabalmomen.data.util.helpers.ImageCompressor
import dev.sayed.mehrabalmomen.domain.model.BugReportRequest
import dev.sayed.mehrabalmomen.domain.repository.platform.DeviceInfoProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class BugReportRemoteDataSourceImpl(
    private val rpcService: BugReportRpcService,
    private val storageService: BugReportStorageService,
    private val deviceInfoProvider: DeviceInfoProvider,
    private val supabase: SupabaseClient,
    private val imageCompressor: ImageCompressor
) : BugReportRemoteDataSource {

    @OptIn(ExperimentalTime::class)
    override suspend fun submit(report: BugReportRequest) {
        val deviceId = deviceInfoProvider.getDeviceId()
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

        val compressedBytes = try {
            imageCompressor.compressFromUri(
                uriString = imageUrlString,
                maxWidth = 1280,
                maxHeight = 1280,
                quality = 78
            ) ?: return null
        } catch (e: Exception) {
            return null
        }

        return storageService.uploadImage(
            fileName = "${deviceId}_${Clock.System.now().toEpochMilliseconds()}.jpg",
            bytes = compressedBytes
        )
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

typealias DailyLimitExceededException = dev.sayed.mehrabalmomen.domain.exceptions.AppException.DailyLimitExceededException
