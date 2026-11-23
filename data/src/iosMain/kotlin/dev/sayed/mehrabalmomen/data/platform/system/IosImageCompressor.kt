package dev.sayed.mehrabalmomen.data.platform.system

import dev.sayed.mehrabalmomen.domain.repository.platform.ImageCompressor
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.dataWithContentsOfURL
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
class IosImageCompressor : ImageCompressor {
    override suspend fun compressFromUri(uriString: String): ByteArray? {
        return try {
            val data = if (uriString.startsWith("file://") || uriString.startsWith("http")) {
                val url = NSURL.URLWithString(uriString) ?: return null
                NSData.dataWithContentsOfURL(url)
            } else {
                NSData.dataWithContentsOfFile(uriString)
            } ?: return null

            val uiImage = UIImage.imageWithData(data) ?: return null
            val jpegData = UIImageJPEGRepresentation(uiImage, 0.78) ?: data
            val length = jpegData.length.toInt()
            if (length <= 0) return null

            val bytes = ByteArray(length)
            memcpy(bytes.refTo(0), jpegData.bytes, jpegData.length)
            bytes
        } catch (_: Exception) {
            null
        }
    }
}
