package com.android.app.slides.tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by francisco on 9/10/15.
 */
public class Configurations {

    //Location Configuration

    public static void saveLocationMode(int locationMode, Context context) {
        String locationPref = "LocationMode";
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(locationPref, locationMode);
        editor.commit();
    }


    public static int loadLocationMode(Context context) {
        String locationPref = "LocationMode";
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_NAME, Activity.MODE_PRIVATE);
        return prefs.getInt(locationPref, 1);
    }

    public static int getLocationModeId(Context context){
        int mode = loadLocationMode(context);

        switch (mode){
            case Constants.SERVICE_MODE_FOREVER:
                return 0;
            case Constants.SERVICE_MODE_ONCE:
                return 1;
        }

        return 1;
    }
}
