package com.android.app.slides.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.android.app.slides.R;
import com.android.app.slides.model.DAOUser;
import com.android.app.slides.model.Sector;
import com.android.app.slides.model.User;
import com.android.app.slides.model.VolleySingleton;
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
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

public class Login extends BaseActivity {
    @Bind(R.id.email)
    AutoCompleteTextView email;
    @Bind(R.id.password)
    EditText password;
    @Bind(R.id.sign_in_button)
    ButtonRectangle sign_in_button;
    @Bind(R.id.forgotten)
    ButtonFlat forgotten;
    @Bind(R.id.loginProgress)
    ProgressBarCircularIndeterminate loginProgress;

    public static String TAG = "Login";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkFields();

        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utilities.isEmailValid(email.getText().toString())) {
                    loginServer();
                } else {
                    email.setError("Email no válido");
                }

            }
        });

        forgotten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Recover.class);
                startActivity(intent);
            }
        });

    }

    public void checkFields() {

        sign_in_button.setEnabled(false);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (completedFields()) {
                    sign_in_button.setEnabled(true);
                } else {
                    sign_in_button.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        email.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.login;
    }

    private boolean completedFields() {
        if (email.getText().length() > 0 && password.getText().length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    private void loginServer() {

        loginProgress.setVisibility(View.VISIBLE);

        Utilities.hideKeyboard(Login.this);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.baseUrl + Constants.loginURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            if(jsonResponse != null){
                                User user = parseLogin(jsonResponse);

                                if (user != null) {
                                    DAOUser daoUser = new DAOUser(Login.this);
                                    daoUser.saveUser(user);

                                    Intent i = new Intent(Login.this, Home.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.putExtra("firstTime", true);
                                    startActivity(i);
                                }
                            }else{
                                ToastManager.showToast(Login.this, "Ha ocurrido un error, inténtelo de nuevo más tarde");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        loginProgress.setVisibility(View.INVISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null) {
                            if (networkResponse.statusCode == 500){
                                try {
                                    String responseBody = new String(error.networkResponse.data, "utf-8");
                                    JSONObject jsonObject = new JSONObject(responseBody);
                                    if (jsonObject!=null){
                                        int errorNo = jsonObject.getInt("errno");
                                        ToastManager.showToast(Login.this, Utilities.getErrorMsgById(errorNo));
                                    }

                                } catch (JSONException | UnsupportedEncodingException e) {
                                    ToastManager.showToast(Login.this, "Ha ocurrido un error, inténtelo de nuevo más tarde");
                                }
                            }else{
                                ToastManager.showToast(Login.this, "Ha ocurrido un error, inténtelo de nuevo más tarde");
                            }
                        }

                        loginProgress.setVisibility(View.INVISIBLE);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("email", email.getText().toString());
                params.put("pass", Utilities.ofuscate(password.getText().toString()));
                return params;
            }
        };

        // Añadir petición a la cola
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    public User parseLogin(JSONObject jsonObject) {

        String apikey, name, desc, phoneNumber, web, image_url, pdf_url, sector;
        User user = null;

        try {

            user = new User();

            apikey = jsonObject.getString("apikey");
            if (apikey != null){
                user.setApikey(apikey);
            }

            name = jsonObject.getString("name");
            if (name != null){
                user.setName(name);
            }

            desc = jsonObject.getString("desc");
            if (desc != null){
                user.setDescription(desc);
            }

            phoneNumber = jsonObject.getString("phoneNumber");
            if (phoneNumber != null){
                user.setPhone(phoneNumber);
            }

            web = jsonObject.getString("web");
            if (web != null){
                user.setWebsite(web);
            }

            image_url = jsonObject.getString("imagen");
            if (image_url != null){
                user.setImage_url(image_url);
            }

            pdf_url = jsonObject.getString("pdf");
            if (pdf_url != null){
                user.setPdf_url(pdf_url);
            }

            sector = jsonObject.getString("sector");
            if (sector != null){
                user.setSector(new Sector(jsonObject.getInt("id_sector"), sector));
            }

            user.setEmail(email.getText().toString());


        } catch (JSONException e) {
            Log.e(TAG, "Error de parsing: " + e.getMessage());
        }

        return user;
    }
}

