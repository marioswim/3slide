package com.android.app.slides.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.app.slides.R;
import com.android.app.slides.adapters.UserAdapter;
import com.android.app.slides.model.DAOUser;
import com.android.app.slides.model.Sector;
import com.android.app.slides.model.User;
import com.android.app.slides.model.VolleySingleton;
import com.android.app.slides.tools.Constants;
import com.android.app.slides.tools.ToastManager;
import com.android.app.slides.tools.Utilities;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by Fran on 2/11/15.
 */
public class SearchList extends BaseActivity {
    @Bind(R.id.result_list)
    ListView searchList;
    @Bind(R.id.Progress)
    ProgressBarCircularIndeterminate searchProgress;

    //Search variables
    String key;
    String sector;
    String distance;

    public static String TAG = "Search Result";


    @Override
    protected int getLayoutResource() {
        return R.layout.result_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent().getExtras()!=null){
            key= getIntent().getExtras().getString("key");
            sector = getIntent().getExtras().getString("sector");
            distance = getIntent().getExtras().getString("distance");
        }

        searchServer();
    }

    private void searchServer() {

        searchProgress.setVisibility(View.VISIBLE);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.baseUrl + Constants.searchURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            if(jsonResponse != null){

                                final ArrayList<User> searchResult = parseSearch(jsonResponse);

                                UserAdapter adapter=new UserAdapter(SearchList.this, searchResult);

                                searchList.setAdapter(adapter);

                                searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        User user = searchResult.get(position);
                                        // TODO: 6/11/15 pasarle todos los campos a la pantalla
                                        // donde se vea el perfil publico

                                        if(Utilities.isNetworkAvailable(SearchList.this)){
                                            Intent intent = new Intent(SearchList.this, UserDetails.class);
                                            intent.putExtra("id_emp", user.getId());
                                            intent.putExtra("name", user.getName());
                                            intent.putExtra("sector", user.getSector().getName());
                                            intent.putExtra("email", user.getEmail());
                                            intent.putExtra("phonenumber", user.getPhone());
                                            intent.putExtra("web", user.getWebsite());
                                            intent.putExtra("desc", user.getDescription());
                                            intent.putExtra("pdf", user.getPdf_url());
                                            startActivity(intent);
                                        }

                                    }
                                });

                            }else{
                                ToastManager.showToast(SearchList.this, "Ha ocurrido un error, inténtelo de nuevo más tarde");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        searchProgress.setVisibility(View.INVISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        ToastManager.showToast(SearchList.this, "Ha ocurrido un error, inténtelo de nuevo más tarde");

                        searchProgress.setVisibility(View.INVISIBLE);

                        finish();


                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("nombre", key);
                params.put("sector", sector);
                params.put("distancia", distance);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                DAOUser daoUser = new DAOUser(SearchList.this);
                User user = daoUser.loadUser();

                params.put("apikey", user.getApikey());

                return params;
            }
        };

        // Añadir petición a la cola
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    public ArrayList<User> parseSearch(JSONObject jsonObject) {
        ArrayList<User> searchResult = new ArrayList<>();
        String email, name, desc, phoneNumber, web, image_url, pdf_url, sector;
        int id;

        try {

            JSONArray sectorsArray = jsonObject.getJSONArray("empresas");

            for (int i =0; i < sectorsArray.length(); i++){
                JSONObject index = sectorsArray.getJSONObject(i);
                User user = new User();

                id= index.getInt("id_emp");
                user.setId(id);

                name = index.getString("name");
                if (name != null){
                    user.setName(name);
                }

                desc = index.getString("desc");
                if (desc != null){
                    user.setDescription(desc);
                }

                phoneNumber = index.getString("phonenumber");
                if (phoneNumber != null){
                    user.setPhone(phoneNumber);
                }

                web = index.getString("web");
                if (web != null){
                    user.setWebsite(web);
                }

                image_url = index.getString("imagen");
                if (image_url != null){
                    user.setImage_url(image_url);
                }

                pdf_url = index.getString("pdf");
                if (pdf_url != null){
                    user.setPdf_url(pdf_url);
                }

                sector = index.getString("sector");
                if (sector != null){
                    user.setSector(new Sector(index.getInt("id_sec"), sector));
                }

                email = index.getString("email");
                if(email!=null){
                   user.setEmail(email);
                }

                searchResult.add(user);
            }


        } catch (JSONException e) {
            Log.e(TAG, "Error de parsing: " + e.getMessage());
        }

        return searchResult;
    }
}
