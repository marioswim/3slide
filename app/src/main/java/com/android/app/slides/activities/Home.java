package com.android.app.slides.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.app.slides.R;
import com.android.app.slides.adapters.OptionsAdapter;
import com.android.app.slides.model.DAOSector;
import com.android.app.slides.model.DAOUser;
import com.android.app.slides.model.Sector;
import com.android.app.slides.model.User;
import com.android.app.slides.model.VolleySingleton;
import com.android.app.slides.services.LocationService;
import com.android.app.slides.tools.Constants;
import com.android.app.slides.tools.ToastManager;
import com.android.app.slides.tools.Utilities;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by francisco on 26/9/15.
 */
public class Home extends BaseActivity {
    @Bind(R.id.optionList)
    RecyclerView mRecyclerView;
    @Bind(R.id.settings)
    IconicsImageView settings;

    private OptionsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public static String TAG = "Home - Sectors";

    @Override
    protected int getLayoutResource() {
        return R.layout.home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

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
                        //PRESENTACION
                        break;

                    case 1:

                        //BUSQUEDA
                        intent = new Intent(Home.this, Search.class);
                        startActivity(intent);
                        break;

                    case 2:
                        //CONTACTOS
                        break;

                    case 3:
                        //CHAT
                        break;

                    case 4:
                        //GRUPOS
                        break;

                    case 5:
                        //CONFIGURACION
                        intent = new Intent(Home.this, Preferences.class);
                        startActivity(intent);
                        break;
                }
            }
        });


        mRecyclerView.setAdapter(mAdapter);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, UserDetail.class);
                intent.putExtra("editMode", Constants.USER_EDIT_MODE);
                startActivity(intent);
            }
        });
        
        if(!Utilities.isMyServiceRunning(getApplicationContext())){
            initializeLocation();
        }

        checkSectors();

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
        titles.add("PRESENTACIÓN");
        titles.add("BÚSQUEDA");
        titles.add("CONTACTOS");
        titles.add("CHAT");
        titles.add("GRUPOS");
        titles.add("CONFIGURACIÓN");

        return titles;
    }

    private List<Integer> initializeImages(){
        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.ic_botn_presentacin_no_text);
        images.add(R.drawable.ic_botn_busqueda_no_text);
        images.add(R.drawable.ic_botn_contactos_no_text);
        images.add(R.drawable.ic_botn_mensajes_no_text);
        images.add(R.drawable.ic_botn_grupos_no_text);
        images.add(R.mipmap.ic_launcher);

        return images;
    }

    private void initializeLocation(){
        Intent locationService = new Intent(this, LocationService.class);
        startService(locationService);
    }

    private void welcomeBackMsg(){
        if(getIntent().getExtras()!=null){
            if(getIntent().getExtras().getBoolean("firstTime", false)){
                DAOUser daoUser = new DAOUser(getApplicationContext());
                User user = daoUser.loadUser();

                ToastManager.showToast(Home.this, "Bienvenido " + user.getName());
            }
        }

    }

    public void checkSectors(){
        DAOSector daoSector = new DAOSector(Home.this);
        if(daoSector.loadSectors().isEmpty()){
            sectorsServer();
        }
    }

    private void sectorsServer() {

        StringRequest request = new StringRequest(Request.Method.GET, Constants.baseUrl + Constants.sectorsURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            if(jsonResponse != null){
                                DAOSector daoSector = new DAOSector(Home.this);
                                daoSector.saveSectors(parseSectors(jsonResponse));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );
        // Añadir petición a la cola
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private ArrayList<Sector> parseSectors(JSONObject result){

        ArrayList<Sector> sectors = new ArrayList<>();

        try {

            JSONArray sectorsArray = result.getJSONArray("sectors");

            for (int i =0; i < sectorsArray.length(); i++){
                JSONObject index = sectorsArray.getJSONObject(i);

                Sector sector = new Sector(index.getInt("id_sec"), index.getString("nombre"));

                sectors.add(sector);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error de parsing: " + e.getMessage());
        }

        return sectors;
    }
}
