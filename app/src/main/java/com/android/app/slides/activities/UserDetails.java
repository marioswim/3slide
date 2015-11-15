package com.android.app.slides.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.app.slides.R;
import com.android.app.slides.model.DAOSector;
import com.android.app.slides.model.DAOUser;
import com.android.app.slides.model.Sector;
import com.android.app.slides.model.User;
import com.android.app.slides.model.VolleySingleton;
import com.android.app.slides.tasks.DownloadImageTask;
import com.android.app.slides.tasks.DownloadPdfTask;
import com.android.app.slides.tools.Constants;
import com.android.app.slides.tools.FileDownloader;
import com.android.app.slides.tools.ToastManager;
import com.android.app.slides.tools.Utilities;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gc.materialdesign.views.ButtonRectangle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

public class UserDetails extends BaseActivity {

    @Bind(R.id.DownloadPptBtn2)
    ButtonRectangle DownloadPptBtn;

    @Bind(R.id.userName)
    TextView userName;
    @Bind(R.id.userSector)
    TextView userSector;
    @Bind(R.id.userEmail)
    TextView userEmail;
    @Bind(R.id.userPhone)
    TextView userPhone;
    @Bind(R.id.userWeb)
    TextView userWeb;
    @Bind(R.id.userDesc)
    TextView userDesc;

    @Bind(R.id.actionCall)
    ImageButton actionCall;
    @Bind(R.id.actionMail)
    ImageButton actionMail;
    @Bind(R.id.actionFriend)
    ImageButton actionFriend;


    @Override
    protected int getLayoutResource() {
        return R.layout.user_details;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent().getExtras()!=null){
            userName.setText(getIntent().getExtras().getString("name"));
            userSector.setText(getIntent().getExtras().getString("sector"));
            userEmail.setText(getIntent().getExtras().getString("email"));
            userPhone.setText(getIntent().getExtras().getString("phonenumber"));
            userWeb.setText(getIntent().getExtras().getString("web"));
            userDesc.setText(getIntent().getExtras().getString("desc"));

            DownloadPptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!getIntent().getExtras().getString("pdf").isEmpty()){
                        String pdfUrl = getIntent().getExtras().getString("pdf");
                        DownloadPdfTask downloadPdfTask = new DownloadPdfTask(UserDetails.this, pdfUrl, getIntent().getExtras().getString("name"));
                        downloadPdfTask.execute();
                    }
                }
            });

            View.OnClickListener actionsListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.actionCall:
                            if(!getIntent().getExtras().getString("phonenumber").isEmpty()){
                                Utilities.makeCall(UserDetails.this, getIntent().getExtras().getString("phonenumber"));
                            }
                            break;
                        case R.id.actionMail:
                            if(!getIntent().getExtras().getString("email").isEmpty()){
                                Utilities.makeEmail(UserDetails.this, getIntent().getExtras().getString("email"), getIntent().getExtras().getString("name"));
                            }
                            break;
                        case R.id.actionFriend:
                            addContactServer();
                            break;
                        default:
                            break;
                    }
                }
            };

            actionCall.setOnClickListener(actionsListener);
            actionMail.setOnClickListener(actionsListener);
            actionFriend.setOnClickListener(actionsListener);


        } else {
            ToastManager.showToast(UserDetails.this, "Ha ocurrido un error intentelo de nuevo mas tarder");
            finish();
        }


    }

    private void addContactServer() {

        StringRequest request = new StringRequest(Request.Method.POST, Constants.baseUrl + Constants.addContactURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        ToastManager.showToast(UserDetails.this, "Añadido a contactos");
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
                                        ToastManager.showToast(UserDetails.this, Utilities.getErrorMsgById(errorNo));
                                    }

                                } catch (JSONException | UnsupportedEncodingException e) {
                                    ToastManager.showToast(UserDetails.this, "Ha ocurrido un error, inténtelo de nuevo más tarde");
                                }
                            }else{
                                ToastManager.showToast(UserDetails.this, "Ha ocurrido un error, inténtelo de nuevo más tarde");
                            }
                        }
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("id_emp", getIntent().getExtras().getInt("id_emp") +"");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                DAOUser daoUser = new DAOUser(UserDetails.this);
                User user = daoUser.loadUser();

                params.put("apikey", user.getApikey());

                return params;
            }
        };
        // Añadir petición a la cola
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

}


