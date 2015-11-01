package com.android.app.slides.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.app.slides.R;
import com.android.app.slides.model.DAOUser;
import com.android.app.slides.model.User;
import com.android.app.slides.model.VolleySingleton;
import com.android.app.slides.tools.Constants;
import com.android.app.slides.tools.DialogManager;
import com.android.app.slides.tools.ToastManager;
import com.android.app.slides.tools.Utilities;
import com.android.volley.AuthFailureError;
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
public class UserDetail extends BaseActivity {
    @Bind(R.id.userName)
    TextView userName;
    @Bind(R.id.userSector)
    TextView userSector;
    @Bind(R.id.userTlf)
    TextView userTlf;
    @Bind(R.id.userWeb)
    TextView userWeb;
    @Bind(R.id.userDesc)
    TextView userDesc;
    @Bind(R.id.userImg)
    ImageView userImg;

    @Bind(R.id.userNameConf)
    LinearLayout userNameConf;
    @Bind(R.id.userSectorConf)
    LinearLayout userSectorConf;
    @Bind(R.id.userTlfConf)
    LinearLayout userTlfConf;
    @Bind(R.id.userWebConf)
    LinearLayout userWebConf;
    @Bind(R.id.userImgConf)
    LinearLayout userImgConf;
    @Bind(R.id.userDescConf)
    LinearLayout userDescConf;

    @Bind(R.id.saveBtn)
    Button saveBtn;

    User user;
    private boolean hasEdited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        editMode();

        setUserInfo();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.user_detail;
    }

    private void setUserInfo() {
        DAOUser daoUser = new DAOUser(this);
        user = daoUser.loadUser();

        if (user != null) {
            if (!user.getName().isEmpty()) {
                userName.setText(user.getName());
            }
            if (!user.getSector().getName().isEmpty()) {
                userSector.setText(user.getSector().getName());
            }
            if (!user.getPhone().isEmpty()) {
                userTlf.setText(user.getPhone());
            }
            if (!user.getWebsite().isEmpty()) {
                userWeb.setText(user.getWebsite());
            }
            if (!user.getDescription().isEmpty()) {
                userDesc.setText(user.getDescription());
            }

        }
    }

    private void editMode() {
        if (getIntent().getExtras() != null) {
            int editMode = getIntent().getExtras().getInt("editMode", Constants.USER_VIEW_MODE);

            if (editMode == Constants.USER_EDIT_MODE) {
                showEditOptions();

                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (hasEdited) {
                            saveUserInfoServer();
                        } else {
                            finish();
                        }
                    }
                });

                View.OnClickListener editListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.userNameConf:
                                DialogManager.showDialog(UserDetail.this, "Editar nombre", userName.getText().toString());
                                break;
                            case R.id.userSectorConf:
                                //Mostrar dialog con lista
                                break;
                            case R.id.userTlfConf:
                                DialogManager.showDialog(UserDetail.this, "Editar teléfono", userTlf.getText().toString());
                                break;
                            case R.id.userWebConf:
                                DialogManager.showDialog(UserDetail.this, "Editar website", userWeb.getText().toString());
                                break;
                            case R.id.userDescConf:
                                DialogManager.showDialog(UserDetail.this, "Editar descripción", userDesc.getText().toString());
                                break;
                        }
                        hasEdited = true;
                    }
                };

                userNameConf.setOnClickListener(editListener);
                userSectorConf.setOnClickListener(editListener);
                userTlfConf.setOnClickListener(editListener);
                userWebConf.setOnClickListener(editListener);
                userDescConf.setOnClickListener(editListener);
            }
        }
    }

    private void showEditOptions() {
        userNameConf.setVisibility(View.VISIBLE);
        userSectorConf.setVisibility(View.VISIBLE);
        userTlfConf.setVisibility(View.VISIBLE);
        userWebConf.setVisibility(View.VISIBLE);
        userImgConf.setVisibility(View.VISIBLE);
        saveBtn.setVisibility(View.VISIBLE);
    }

    private void saveUserInfoServer() {

        Utilities.hideKeyboard(UserDetail.this);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.baseUrl + Constants.updateProfileURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        ToastManager.showToast(UserDetail.this, "Ha ocurrido un error, inténtelo de nuevo más tarde");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("nombre", userName.getText().toString());
                params.put("descripcion", userDesc.getText().toString());
                params.put("telefono", userTlf.getText().toString());
                params.put("web", userWeb.getText().toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                params.put("apikey", user.getApikey());

                return params;
            }
        };

        // Añadir petición a la cola
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

    }
}
