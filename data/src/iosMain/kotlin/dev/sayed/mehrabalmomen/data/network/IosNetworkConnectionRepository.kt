package dev.sayed.mehrabalmomen.data.network

import dev.sayed.mehrabalmomen.domain.repository.network.NetworkConnectionRepository

class IosNetworkConnectionRepository : NetworkConnectionRepository {
    override fun isCurrentlyConnected(): Boolean {
        // Basic dummy implementation
        return true
    }
}
