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
public class ContactList extends BaseActivity {
    @Bind(R.id.result_list)
    ListView resultList;
    @Bind(R.id.Progress)
    ProgressBarCircularIndeterminate Progress;

    public static String TAG = "Contact Result";


    @Override
    protected int getLayoutResource() {
        return R.layout.result_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        searchServer();
    }

    private void searchServer() {

        Progress.setVisibility(View.VISIBLE);

        StringRequest request = new StringRequest(Request.Method.GET, Constants.baseUrl + Constants.getContactsURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            if(jsonResponse != null){

                                final ArrayList<User> result = parseContacts(jsonResponse);

                                UserAdapter adapter=new UserAdapter(ContactList.this, result);

                                resultList.setAdapter(adapter);

                                resultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        /*User user = result.get(position);

                                        if(Utilities.isNetworkAvailable(ContactList.this)){
                                            Intent intent = new Intent(ContactList.this, UserDetails.class);
                                            intent.putExtra("id_emp", user.getId());
                                            intent.putExtra("name", user.getName());
                                            intent.putExtra("sector", user.getSector().getName());
                                            intent.putExtra("email", user.getEmail());
                                            intent.putExtra("phonenumber", user.getPhone());
                                            intent.putExtra("web", user.getWebsite());
                                            intent.putExtra("desc", user.getDescription());
                                            intent.putExtra("pdf", user.getPdf_url());
                                            intent.putExtra("image", user.getImage_url());
                                            startActivity(intent);
                                        }*/

                                    }
                                });

                            }else{
                                ToastManager.showToast(ContactList.this, "Ha ocurrido un error, inténtelo de nuevo más tarde");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Progress.setVisibility(View.INVISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        ToastManager.showToast(ContactList.this, "Ha ocurrido un error, inténtelo de nuevo más tarde");

                        Progress.setVisibility(View.INVISIBLE);

                        finish();


                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                DAOUser daoUser = new DAOUser(ContactList.this);
                User user = daoUser.loadUser();

                params.put("apikey", user.getApikey());

                return params;
            }
        };

        // Añadir petición a la cola
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    public ArrayList<User> parseContacts(JSONObject jsonObject) {
        ArrayList<User> contactsResult = new ArrayList<>();
        String email, name, desc, phoneNumber, web, image_url, pdf_url, sector;
        int id;

        try {

            JSONArray sectorsArray = jsonObject.getJSONArray("contactos");

            for (int i =0; i < sectorsArray.length(); i++){
                JSONObject index = sectorsArray.getJSONObject(i);
                User user = new User();

                id= index.getInt("id");
                user.setId(id);

                name = index.getString("nombre");
                if (name != null){
                    user.setName(name);
                }

                image_url = index.getString("imagen");
                if (image_url != null){
                    user.setImage_url(image_url);
                }

                contactsResult.add(user);
            }


        } catch (JSONException e) {
            Log.e(TAG, "Error de parsing: " + e.getMessage());
        }

        return contactsResult;
    }
}
