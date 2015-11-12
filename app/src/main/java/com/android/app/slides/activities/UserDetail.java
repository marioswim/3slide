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

    int serverResponseCode = 0;
    String upLoadServerUri = null;

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

            if(SlidesApp.getUserBitmap()!=null){
                userImg.setImageBitmap(SlidesApp.getUserBitmap());
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
                    startActivityForResult(intent, 0);
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
                // TODO: 7/11/15 el metodo getsectorbyname no funciona -- Descomentado por DM. Ahora si funciona. Ver Home line 205.
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
        if(resCode == Activity.RESULT_OK && data != null){

            upLoadServerUri = Constants.baseUrl + Constants.uploadImageURL;

            final String realPath= RealPathUtil.getPath(getApplicationContext(), data.getData());

            final Uri uri = data.getData();

            new Thread(new Runnable() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                        }
                    });

                    int result = uploadImg(realPath);

                }
            }).start();
        }
    }

    public int uploadImg(String sourceFileUri) {


        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            //dialog.dismiss();

            //Log.e("uploadFile", "Source File not exist :"
            //        +uploadFilePath + "" + uploadFileName);

            runOnUiThread(new Runnable() {
                public void run() {
                    //messageText.setText("Source File not exist :"
                    //        +uploadFilePath + "" + uploadFileName);
                }
            });

            return 0;

        } else {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);
                conn.setRequestProperty("apikey", user.getApikey());

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                //Log.i("uploadFile", "HTTP Response is : "
                //        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {

                            //String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                            //        +" http://www.androidexample.com/media/uploads/"
                            //        +uploadFileName;

                            //messageText.setText(msg);
                            Toast.makeText(UserDetail.this, "File Upload Complete.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                //dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        //messageText.setText("MalformedURLException Exception : check script url.");
                        Toast.makeText(UserDetail.this, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                //Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                //dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        //messageText.setText("Got Exception : see logcat ");
                        Toast.makeText(UserDetail.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                //Log.e("Upload file to server Exception", "Exception : "
                //        + e.getMessage(), e);
            }
            //dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }
}
