package com.android.app.slides.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mikepenz.iconics.view.IconicsImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by francisco on 26/9/15.
 */
public class Home extends BaseActivity {
    @Bind(R.id.optionList)
    RecyclerView mRecyclerView;
    @Bind(R.id.settings)
    IconicsImageView settings;
    @Bind(R.id.profile_image)
    CircleImageView profile_image;
    @Bind(R.id.userName)
    TextView userName;
    @Bind(R.id.userSector)
    TextView userSector;

    User user;
    private OptionsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public static String TAG = "Home - Sectors";

    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapterMenu;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    @Override
    protected int getLayoutResource() {
        return R.layout.home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

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
                        intent = new Intent(Home.this,Presentation.class);
                        startActivity(intent);
                        break;

                    case 1:

                        //BUSQUEDA
                        intent = new Intent(Home.this, Search.class);
                        startActivity(intent);
                        break;

                    case 2:
                        //CONTACTOS
                        intent = new Intent(Home.this, ContactList.class);
                        startActivity(intent);
                        break;

                    case 3:
                        //CHAT
                        Toast.makeText(Home.this, "Disponible en la próxima versión", Toast.LENGTH_SHORT).show();
                        break;

                    case 4:
                        //GRUPOS
                        Toast.makeText(Home.this, "Disponible sólo en la versión Premium", Toast.LENGTH_SHORT).show();
                        break;

                    case 5:
                        //MENSAJES
                        Toast.makeText(Home.this, "Disponible sólo en la versión Premium", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, MyAccount.class);
                intent.putExtra("editMode", Constants.USER_EDIT_MODE);
                startActivity(intent);
            }
        });
        
        if(!Utilities.isMyServiceRunning(getApplicationContext())){
            initializeLocation();
        }

        checkSectors();

        setUserInfo();


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
        titles.add("Presentación");
        titles.add("Búsqueda");
        titles.add("Contactos");
        titles.add("Chat");
        titles.add("Grupos");
        titles.add("Mensajes");

        return titles;
    }

    private List<Integer> initializeImages(){
        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.ic_botn_presentacin_no_text_down);
        images.add(R.drawable.ic_botn_busqueda_no_text_down);
        images.add(R.drawable.ic_botn_contactos_no_text_down);
        images.add(R.drawable.ic_botn_chat_no_text_down);
        images.add(R.drawable.ic_botn_grupos_no_text_down2);
        images.add(R.drawable.ic_botn_mensajes_no_text_down2);

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
        // Changed by DM. Update sector Always.
        //if(daoSector.loadSectors().isEmpty()){
            sectorsServer();
        //}
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

    private void setUserInfo(){
        DAOUser daoUser = new DAOUser(Home.this);
        user = daoUser.loadUser();

        userName.setText(user.getName());
        userSector.setText(user.getSector().getName());

        if(!user.getImage_url().isEmpty()){
            Utilities.profileImageServer(user.getImage_url(), profile_image, getApplicationContext());
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        setUserInfo();
    }

    //Side Menu
    private void addDrawerItems() {
        String[] osArray = { "","Presentación", "Búsqueda", "Contactos", "Chat", "Grupos", "Mensajes", "Ajustes" };
        mAdapterMenu = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapterMenu);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position) {
                    case 1:
                        //PRESENTACION
                        intent = new Intent(Home.this, Presentation.class);
                        startActivity(intent);
                        break;

                    case 2:

                        //BUSQUEDA
                        intent = new Intent(Home.this, Search.class);
                        startActivity(intent);
                        break;

                    case 3:
                        //CONTACTOS
                        intent = new Intent(Home.this, ContactList.class);
                        startActivity(intent);
                        break;

                    case 4:
                        //CHAT
                        Toast.makeText(Home.this, "Disponible en la próxima versión", Toast.LENGTH_SHORT).show();
                        break;

                    case 5:
                        //GRUPOS
                        Toast.makeText(Home.this, "Disponible sólo en la versión Premium", Toast.LENGTH_SHORT).show();
                        break;

                    case 6:
                        //MENSAJES
                        Toast.makeText(Home.this, "Disponible sólo en la versión Premium", Toast.LENGTH_SHORT).show();
                        break;

                    case 7:
                        //AJUSTES
                        intent = new Intent(Home.this, Preferences.class);
                        startActivity(intent);
                        break;
                }

            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Menú");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

}
