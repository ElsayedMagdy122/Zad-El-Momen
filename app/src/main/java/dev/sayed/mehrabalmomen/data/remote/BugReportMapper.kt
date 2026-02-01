package dev.sayed.mehrabalmomen.data.remote

import android.os.Build
import dev.sayed.mehrabalmomen.data.remote.dto.BugReportInsertDto
import dev.sayed.mehrabalmomen.domain.model.BugReportRequest
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun BugReportRequest.toInsertDto(
    deviceId: String,
    imageUrl: String?,
    dayStamp: Long
): BugReportInsertDto {
    return BugReportInsertDto(
        uid = deviceId,
        title = title,
        description = description,
        featureArea = featureArea,
        imageUrl = imageUrl,
        createdAt = Clock.System.now().toString(),
        dayStamp = dayStamp,
        deviceName = "${Build.MANUFACTURER} ${Build.MODEL}",
        device = deviceId,
        androidVersion = "${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})"
    )
}