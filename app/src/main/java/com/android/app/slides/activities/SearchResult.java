package com.android.app.slides.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.android.app.slides.R;
import com.android.app.slides.adapters.SearchAdapter;
import com.android.app.slides.model.DAOUser;
import com.android.app.slides.model.Sector;
import com.android.app.slides.model.User;
import com.android.app.slides.model.VolleySingleton;
import com.android.app.slides.tools.Constants;
import com.android.app.slides.tools.ToastManager;
import com.android.app.slides.tools.Utilities;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by Fran on 2/11/15.
 */
public class SearchResult extends BaseActivity {
    @Bind(R.id.search_list)
    ListView searchList;

    //Search variables
    String key;
    String sector;
    int distance;

    public static String TAG = "Search Result";


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_lista_busqueda;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //searchServer();
        User user1 = new User();
        user1.setName("Seur");
        user1.setSector(new Sector(1, "Mensajería"));

        User user2 = new User();
        user2.setName("Gambrinus");
        user2.setSector(new Sector(1, "Hostelería"));

        User user3 = new User();
        user3.setName("Construcciones Manolo SA");
        user3.setSector(new Sector(1, "Construcción"));

        ArrayList<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);

        SearchAdapter adapter=new SearchAdapter(SearchResult.this, users);

        searchList.setAdapter(adapter);
    }

    private void searchServer() {

        //loginProgress.setVisibility(View.VISIBLE);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.baseUrl + Constants.searchURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            if(jsonResponse != null){

                                SearchAdapter adapter=new SearchAdapter(SearchResult.this, parseSearch(jsonResponse));

                                searchList.setAdapter(adapter);

                            }else{
                                ToastManager.showToast(SearchResult.this, "Ha ocurrido un error, inténtelo de nuevo más tarde");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //loginProgress.setVisibility(View.INVISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        ToastManager.showToast(SearchResult.this, "Ha ocurrido un error, inténtelo de nuevo más tarde");

                        //loginProgress.setVisibility(View.INVISIBLE);

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
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                DAOUser daoUser = new DAOUser(SearchResult.this);
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

        try {

            JSONArray sectorsArray = jsonObject.getJSONArray("empresas");

            for (int i =0; i < sectorsArray.length(); i++){
                JSONObject index = sectorsArray.getJSONObject(i);
                User user = new User();

                name = index.getString("name");
                if (name != null){
                    user.setName(name);
                }

                desc = index.getString("desc");
                if (desc != null){
                    user.setDescription(desc);
                }

                phoneNumber = index.getString("phoneNumber");
                if (phoneNumber != null){
                    user.setPhone(phoneNumber);
                }

                web = index.getString("web");
                if (web != null){
                    user.setWebsite(web);
                }

                image_url = index.getString("image");
                if (image_url != null){
                    user.setImage_url(image_url);
                }

                pdf_url = index.getString("pdf");
                if (pdf_url != null){
                    user.setPdf_url(pdf_url);
                }

                sector = index.getString("sector");
                if (sector != null){
                    user.setSector(new Sector(jsonObject.getInt("id_sector"), sector));
                }

                // TODO: 2/11/15 descomentar de que lo devuelva el servidor
                //email = jsonObject.getString("email");
                //if(email!=null){
                //    user.setEmail(email);
                //}

                searchResult.add(user);
            }


        } catch (JSONException e) {
            Log.e(TAG, "Error de parsing: " + e.getMessage());
        }

        return searchResult;
    }
}
