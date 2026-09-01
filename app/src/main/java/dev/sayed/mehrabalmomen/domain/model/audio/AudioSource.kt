package dev.sayed.mehrabalmomen.domain.model.audio

/**
 * Defines the origin of an audio stream or file to be played.
 */
sealed class AudioSource {
    /** A remote network stream or file URL. */
    data class RemoteUrl(val url: String) : AudioSource()

    /** 
     * A local file stored in the device storage (e.g., downloaded Quran surahs).
     * @param path The absolute path to the file.
     */
    data class LocalFile(val path: String) : AudioSource()

    /**
     * A local platform-specific resource identifier (Raw resource).
     * @param name The semantic name of the resource (e.g., "azan_makkah").
     */
    data class LocalResource(val name: String) : AudioSource()

    companion object {
        /**
         * Helper to create an [AudioSource] from a string path or URL.
         */
        fun fromPath(path: String): AudioSource {
            return when {
                path.startsWith("http") -> RemoteUrl(path)
                path.startsWith("file://") -> LocalFile(path.removePrefix("file://"))
                else -> LocalFile(path)
            }
        }
    }
}
