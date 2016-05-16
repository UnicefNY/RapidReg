package org.unicef.rapidreg.network;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStatusManager {

    public static boolean isOnline(ConnectivityManager connectivityManager) {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }
}
