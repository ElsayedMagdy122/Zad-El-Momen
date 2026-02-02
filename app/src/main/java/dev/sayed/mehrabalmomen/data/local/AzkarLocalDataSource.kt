package dev.sayed.mehrabalmomen.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import dev.sayed.mehrabalmomen.data.local.dto.AzkarItemDto


class AzkarLocalDataSource(
    private val context: Context,
    private val gson: Gson
) {

    fun getAzkar(): Map<String, List<AzkarItemDto>> {
        val rawMap = readRawJson()
        return rawMap.mapValues { (_, element) ->
            extractItems(element)
        }
    }

    private fun readRawJson(): Map<String, JsonElement> {
        val json = context.assets
            .open("azkar.json")
            .bufferedReader()
            .use { it.readText() }

        val type = object : TypeToken<Map<String, JsonElement>>() {}.type
        return gson.fromJson(json, type)
    }

    private fun extractItems(element: JsonElement): List<AzkarItemDto> {
        if (!element.isJsonArray) return emptyList()

        val result = mutableListOf<AzkarItemDto>()

        element.asJsonArray.forEach { item ->
            when {
                item.isJsonObject -> addItem(item.asJsonObject, result)
                item.isJsonArray -> item.asJsonArray
                    .filter { it.isJsonObject }
                    .forEach { addItem(it.asJsonObject, result) }
            }
        }
        return result
    }

    private fun addItem(json: JsonObject, list: MutableList<AzkarItemDto>) {
        if (!isValidItem(json)) return

        list.add(
            AzkarItemDto(
                content = cleanText(json["content"].asString),
                count = cleanCount(json["count"]?.asString),
                description = json["description"]?.asString?.trim().takeIf { !it.isNullOrEmpty() },
                reference = json["reference"]?.asString?.trim().takeIf { !it.isNullOrEmpty() }
            )
        )
    }

    private fun isValidItem(json: JsonObject): Boolean {
        val category = json["category"]?.asString ?: return false
        val content = json["content"]?.asString?.trim()

        return category != "stop" &&
                !content.isNullOrEmpty() &&
                content != "stop"
    }

    private fun cleanCount(value: String?): String =
        value?.trim()?.removePrefix("0").takeUnless { it.isNullOrEmpty() } ?: "1"
    private fun cleanText(raw: String): String {
        var text = raw.trim()
        text = text.replace(Regex("\\\\+"), "")
        text = text.replace(Regex("[\\n\\r\\t\\u2028\\u2029]+"), " ")
        text = text.replace(Regex("\\s{2,}"), " ")
        return text.trim()
    }
}