package dev.sayed.mehrabalmomen.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DailyReportCountDto(
    @SerialName("uid")
    val uid: String,
    @SerialName("day_stamp")
    val dayStamp: Long
)
