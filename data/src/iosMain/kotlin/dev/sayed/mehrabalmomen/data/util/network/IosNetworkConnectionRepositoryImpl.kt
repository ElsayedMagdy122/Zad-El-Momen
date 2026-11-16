package dev.sayed.mehrabalmomen.data.util.network

import dev.sayed.mehrabalmomen.domain.repository.network.NetworkConnectionRepository

class IosNetworkConnectionRepositoryImpl : NetworkConnectionRepository {
    override fun isCurrentlyConnected(): Boolean {
        return true
    }
}
