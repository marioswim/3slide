package com.android.app.slides.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.app.slides.tools.Constants;
import com.android.app.slides.tools.SlidesApp;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by francisco on 1/11/15.
 */
public class DAOSector {

    Context context;

    public DAOSector(Context context){
        this.context = context;
    }

    public void saveSectors(ArrayList<Sector> sectors){

        //lo guardamos en la sesion
        SlidesApp app = new SlidesApp();
        app.setSectors(sectors);

        //lo guardamos en shared preferences
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_SECTORS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        for (int i=0; i<sectors.size();i++){
            editor.putInt("sectorID"+i, sectors.get(i).getId());
            editor.putString("sectorNAME"+i, sectors.get(i).getName());
        }
        editor.putInt("sectorCount", sectors.size());
        editor.commit();
    }

    public ArrayList<Sector> loadSectors(){

        SlidesApp app = new SlidesApp();
        ArrayList<Sector> sectors = app.getSectors();

        if(sectors!=null){
            return sectors;
        }else{
            sectors = new ArrayList<>();
            SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_SECTORS_NAME, 0);
            int size = settings.getInt("sectorCount", 0);
            for (int i = 0; i < size; i++){
                int id = settings.getInt("sectorID"+i, -1);
                String sectorName = settings.getString("sectorNAME"+i, "");

                sectors.add(new Sector(id, sectorName));
            }

            return sectors;
        }
    }
}
