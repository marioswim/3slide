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
import com.android.app.slides.tasks.DownloadPdfTask;
import com.android.app.slides.tasks.UploadImageTask;
import com.android.app.slides.tasks.UploadPdfTask;
import com.android.app.slides.tools.Constants;
import com.android.app.slides.tools.FileDownloader;
import com.android.app.slides.tools.RealPathUtil;
import com.android.app.slides.tools.SlidesApp;
import com.android.app.slides.tools.ToastManager;
import com.android.app.slides.tools.Utilities;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;

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
    @Bind(R.id.Progress)
    ProgressBarCircularIndeterminate Progress;

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
                intent.setType("application/pdf");
                startActivityForResult(intent, REQ_UPLOAD_PDF);
            }
        });
        DownloadPptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null){
                    DownloadPdfTask downloadPdfTask = new DownloadPdfTask(Presentation.this, user.getPdf_url(), getIntent().getExtras().getString("name"));
                    downloadPdfTask.execute();
                }
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
            UploadPdfTask uploadPdfTask = new UploadPdfTask(Presentation.this, data.getData(), user.getApikey(), Progress);
            uploadPdfTask.execute();
        }
    }
}
