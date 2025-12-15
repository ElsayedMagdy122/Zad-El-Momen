package dev.sayed.mehrabalmomen.domain.repository

interface NetworkConnectionRepository {
    fun isCurrentlyConnected(): Boolean
}