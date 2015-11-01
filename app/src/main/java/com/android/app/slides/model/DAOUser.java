package com.android.app.slides.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.app.slides.tools.Constants;
import com.android.app.slides.tools.SlidesApp;

/**
 * Created by francisco on 9/10/15.
 */
public class DAOUser {

    Context context;

    public DAOUser(Context context){
        this.context = context;
    }

    public void saveUser(User user){

        //lo guardamos en la sesion
        SlidesApp app = new SlidesApp();
        app.setUser(user);

        //lo guardamos en shared preferences
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("name", user.getName());
        if(user.getSector()!=null) {
            editor.putInt("id_sector", user.getSector().getId());
            editor.putString("sector", user.getSector().getName());
        }
        editor.putString("phone", user.getPhone());
        editor.putString("email", user.getEmail());
        editor.putString("website", user.getWebsite());
        editor.putString("description", user.getDescription());
        editor.putString("image", user.getImage_url());
        editor.putString("pdf", user.getPdf_url());
        editor.putString("apikey", user.getApikey());
        editor.commit();
    }

    public User loadUser(){
        SlidesApp app = new SlidesApp();
        User u = app.getUser();
        if(u!=null){
            return u;
        }else{
            SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
            String name = settings.getString("name", null);
            if(name!=null){
                String sector = settings.getString("sector", "");
                int id_sector = settings.getInt("id_sector", -1);
                String phone = settings.getString("phone", "");
                String email = settings.getString("email", "");
                String website = settings.getString("website", "");
                String description = settings.getString("description", "");
                String image = settings.getString("image", "");
                String pdf = settings.getString("pdf", "");
                String apikey = settings.getString("apikey", "");
                u = new User(name, new Sector(id_sector, sector), phone, website, email, description, image, apikey, pdf);
                return u;
            }
        }
        return null;
    }

    public void eliminarUsuario(){
        SlidesApp app = new SlidesApp();
        User u = app.getUser();
        if(u!=null){
            app.setUser(null);
        }
        //Eliminar de SharedPreferences
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        String name = settings.getString("name", null);
        if(name!=null){
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.commit();
        }
    }
}
