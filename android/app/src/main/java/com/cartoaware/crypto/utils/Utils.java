package com.cartoaware.crypto.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.cartoaware.crypto.R;

/**
 * Created by davidhodge on 12/8/17.
 */

public class Utils {

    public static boolean isNetworkConnectionAvailable(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED);
    }

    public static boolean isOnWifi(Context mContext) {
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    public static void urlIntent(Context mContext, String url) {
        String extraVal = mContext.getString(R.string.extralink);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url + "?ref=" + extraVal + "?utm_source=" + extraVal + "?from=" + extraVal));
        mContext.startActivity(i);
    }

    public static void geoIntent(Context mContext, double lat, double lon) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        String data = String.format("geo:%s,%s", lat, lon);
        intent.setData(Uri.parse(data));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
}
