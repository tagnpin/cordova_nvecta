package com.plugin.notifyvisitors;

import android.app.Application;
import android.util.Log;

import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaPreferences;

import com.notifyvisitors.notifyvisitors.NotifyVisitorsApplication;


public class NVApplication extends Application{
    private static final String TAG = "NotifyVisitors";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"Application-Class Register !!!!");

        try {
            // Parse config.xml
            ConfigXmlParser parser = new ConfigXmlParser();
            parser.parse(this);
            CordovaPreferences preferences = parser.getPreferences();

            // Fetch the custom preference
            String notifyvisitors_bid = preferences.getString("notifyvisitors_bid", "0");
            String notifyvisitors_bid_e = preferences.getString("notifyvisitors_bid_e", "");
            Log.d(TAG, "notifyvisitors_bid: " + notifyvisitors_bid + ", notifyvisitors_bid_e: " + notifyvisitors_bid_e);

            int brandID = 0;
            if (notifyvisitors_bid != null && !notifyvisitors_bid.isEmpty()) {
                brandID = Integer.parseInt(notifyvisitors_bid);
            }

            NotifyVisitorsApplication.register(this, brandID, notifyvisitors_bid_e);
        } catch (Exception e) {
            Log.e(TAG,"Application-Class Register Error: " + e);
        }
    }
}


