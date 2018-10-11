package com.hazem.utilslib.libs.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * Connectivity Class
 * <p>
 * Helper Class Contains Connectivity Methods
 * <p>
 * Version 1.0
 * <p>
 * Updated Version --
 */

public class Connectivity {

    private static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * Check if There Is Network Connection Available
     *
     * @param context context only needed
     * @return true if there is network available other wise return false
     */
    public static boolean isConnected(Context context) {
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        return (info != null && info.isConnectedOrConnecting());
    }


}