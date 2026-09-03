package dev.sayed.mehrabalmomen.data.bugReport.mapper

import dev.sayed.mehrabalmomen.data.bugReport.remote.dto.BugReportInsertDto
import dev.sayed.mehrabalmomen.domain.model.BugReportRequest
import kotlinx.datetime.Clock

/**
 * Maps domain [BugReportRequest] to data [BugReportInsertDto].
 */
fun BugReportRequest.toInsertDto(
    deviceId: String,
    imageUrl: String?,
    dayStamp: Long,
    deviceName: String,
    osVersion: String
): BugReportInsertDto {
    return BugReportInsertDto(
        uid = deviceId,
        title = title,
        description = description,
        featureArea = featureArea,
        imageUrl = imageUrl,
        createdAt = Clock.System.now().toString(),
        dayStamp = dayStamp,
        deviceName = deviceName,
        androidVersion = osVersion
    )
}
