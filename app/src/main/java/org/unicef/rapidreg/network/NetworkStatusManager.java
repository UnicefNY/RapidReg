package org.unicef.rapidreg.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.unicef.rapidreg.PrimeroApplication;

public class NetworkStatusManager {

    public static boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) PrimeroApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }
}
