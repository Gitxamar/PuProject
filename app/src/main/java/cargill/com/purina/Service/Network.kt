package cargill.com.purina.Service

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class Network {
    companion object{
        fun isAvailable(context: Context): Boolean{
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var activeNetworkInfo: NetworkInfo? = null
            activeNetworkInfo = cm.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
        }
    }
}