package com.kumar.estimote.homework01;

import android.app.Application;

import com.estimote.sdk.BeaconManager;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by tarun on 10/4/2015.
 */
public class MyApplication extends Application{
    private BeaconManager beaconManager;
    private Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        beaconManager = new BeaconManager(getApplicationContext());
        Parse.initialize(this, "mBn5ZTL38GuillbOaSN8EXlTnBlqopMWEQYESnI9", "RQ7N7QCOLiKIrio82wknTMmIZjnKx7HXiTEjSd1e");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

}
