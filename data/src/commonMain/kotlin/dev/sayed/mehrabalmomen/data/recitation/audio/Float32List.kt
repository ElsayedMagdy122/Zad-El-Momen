package dev.sayed.mehrabalmomen.data.recitation.audio

class Float32List(val size: Int, init: (Int) -> Float = { 0f }) {
    private val data = FloatArray(size, init)

    operator fun get(index: Int): Float = data[index]
    operator fun set(index: Int, value: Float) {
        data[index] = value
    }

    fun toFloatArray(): FloatArray = data

    companion object {
        fun from(array: FloatArray): Float32List {
            val list = Float32List(array.size) { 0f }
            array.copyInto(list.data, 0, 0, array.size)
            return list
        }
    }
}
