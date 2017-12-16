package com.cartoaware.crypto;

import android.app.Application;

import com.mapbox.mapboxsdk.Mapbox;
import com.parse.Parse;
import com.pixplicity.easyprefs.library.Prefs;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by davidhodge on 12/8/17.
 */

public class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.Configuration.Builder configuration = new Parse.Configuration.Builder(getApplicationContext())
                .applicationId(getString(R.string.app_id))
                .clientKey(getString(R.string.client_key))
                .server(getString(R.string.server))
                .enableLocalDataStore();
        Parse.initialize(configuration.build());

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.font))
                .setFontAttrId(R.attr.fontPath)
                .build());

        Mapbox.getInstance(getApplicationContext(), getString(R.string.access_token));

        new Prefs.Builder()
                .setContext(this)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }
}
