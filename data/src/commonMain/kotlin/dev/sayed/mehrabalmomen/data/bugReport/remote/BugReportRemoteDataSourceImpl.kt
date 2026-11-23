package dev.sayed.mehrabalmomen.data.bugReport.remote

import dev.sayed.mehrabalmomen.data.bugReport.mapper.toInsertDto
import dev.sayed.mehrabalmomen.domain.model.BugReportRequest
import dev.sayed.mehrabalmomen.domain.repository.platform.DeviceInfoProvider
import dev.sayed.mehrabalmomen.domain.repository.platform.ImageCompressor
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
    private val imageCompressor: ImageCompressor,
    private val supabase: SupabaseClient
) : BugReportRemoteDataSource {


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
        val compressedBytes = imageCompressor.compressFromUri(imageUrlString) ?: return null

        return storageService.uploadImage(
            fileName = "${deviceId}_${Clock.System.now().toEpochMilliseconds()}.jpg",
            bytes = compressedBytes
        )
    }

    private fun currentDayStamp(): Long =
        Clock.System.now()
            .toLocalDateTime(TimeZone.UTC)
            .date
            .toEpochDays()

    private companion object {
        const val DAILY_LIMIT = 5
        const val TABLE_REPORTS = "reports"
    }
}

class DailyLimitExceededException(message: String) : Exception(message)
