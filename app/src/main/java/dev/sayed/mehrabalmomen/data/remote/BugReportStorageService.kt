package dev.sayed.mehrabalmomen.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage

class BugReportStorageService(
    private val supabase: SupabaseClient
) {

    suspend fun uploadImage(
        fileName: String,
        bytes: ByteArray
    ): String {
        val bucket = supabase.storage.from(BUCKET_NAME)
        val path = "$PATH$fileName"

        bucket.upload(path, bytes)
        return bucket.publicUrl(path)
    }

    private companion object {
        const val BUCKET_NAME = "report_images"
        const val PATH = "images/"

    }
}