package com.android.app.slides.activities;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by francisco on 30/9/15.
 */
public class Location {

    LocationManager locationManager;
    float lat = 0;
    float lon = 0;
    Context context;
    LocationListener locationListenerGPS;
    LocationListener locationListenerNetwork;


    public void prueba(final Context context){
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        locationListenerGPS = new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onLocationChanged(android.location.Location location) {
                // TODO Auto-generated method stub
                actualizaPosicion(location, context);
            }
        };


        locationListenerNetwork = new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onLocationChanged(android.location.Location location) {
                // TODO Auto-generated method stub
                actualizaPosicion(location, context);
            }
        };

        boolean nada = false;

        try {
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                Toast.makeText(context, "No tiene la ubicaci�n por GPS conectada", Toast.LENGTH_LONG).show();
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, locationListenerGPS);
                nada=true;
            }

            if(!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                Toast.makeText(context, "No tiene la ubicaci�n por internet conectada", Toast.LENGTH_LONG).show();
            }else{
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 100, locationListenerNetwork);
                nada=true;
            }
        }catch (SecurityException e){

        }

        if(!nada){
            Toast.makeText(context, "Ubicacion no conectada, contectela e intente de nuevo.", Toast.LENGTH_LONG).show();
        }
    }


    public void actualizaPosicion(android.location.Location location, Context context){
        try{
            lat = (float) location.getLatitude();
            lon = (float) location.getLongitude();
            Toast.makeText(context, ""+lat+lon, Toast.LENGTH_LONG).show();

            if((lat!=0)&&(lon!=0)){
                locationManager.removeUpdates(locationListenerGPS);
                locationManager.removeUpdates(locationListenerNetwork);
            }
        }catch(Exception e){

        }
    }
}
