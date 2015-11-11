package com.android.app.slides.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.app.slides.R;
import com.android.app.slides.tools.Constants;
import com.android.app.slides.tools.FileDownloader;
import com.gc.materialdesign.views.ButtonRectangle;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;

public class SearchResultDetails extends BaseActivity {

    @Bind(R.id.DownloadPptBtn) ButtonRectangle DownloadPptBtn;

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


    @Override
    protected int getLayoutResource() {
        return R.layout.search_result_details;
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
        }

        DownloadPptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pdfUrl = getIntent().getExtras().getString("pdf");
                new DownloadFile().execute(Constants.baseUrl + "/" + Constants.SERVER_PDFS_FOLDER + "/" + pdfUrl, pdfUrl);
            }
        });
    }

    // TODO: 10/11/15 Codigo Duplicado en Presentation.java

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
                Toast.makeText(SearchResultDetails.this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
            }


            return null;
        }
    }
}


