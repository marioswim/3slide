package com.android.app.slides.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.app.slides.R;
import com.android.app.slides.model.DAOSector;
import com.android.app.slides.model.DAOUser;
import com.android.app.slides.model.Sector;
import com.android.app.slides.model.User;
import com.android.app.slides.tasks.UploadImageTask;
import com.android.app.slides.tasks.UploadPdfTask;
import com.android.app.slides.tools.Constants;
import com.android.app.slides.tools.FileDownloader;
import com.android.app.slides.tools.RealPathUtil;
import com.android.app.slides.tools.SlidesApp;
import com.android.app.slides.tools.ToastManager;
import com.android.app.slides.tools.Utilities;
import com.gc.materialdesign.views.ButtonRectangle;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.Bind;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by david on 6/11/15.
 */
public class Presentation extends BaseActivity {
    @Bind(R.id.UploadPptBtn) ButtonRectangle UploadPptBtn;
    @Bind(R.id.DownloadPptBtn) ButtonRectangle DownloadPptBtn;

    @Bind(R.id.userName)
    TextView userName;
    @Bind(R.id.userSector)
    TextView userSector;
    @Bind(R.id.userEmail)
    TextView userEmail;
    @Bind(R.id.userWeb)
    TextView userWeb;
    @Bind(R.id.userPhone)
    TextView userPhone;
    @Bind(R.id.profile_image)
    CircleImageView profile_image;

    User user;

    private final int REQ_UPLOAD_PDF = 1;

    @Override
    protected int getLayoutResource() {
        return R.layout.presentation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUserInfo();

        UploadPptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, REQ_UPLOAD_PDF);
            }
        });
        DownloadPptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DownloadFile().execute(Constants.baseUrl + "/" + Constants.SERVER_PDFS_FOLDER + "/" + user.getPdf_url(), user.getPdf_url());
            }
        });

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
            if (!user.getEmail().isEmpty()) {
                userEmail.setText(user.getEmail());
            }
            if (!user.getWebsite().isEmpty()) {
                userWeb.setText(user.getWebsite());
            }
            if (!user.getPhone().isEmpty()) {
                userPhone.setText(user.getPhone());
            }

            if(!user.getImage_url().isEmpty()){
                Utilities.profileImageServer(user.getImage_url(), profile_image, getApplicationContext());
            }
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        if(reqCode == REQ_UPLOAD_PDF && resCode == Activity.RESULT_OK && data != null){
            UploadPdfTask uploadPdfTask = new UploadPdfTask(getApplicationContext(), data.getData(), user.getApikey());
            uploadPdfTask.execute();
        }
    }


    // TODO: 10/11/15 Codigo Duplicado en SearchResultsDetails.java

    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];
            String fileName = strings[1];
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, Constants.LOCAL_PDFS_FOLDER);
            folder.mkdir();

            File pdfFile = new File(folder ,fileName);

            try{
                pdfFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);

            Uri path = Uri.fromFile(pdfFile);
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(path, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try{
                startActivity(pdfIntent);
            }catch(ActivityNotFoundException e){
                Toast.makeText(Presentation.this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
            }


            return null;
        }
    }
}
