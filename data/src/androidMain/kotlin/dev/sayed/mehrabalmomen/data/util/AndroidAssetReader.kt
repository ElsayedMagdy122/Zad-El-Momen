package dev.sayed.mehrabalmomen.data.util

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidAssetReader(
    private val context: Context
) : AssetReader {
    override suspend fun readText(fileName: String): String = withContext(Dispatchers.IO) {
        context.assets.open(fileName).bufferedReader().use { it.readText() }
    }
}
