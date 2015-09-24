package com.android.app.slides.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by francisco on 24/9/15.
 */
public class Utilities {

/*    public static void saveUser(Activity a, User user){

        //lo guardamos en la sesion
        RadioApp app = new RadioApp();
        app.setUser(user);

        //lo guardamos en shared preferences
        SharedPreferences settings = a.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("name", user.getName());
        editor.putString("email", user.getEmail());
        editor.putString("photo", user.getPhotoUrl());
        editor.putString("profile", user.getGooglePlusProfile());
        editor.putString("apiKey", user.getApiKey());
        editor.commit();
    }

    public static Usuario loadUser(Activity a){
        RadioApp app = new RadioApp();
        User u = app.getUser();
        if(u!=null){
            return u;
        }else{
            SharedPreferences settings = a.getSharedPreferences(PREFS_NAME, 0);
            String name = settings.getString("name", null);
            if(name!=null){
                String email = settings.getString("email", null);
                String photo = settings.getString("photo", null);
                String profile = settings.getString("profile", null);
                String apiKey = settings.getString("apiKey", null);
                boolean first = settings.getBoolean("first", false);
                u = new Usuario(name, email, photo, profile);
                u.setApiKey(apiKey);
                return u;
            }
        }
        return null;
    }*/

    public static boolean isNetworkAvailable(Activity a) {
        Context context = a.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            Toast.makeText(a.getBaseContext(), "No hay internet", Toast.LENGTH_LONG).show();
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
