package dev.sayed.mehrabalmomen.data.settings.repositiory

import android.content.Context
import dev.sayed.mehrabalmomen.data.settings.dto.DeviceBrand
import dev.sayed.mehrabalmomen.domain.repository.settings.BatteryOptimizationRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray

class BatteryOptimizationRepositoryImpl(private val context: Context, private val json: Json) :
    BatteryOptimizationRepository {
    private fun loadBrands(): List<DeviceBrand> {
        return try {
            val jsonString = context.assets.open("dont_kill_app.json")
                .bufferedReader()
                .use { it.readText() }

            val root = json.decodeFromString<JsonObject>(jsonString)
            val brandsJsonArray = root["brands"]?.jsonArray ?: return emptyList()

            brandsJsonArray.map { element ->
                json.decodeFromJsonElement<DeviceBrand>(element)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun getBrandInstructions(manufacturer: String, isArabic: Boolean): List<String> {
        val brand = loadBrands().find { it.manufacturer.equals(manufacturer, ignoreCase = true) }
        return brand?.batterySettings?.let {
            if (isArabic) it.arabic.instructions else it.english.instructions
        } ?: emptyList()
    }
}