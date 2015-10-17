package com.android.app.slides.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.app.slides.activities.Home;
import com.android.app.slides.model.DAOUser;
import com.android.app.slides.model.User;
import com.android.app.slides.tools.Configurations;
import com.android.app.slides.tools.Constants;
import com.android.app.slides.tools.ToastManager;
import com.android.app.slides.tools.Utilities;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by francisco on 4/10/15.
 */
public class LocationService extends Service
{
    private static final int THIRTY_MINUTES = 1000 * 60;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;

    Intent intent;
    public static int mode;


    public static IntentFilter intentFilter;
    public static LocationReceiver myReceiver;

    @Override
    public void onCreate()
    {
        super.onCreate();

        mode = Configurations.loadLocationMode(this);

        myReceiver = new LocationReceiver();
        if(mode == Constants.SERVICE_MODE_ONCE){
            intent = new Intent(Constants.BROADCAST_ACTION_LOCATION_ONCE);
            intentFilter = new IntentFilter(Constants.BROADCAST_ACTION_LOCATION_ONCE);
        }else if (mode == Constants.SERVICE_MODE_FOREVER){
            intent = new Intent(Constants.BROADCAST_ACTION_LOCATION_FOREVER);
            intentFilter = new IntentFilter(Constants.BROADCAST_ACTION_LOCATION_FOREVER);
        }

        registerReceiver(myReceiver, intentFilter);
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        boolean go = false;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener(this, mode);

        try{
            if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, listener);
                go = true;
            }else{
                listener.onProviderDisabled("Network GPS");
            }
            if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, listener);
                go = true;
            }else{
                listener.onProviderDisabled("GPS");
            }

            if(!go){
                this.stopSelf();
            }
        }catch (SecurityException e){

        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > THIRTY_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -THIRTY_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }



    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }



    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        try{
            locationManager.removeUpdates(listener);
            unregisterReceiver(myReceiver);
        }catch (SecurityException e){

        }
    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }

    public static void reInitializeBroadcastReceiver(Context context){
        myReceiver = new LocationReceiver();
        context.registerReceiver(myReceiver, intentFilter);
    }




    public class MyLocationListener implements LocationListener
    {
        Service context;
        int mode;

        public MyLocationListener(Service context, int mode){
            this.context = context;
            this.mode = mode;
        }

        public void onLocationChanged(final Location loc)
        {
            if(isBetterLocation(loc, previousBestLocation)) {
                loc.getLatitude();
                loc.getLongitude();
                intent.putExtra("Latitude", loc.getLatitude());
                intent.putExtra("Longitude", loc.getLongitude());
                intent.putExtra("Provider", loc.getProvider());
                sendBroadcast(intent);

                if (mode == Constants.SERVICE_MODE_ONCE){
                    context.stopSelf();
                }
            }
        }

        public void onProviderDisabled(String provider)
        {
            Toast.makeText(getApplicationContext(), provider + " Disabled", Toast.LENGTH_SHORT).show();
        }


        public void onProviderEnabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }


        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }

    }

    public static class LocationReceiver extends BroadcastReceiver {

        private RequestQueue requestQueue;
        Double latitude, longitude;

        @Override
        public void onReceive(Context context, Intent intent) {

            latitude = intent.getDoubleExtra("Latitude", 0);
            longitude = intent.getDoubleExtra("Longitude", 0);
            String provider = intent.getStringExtra("Provider");

            requestQueue = Volley.newRequestQueue(context);

            updateLocationServer(context);

            Toast.makeText(context, provider + latitude + longitude, Toast.LENGTH_LONG).show();

            if (mode == Constants.SERVICE_MODE_ONCE){
                Intent explicitIntent = new Intent(context, LocationService.class);
                context.stopService(explicitIntent);
            }else if (mode == Constants.SERVICE_MODE_FOREVER){
                reInitializeBroadcastReceiver(context);
            }
        }

        private void updateLocationServer(final Context context){

            StringRequest request = new StringRequest(Request.Method.POST, Constants.baseUrl + Constants.updateLocationURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    // the POST parameters:
                    // TODO: 17/10/15 Mandar el apikey si finalmente es necesario
                    DAOUser daoUser = new DAOUser(context);
                    User user = daoUser.loadUser();

                    params.put("latitud", latitude.toString());
                    params.put("longitude", longitude.toString());
                    return params;
                }
            };

            // Añadir petición a la cola
            requestQueue.add(request);

        }

    }
}