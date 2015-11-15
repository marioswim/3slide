package com.android.app.slides.tasks;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.android.app.slides.tools.Constants;
import com.android.app.slides.tools.FileDownloader;

import java.io.File;
import java.io.IOException;

/**
 * Created by francisco on 15/11/15.
 */
public class DownloadPdfTask extends AsyncTask<Void, Void, Void> {
    Context context;
    String url, filename;

    public DownloadPdfTask(Context context, String url, String filename){
        this.context = context;
        this.url = url;
        this.filename = filename;
    }

    @Override
    protected Void doInBackground(Void... unused) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File folder = new File(extStorageDirectory, Constants.LOCAL_PDFS_FOLDER);
        folder.mkdir();

        File pdfFile = new File(folder ,filename);

        try{
            pdfFile.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }
        FileDownloader.downloadFile(url, pdfFile);

        Uri path = Uri.fromFile(pdfFile);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try{
            context.startActivity(pdfIntent);
        }catch(ActivityNotFoundException e){
            Toast.makeText(context, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
        }


        return null;
    }
}