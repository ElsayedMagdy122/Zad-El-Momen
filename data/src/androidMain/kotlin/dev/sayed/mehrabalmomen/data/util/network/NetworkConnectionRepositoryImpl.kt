package dev.sayed.mehrabalmomen.data.util.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dev.sayed.mehrabalmomen.domain.repository.network.NetworkConnectionRepository

class NetworkConnectionRepositoryImpl(
    context: Context
) : NetworkConnectionRepository {
    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)

    @SuppressLint("MissingPermission")
    override fun isCurrentlyConnected(): Boolean {
        val network = connectivityManager?.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
