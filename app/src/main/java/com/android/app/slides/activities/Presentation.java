package com.android.app.slides.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import com.android.app.slides.tools.Constants;
import com.android.app.slides.tools.FileDownloader;
import com.android.app.slides.tools.SlidesApp;
import com.gc.materialdesign.views.ButtonRectangle;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;

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

    User user;

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
                Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fileIntent.setType("file/*"); // intent type to filter application based on your requirement
                startActivityForResult(fileIntent, RESULT_OK);
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
        }
    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
            String fileName = strings[1];  // -> maven.pdf
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
