package dev.sayed.mehrabalmomen.data.recitation.engine

data class TranscriptionResult(
    val text: String,
    val isFinal: Boolean,
    val startTime: Long,
    val tokens: List<String>,
    val timestamps: List<Double>,
    val streamEpoch: Int
)
