package dev.sayed.mehrabalmomen.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dev.sayed.mehrabalmomen.domain.repository.NetworkConnectionRepository

class NetworkConnectionRepositoryImpl(private val context: Context) : NetworkConnectionRepository {
    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
    override fun isCurrentlyConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}