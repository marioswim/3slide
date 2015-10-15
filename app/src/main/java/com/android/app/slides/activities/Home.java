package com.android.app.slides.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.app.slides.R;
import com.android.app.slides.adapters.OptionsAdapter;
import com.android.app.slides.model.DAOUser;
import com.android.app.slides.model.User;
import com.android.app.slides.services.LocationService;
import com.android.app.slides.tools.ToastManager;
import com.android.app.slides.tools.Utilities;

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
                        break;

                    case 1:
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
        
        if(!Utilities.isMyServiceRunning(getApplicationContext())){
            initializeLocation();
        }

        welcomeBackMsg();


    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    private List<String> initializeTitles(){
        List<String> titles = new ArrayList<>();
        titles.add("Búsqueda");
        titles.add("Mi cuenta");
        titles.add("Contactos");
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
        Intent locationService = new Intent(this, LocationService.class);
        startService(locationService);
    }

    private void welcomeBackMsg(){
        DAOUser daoUser = new DAOUser(getApplicationContext());
        User user = daoUser.loadUser();

        ToastManager.showToast(Home.this, "Bienvenido " + user.getName());
    }
}
