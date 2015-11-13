package com.android.app.slides.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.app.slides.R;
import com.android.app.slides.model.DAOSector;
import com.android.app.slides.model.DAOUser;
import com.android.app.slides.model.Sector;
import com.android.app.slides.model.User;
import com.android.app.slides.model.VolleySingleton;
import com.android.app.slides.tasks.DownloadImageTask;
import com.android.app.slides.tasks.UploadImageTask;
import com.android.app.slides.tools.Constants;
import com.android.app.slides.tools.DialogManager;
import com.android.app.slides.tools.RealPathUtil;
import com.android.app.slides.tools.SlidesApp;
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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Target;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by francisco on 26/9/15.
 */
public class UserDetail extends BaseActivity {
    @Bind(R.id.userName)
    EditText userName;
    @Bind(R.id.userSector)
    Spinner userSector;
    @Bind(R.id.userTlf)
    EditText userTlf;
    @Bind(R.id.userWeb)
    EditText userWeb;
    @Bind(R.id.userEmail)
    EditText userEmail;
    @Bind(R.id.userDesc)
    EditText userDesc;
    @Bind(R.id.userImg)
    ImageView userImg;

    @Bind(R.id.saveBtn)
    ButtonRectangle saveBtn;

    User user;
    private boolean hasEdited = false;
    private final int REQ_UPLOAD_IMG = 1;

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
            if (!user.getPhone().isEmpty()) {
                userTlf.setText(user.getPhone());
            }
            if (!user.getWebsite().isEmpty()) {
                userWeb.setText(user.getWebsite());
            }
            if (!user.getDescription().isEmpty()) {
                userDesc.setText(user.getDescription());
            }

            userEmail.setText(user.getEmail());

            DAOSector daoSector = new DAOSector(UserDetail.this);
            ArrayList<Sector> sectors = daoSector.loadSectors();
            ArrayAdapter sectorAdapter = new ArrayAdapter(UserDetail.this, android.R.layout.simple_spinner_item);

            for(Sector sector : sectors){
                sectorAdapter.add(sector.getName());
            }

            userSector.setAdapter(sectorAdapter);

            if (!user.getSector().getName().isEmpty()) {
                int spinnerPosition3 = sectorAdapter.getPosition(user.getSector().getName());
                userSector.setSelection(spinnerPosition3);
            }

            if(!user.getImage_url().isEmpty()){
                Utilities.profileImageServer(user.getImage_url(), userImg, getApplicationContext());
            }


        }
    }

    private void editMode() {
        if (getIntent().getExtras() != null) {
            int editMode = getIntent().getExtras().getInt("editMode", Constants.USER_VIEW_MODE);

            if (editMode == Constants.USER_EDIT_MODE) {
                showEditOptions();
            }

            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hasEdited) {
                        if (Utilities.isNetworkAvailable(UserDetail.this)) {
                            saveUserInfoServer();

                            //Save in local
                            user.setName(userName.getText().toString());
                            user.setDescription(userDesc.getText().toString());
                            user.setPhone(userTlf.getText().toString());
                            user.setWebsite(userWeb.getText().toString());
                            user.setSector(DAOSector.getSectorByName(userSector.getItemAtPosition(userSector.getSelectedItemPosition()).toString()));
                            DAOUser daoUser = new DAOUser(UserDetail.this);
                            daoUser.saveUser(user);
                        }
                    } else {
                        finish();
                    }
                }
            });

            userImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 1. on Upload click call ACTION_GET_CONTENT intent
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    // 2. pick image only
                    intent.setType("image/*");
                    // 3. start activity
                    startActivityForResult(intent, REQ_UPLOAD_IMG);
                }
            });
        }
    }

    private void showEditOptions() {

        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hasEdited = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        userName.addTextChangedListener(tw);
        userDesc.addTextChangedListener(tw);
        userEmail.addTextChangedListener(tw);
        userTlf.addTextChangedListener(tw);
        userWeb.addTextChangedListener(tw);

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
                if(userSector.getItemAtPosition(userSector.getSelectedItemPosition())!=null){
                    Sector sector = DAOSector.getSectorByName(userSector.getItemAtPosition(userSector.getSelectedItemPosition()).toString());
                    if (sector!=null){
                        params.put("id_sector", sector.getId() + "");
                    }
                }

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

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        if(reqCode == REQ_UPLOAD_IMG && resCode == Activity.RESULT_OK && data != null){
            UploadImageTask uploadImageTask = new UploadImageTask(getApplicationContext(), data.getData(), user.getApikey());
            uploadImageTask.execute();
        }
    }
}
