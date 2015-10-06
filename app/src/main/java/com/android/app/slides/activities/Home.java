package com.android.app.slides.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.android.app.slides.R;
import com.android.app.slides.adapters.OptionsAdapter;
import com.android.app.slides.services.LocationReceiver;
import com.android.app.slides.services.LocationService;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by francisco on 26/9/15.
 */
public class Home extends BaseActivity {
    @Bind(R.id.optionList)
    RecyclerView mRecyclerView;

    private OptionsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    LocationReceiver myReceiver;
    IntentFilter intentFilter;
    Intent locationService;

    @Override
    protected int getLayoutResource() {
        return R.layout.home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new OptionsAdapter(initializeImages(), initializeTitles());
        mAdapter.SetOnItemClickListener(new OptionsAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {
                // TODO Auto-generated method stub
                Intent intent;
                switch (position) {
                    case 0:
                        //intent = new Intent(ActivityMain.this, Principal.class);
                        //startActivity(intent);
                        break;

                    case 1:
                        //intent = new Intent(ActivityMain.this, ActivityProblems.class);
                        //startActivity(intent);
                        break;

                    case 2:
                        break;

                    case 3:
                        intent = new Intent(Home.this, Preferences.class);
                        startActivity(intent);
                        break;
                }
            }
        });


        mRecyclerView.setAdapter(mAdapter);

        initializeLocation();


    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationHandler();

    }
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationHandler();
    }

    private List<String> initializeTitles(){
        List<String> titles = new ArrayList<>();
        titles.add("Búsqueda");
        titles.add("Mi cuenta");
        titles.add("Grupos");
        titles.add("Configuración");

        return titles;
    }

    private List<Integer> initializeImages(){
        List<Integer> images = new ArrayList<>();
        images.add(R.mipmap.ic_launcher);
        images.add(R.mipmap.ic_launcher);
        images.add(R.mipmap.ic_launcher);
        images.add(R.mipmap.ic_launcher);

        return images;
    }

    private void initializeLocation(){
        locationService = new Intent(this, LocationService.class);
        startService(locationService);
        myReceiver = new LocationReceiver();
        intentFilter = new IntentFilter("New Location");
    }

    private void startLocationHandler(){
        registerReceiver(myReceiver, intentFilter);
    }

    private void stopLocationHandler(){
        unregisterReceiver(myReceiver);
        stopService(locationService);
    }
}
