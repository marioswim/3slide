package com.android.app.slides.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by francisco on 5/10/15.
 */
public class LocationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Double latitude = intent.getDoubleExtra("Latitude", 0);
        Double longitude = intent.getDoubleExtra("Longitude", 0);
        String provider = intent.getStringExtra("Provider");


        Toast.makeText(context, provider + latitude + longitude, Toast.LENGTH_LONG).show();
    }

}
