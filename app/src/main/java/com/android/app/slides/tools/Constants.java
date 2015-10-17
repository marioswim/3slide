package com.android.app.slides.tools;

/**
 * Created by francisco on 26/9/15.
 */
public class Constants {

    public static String baseUrl = "http://triunfalia.com";
    public static String loginURL = "/login";
    public static String registerURL = "/signup";
    public static String updateProfileURL = "/updateProfile";
    public static String updateLocationURL = "/updateLocation";

    public static String PREFS_NAME = "3slidesPrefs";

    public static final int SERVICE_MODE_ONCE = 1;
    public static final int SERVICE_MODE_FOREVER = 2;

    public static final String BROADCAST_ACTION_LOCATION_FOREVER = "Location Forever";
    public static final String BROADCAST_ACTION_LOCATION_ONCE = "Location Once";

    public static final int USER_VIEW_MODE = 0;
    public static final int USER_EDIT_MODE = 1;
}