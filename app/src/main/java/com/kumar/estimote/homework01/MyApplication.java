package com.kumar.estimote.homework01;

import android.app.Application;

import com.estimote.sdk.BeaconManager;
import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by tarun on 10/4/2015.
 */
public class MyApplication extends Application{
    private BeaconManager beaconManager;

    @Override
    public void onCreate() {
        super.onCreate();
        beaconManager = new BeaconManager(getApplicationContext());
        Parse.initialize(this, "mBn5ZTL38GuillbOaSN8EXlTnBlqopMWEQYESnI9", "RQ7N7QCOLiKIrio82wknTMmIZjnKx7HXiTEjSd1e");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

}
