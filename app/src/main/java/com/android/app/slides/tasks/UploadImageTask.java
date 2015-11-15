package com.android.app.slides.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.app.slides.activities.Home;
import com.android.app.slides.model.DAOSector;
import com.android.app.slides.model.DAOUser;
import com.android.app.slides.model.Sector;
import com.android.app.slides.model.User;
import com.android.app.slides.model.VolleySingleton;
import com.android.app.slides.tools.Constants;
import com.android.app.slides.tools.RealPathUtil;
import com.android.app.slides.tools.SlidesApp;
import com.android.app.slides.tools.ToastManager;
import com.android.app.slides.tools.Utilities;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by francisco on 13/11/15.
 */

public class UploadImageTask extends AsyncTask<Void, Void, Void> {

    int serverResponseCode = 0;
    Uri uploadUri;
    String upLoadUrl;
    Context context;
    String apikey;
    ProgressBarCircularIndeterminate progress;


    public UploadImageTask (Context context, Uri uri, String apikey, ProgressBarCircularIndeterminate progress){
        this.context = context;
        this.uploadUri = uri;
        this.upLoadUrl = Constants.baseUrl + Constants.uploadImageURL;
        this.apikey = apikey;
        this.progress = progress;
    }

    @Override
    protected void onPreExecute() {
        this.progress.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Void... params) {

        final String realPath= RealPathUtil.getPath(context, uploadUri);

        uploadImg(realPath);

        return (null);
    }

    @Override
    protected void onPostExecute(Void unused) {
        if(serverResponseCode == 200){
            ToastManager.showToast((Activity) context, "Subido correctamente");
            loginServer();
            SlidesApp app = new SlidesApp();
            app.setUserBitmap(null);
        }else{
            ToastManager.showToast((Activity) context, "Ha ocurrido un error, intentelo mas tarde");
        }
        this.progress.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCancelled() {
        this.progress.setVisibility(View.INVISIBLE);
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

            return -1;

        } else {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadUrl);

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
                conn.setRequestProperty("apikey", apikey);

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

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                ex.printStackTrace();

                return -1;

            } catch (Exception e) {

                e.printStackTrace();

                return -1;
            }
            return serverResponseCode;

        }
    }

    private void loginServer() {


        StringRequest request = new StringRequest(Request.Method.POST, Constants.baseUrl + Constants.loginURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            if(jsonResponse != null){
                                User user = parseLogin(jsonResponse);

                                if (user != null) {
                                    DAOUser daoUser = new DAOUser(context);
                                    daoUser.saveUser(user);
                                }
                            }else{
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                                    }

                                } catch (JSONException | UnsupportedEncodingException e) {
                                }
                            }else{
                            }
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                SharedPreferences settings = context.getSharedPreferences("Chapuza", 0);
                String name = settings.getString("name", null);
                params.put("email", settings.getString("email", ""));
                params.put("pass", Utilities.ofuscate(settings.getString("pass", "")));
                return params;
            }
        };

        // Añadir petición a la cola
        VolleySingleton.getInstance(context.getApplicationContext()).addToRequestQueue(request);
    }

    public User parseLogin(JSONObject jsonObject) {

        String apikey, name, desc, phoneNumber, web, image_url, pdf_url, sector;
        User user = null;
        int id;

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

            phoneNumber = jsonObject.getString("phonenumber");
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

            SharedPreferences settings = context.getSharedPreferences("Chapuza", 0);
            user.setEmail(settings.getString("email", ""));


        } catch (JSONException e) {
        }

        return user;
    }
}
