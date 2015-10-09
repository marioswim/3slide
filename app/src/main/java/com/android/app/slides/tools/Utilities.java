package com.android.app.slides.tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.android.app.slides.model.User;

/**
 * Created by francisco on 24/9/15.
 */
public class Utilities {

    public static void saveUser(Activity a, User user){

        //lo guardamos en la sesion
        SlidesApp app = new SlidesApp();
        app.setUser(user);

        //lo guardamos en shared preferences
        SharedPreferences settings = a.getSharedPreferences(Constants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("name", user.getName());
        editor.putString("sector", user.getSector());
        editor.putString("phone", user.getPhone());
        editor.putString("email", user.getEmail());
        editor.putString("website", user.getWebsite());
        editor.putString("description", user.getDescription());
        editor.putString("image", user.getImage_url());
        editor.putString("apikey", user.getApikey());
        editor.commit();
    }

    public static User loadUser(Activity a){
        SlidesApp app = new SlidesApp();
        User u = app.getUser();
        if(u!=null){
            return u;
        }else{
            SharedPreferences settings = a.getSharedPreferences(Constants.PREFS_NAME, 0);
            String name = settings.getString("name", null);
            if(name!=null){
                String sector = settings.getString("sector", "");
                String phone = settings.getString("phone", "");
                String email = settings.getString("email", "");
                String website = settings.getString("website", "");
                String description = settings.getString("description", "");
                String image = settings.getString("image", "");
                String apikey = settings.getString("apikey", "");
                u = new User(name, sector, phone, website, email, description, image, apikey);
                return u;
            }
        }
        return null;
    }

    public static void eliminarUsuario(Activity a){
        SlidesApp app = new SlidesApp();
        User u = app.getUser();
        if(u!=null){
            app.setUser(null);
        }
        //Eliminar de SharedPreferences
        SharedPreferences settings = a.getSharedPreferences(Constants.PREFS_NAME, 0);
        String name = settings.getString("name", null);
        if(name!=null){
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.commit();
        }
    }

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

    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
