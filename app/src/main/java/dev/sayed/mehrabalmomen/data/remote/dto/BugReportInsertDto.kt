package dev.sayed.mehrabalmomen.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BugReportInsertDto(
    @SerialName("uid")
    val uid: String,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("feature_area")
    val featureArea: String? = null,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("day_stamp")
    val dayStamp: Long,
    @SerialName("device_name")
    val deviceName: String,
    @SerialName("android_version")
    val androidVersion: String
)
