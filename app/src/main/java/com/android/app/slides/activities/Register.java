package com.android.app.slides.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.android.app.slides.R;
import com.android.app.slides.model.DAOUser;
import com.android.app.slides.model.User;
import com.android.app.slides.tools.Constants;
import com.android.app.slides.tools.DialogManager;
import com.android.app.slides.tools.ToastManager;
import com.android.app.slides.tools.Utilities;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

public class Register extends BaseActivity {
    @Bind(R.id.name)
    EditText name;
    @Bind(R.id.email)
    EditText email;
    @Bind(R.id.email2)
    EditText email2;
    @Bind(R.id.password)
    EditText password;
    @Bind(R.id.registerBtn)
    ButtonRectangle registerBtn;
    @Bind(R.id.termsCheckBox)
    CheckBox termsCheckBox;
    @Bind(R.id.termsText)
    TextView termsText;
    @Bind(R.id.registerProgress)
    ProgressBarCircularIndeterminate registerProgress;

    private RequestQueue requestQueue;
    public static String TAG = "Register";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkFields();

        termsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogManager.showDialog(Register.this, "Términos de usuario", "1- Blabla\n2- CLacla");
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean go = true;
                if(!Utilities.isEmailValid(email.getText().toString())){
                    email.setError("Email no válido");
                    go = false;
                }
                if (!email.getText().toString().equals(email2.getText().toString())){
                    email2.setError("No conciden los emails");
                    go = false;
                }

                if(go){
                    registerServer();
                }
            }
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.register;
    }

    private void checkFields(){

        registerBtn.setEnabled(false);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (completedFields()){
                    registerBtn.setEnabled(true);
                }else{
                    registerBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        name.addTextChangedListener(watcher);
        email.addTextChangedListener(watcher);
        email2.addTextChangedListener(watcher);
        password.addTextChangedListener(watcher);

        termsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (completedFields()){
                    registerBtn.setEnabled(true);
                }else{
                    registerBtn.setEnabled(false);
                }
            }
        });
    }

    private boolean completedFields(){
        if(name.getText().length()>0 && email.getText().length()>0 && email2.getText().length()>0 && password.getText().length()>0 && termsCheckBox.isChecked()){
            return true;
        }else{
            return false;
        }
    }

    private void registerServer(){

        registerProgress.setVisibility(View.VISIBLE);

        Utilities.hideKeyboard(Register.this);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.baseUrl + Constants.registerURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            if(jsonResponse != null){
                                User user = parseRegister(jsonResponse);

                                if (user != null) {
                                    DAOUser daoUser = new DAOUser(Register.this);
                                    daoUser.saveUser(user);

                                    Intent i = new Intent(Register.this, Home.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                }
                            }else{
                                ToastManager.showToast(Register.this, "Ha ocurrido un error, inténtelo de nuevo más tarde");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        registerProgress.setVisibility(View.INVISIBLE);
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
                                        ToastManager.showToast(Register.this, Utilities.getErrorMsgById(errorNo));
                                    }

                                } catch (JSONException | UnsupportedEncodingException e) {
                                    ToastManager.showToast(Register.this, "Ha ocurrido un error, inténtelo de nuevo más tarde");
                                }
                            }else{
                                ToastManager.showToast(Register.this, "Ha ocurrido un error, inténtelo de nuevo más tarde");
                            }
                        }

                        registerProgress.setVisibility(View.INVISIBLE);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("email", email.getText().toString());
                params.put("pass", password.getText().toString());
                return params;
            }
        };

        // Añadir petición a la cola
        requestQueue.add(request);

    }

    private User parseRegister(JSONObject jsonObject){

        String apikey;
        User user = null;

        try {

            user = new User();

            apikey = jsonObject.getString("apikey");
            if (apikey != null) {
                user.setApikey(apikey);
            }

            user.setEmail(email.getText().toString());
            user.setName(name.getText().toString());

        }catch (JSONException e){
            Log.e(TAG, "Error de parsing: " + e.getMessage());
        }

        return user;

    }
}

