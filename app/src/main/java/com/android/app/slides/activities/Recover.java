package com.android.app.slides.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.android.app.slides.R;
import com.android.app.slides.model.DAOUser;
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
import com.gc.materialdesign.views.ButtonRectangle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by francisco on 26/9/15.
 */
public class Recover extends BaseActivity {
    @Bind(R.id.email)
    AutoCompleteTextView email;
    @Bind(R.id.recoverBtn)
    ButtonRectangle recoverBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkFields();

        recoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isEmailValid(email.getText().toString())) {
                    recoverServer();
                } else {
                    email.setError("Email no válido");
                }
            }
        });
    }



    @Override
    protected int getLayoutResource() {
        return R.layout.recover;
    }

    private void checkFields(){
        recoverBtn.setEnabled(false);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()>0){
                    recoverBtn.setEnabled(true);
                }else{
                    recoverBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void recoverServer() {

        Utilities.hideKeyboard(Recover.this);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.baseUrl + Constants.recoverURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        ToastManager.showToast(Recover.this, "Se le ha enviado un correo electrónico con su nueva contraseña");
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
                                        ToastManager.showToast(Recover.this, Utilities.getErrorMsgById(errorNo));
                                    }

                                } catch (JSONException | UnsupportedEncodingException e) {
                                    ToastManager.showToast(Recover.this, "Ha ocurrido un error, inténtelo de nuevo más tarde");
                                }
                            }else{
                                ToastManager.showToast(Recover.this, "Ha ocurrido un error, inténtelo de nuevo más tarde");
                            }
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("email", email.getText().toString());
                return params;
            }
        };

        // Añadir petición a la cola
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}
