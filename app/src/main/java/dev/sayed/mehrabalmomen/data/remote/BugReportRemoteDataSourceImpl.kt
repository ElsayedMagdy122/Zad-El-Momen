package dev.sayed.mehrabalmomen.data.remote

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.core.net.toUri
import dev.sayed.mehrabalmomen.domain.model.BugReportRequest
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import java.time.Instant
import java.time.ZoneOffset

class BugReportRemoteDataSourceImpl(
    private val rpcService: BugReportRpcService,
    private val storageService: BugReportStorageService,
    private val supabase: SupabaseClient,
    private val context: Context
) : BugReportRemoteDataSource {

    override suspend fun submit(report: BugReportRequest) {
        val deviceId = getDeviceId()
        val dayStamp = currentDayStamp()

        enforceDailyLimit(deviceId, dayStamp)

        val imageUrl = uploadImageIfNeeded(report, deviceId)

        val dto = report.toInsertDto(
            deviceId = deviceId,
            imageUrl = imageUrl,
            dayStamp = dayStamp
        )

        supabase.from(TABLE_REPORTS).insert(dto)
    }

    private suspend fun enforceDailyLimit(
        deviceId: String,
        dayStamp: Long
    ) {
        val count = rpcService.getDailyCount(deviceId, dayStamp)
        if (count >= DAILY_LIMIT) {
            throw Exception("You have reached the daily limit of $DAILY_LIMIT reports")
        }
    }

    private suspend fun uploadImageIfNeeded(
        report: BugReportRequest,
        deviceId: String
    ): String? {
        val uri = report.imageUrl ?: return null
        val bytes = context.contentResolver
            .openInputStream(uri.toUri())
            ?.readBytes()
            ?: error("Cannot read image bytes")

        return storageService.uploadImage(
            fileName = "${deviceId}_${System.currentTimeMillis()}.png",
            bytes = bytes
        )
    }

    @SuppressLint("HardwareIds")
    private fun getDeviceId(): String =
        Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: error("Cannot get device ID")

    private fun currentDayStamp(): Long =
        Instant.now()
            .atOffset(ZoneOffset.UTC)
            .toLocalDate()
            .toEpochDay()

    private companion object {
        const val DAILY_LIMIT = 80
        const val TABLE_REPORTS = "reports"
    }
}
