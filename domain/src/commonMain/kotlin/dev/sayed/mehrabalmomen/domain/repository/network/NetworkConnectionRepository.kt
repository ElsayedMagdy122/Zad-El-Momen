package dev.sayed.mehrabalmomen.domain.repository.network

interface NetworkConnectionRepository {
    fun isCurrentlyConnected(): Boolean
}