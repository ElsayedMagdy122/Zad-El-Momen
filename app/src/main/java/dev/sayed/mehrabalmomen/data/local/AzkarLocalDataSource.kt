package dev.sayed.mehrabalmomen.data.local

import android.content.Context
import dev.sayed.mehrabalmomen.data.local.dto.AzkarItemDto
import kotlinx.serialization.json.Json


class AzkarLocalDataSource(
    private val context: Context,
    private val json: Json
) {
    fun getAzkar(): Map<String, List<AzkarItemDto>> {
        return try {
            val jsonString = context.assets
                .open("azkar.json")
                .bufferedReader()
                .use { it.readText() }

            json.decodeFromString<Map<String, List<AzkarItemDto>>>(jsonString)
        } catch (e: Exception) {
            emptyMap()
        }
    }
}