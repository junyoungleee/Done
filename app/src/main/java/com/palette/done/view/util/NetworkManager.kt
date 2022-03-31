package com.palette.done.view.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.palette.done.R
import com.palette.done.view.decoration.DoneToast

class NetworkManager {
    // 셀룰러, WIFI 연결 상태 확인
    companion object {
        fun checkNetworkState(context: Context): Boolean {
            val connectivityManager: ConnectivityManager = context.getSystemService(ConnectivityManager::class.java)
            val network: Network = connectivityManager.activeNetwork ?: return false
            val actNetwork: NetworkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                else -> false
            }
        }

        fun showRequireNetworkToast(context: Context) {
            DoneToast.createToast(context, text = context.resources.getString(R.string.network_error))?.show()
        }
    }
}