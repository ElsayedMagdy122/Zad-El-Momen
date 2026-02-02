package dev.sayed.mehrabalmomen.data.remote

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.core.net.toUri
import dev.sayed.mehrabalmomen.data.util.ImageCompressor
import dev.sayed.mehrabalmomen.domain.model.BugReportRequest
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import java.security.MessageDigest
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
            throw DailyLimitExceededException("You have reached the daily limit of $DAILY_LIMIT reports")
        }
    }

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
            fileName = "${deviceId}_${System.currentTimeMillis()}.jpg",
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

    private fun currentDayStamp(): Long =
        Instant.now()
            .atOffset(ZoneOffset.UTC)
            .toLocalDate()
            .toEpochDay()

    private companion object {
        const val DAILY_LIMIT = 5
        const val TABLE_REPORTS = "reports"
    }
}

class DailyLimitExceededException(message: String) : Exception(message)
