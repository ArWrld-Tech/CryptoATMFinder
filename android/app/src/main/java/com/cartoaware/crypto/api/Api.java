package com.cartoaware.crypto.api;

import android.content.Context;

import com.cartoaware.crypto.utils.Constants;
import com.cartoaware.crypto.utils.NetworkUtils;
import com.cartoaware.crypto.utils.Utils;
import com.parse.FindCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.pixplicity.easyprefs.library.Prefs;

/**
 * Created by davidhodge on 12/8/17.
 */

public class Api {

    public static void fetchAllATMs(Context mContext, String iso, FindCallback<ParseObject> callback) {
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(Constants.CLASS_ATM);
        if (shouldLoadLocally(mContext, Constants.CLASS_ATM, true)) {
            parseQuery.fromLocalDatastore();
        }
        if (iso != null) {
            parseQuery.whereEqualTo(Constants.ATM_ISO, iso);
        }
        parseQuery.findInBackground(callback);
    }

    public static void fetchNearbyATMs(ParseGeoPoint parseGeoPoint, FindCallback<ParseObject> callback) {
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(Constants.CLASS_ATM);
        parseQuery.fromLocalDatastore();
        if (parseGeoPoint != null) {
            parseQuery.whereWithinMiles(Constants.ATM_LOCATION, parseGeoPoint, 200);
        }
        parseQuery.findInBackground(callback);
    }

    public static void fetchATMById(String id, FindCallback<ParseObject> callback) {
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(Constants.CLASS_ATM);
        parseQuery.fromLocalDatastore();
        parseQuery.whereEqualTo("objectId", id);
        parseQuery.findInBackground(callback);
    }

    private static boolean shouldLoadLocally(Context mContext, String modelClass, boolean willUpdate) {
        boolean loadLocally = false;
        if (!Utils.isNetworkConnectionAvailable(mContext)) {
            return true;
        }
        if (!NetworkUtils.isConnectedFast(mContext)) {
            if (Prefs.getLong("ts_" + modelClass, 0) != 0) {
                return true;
            }
        }
        if (System.currentTimeMillis() - Prefs.getLong("ts_" + modelClass, 0) > Constants.CACHE_TO) {
            if (willUpdate) {
                Prefs.putLong("ts_" + modelClass, System.currentTimeMillis());
            }
            loadLocally = false;
        } else {
            loadLocally = true;
        }
        return loadLocally;
    }

}
